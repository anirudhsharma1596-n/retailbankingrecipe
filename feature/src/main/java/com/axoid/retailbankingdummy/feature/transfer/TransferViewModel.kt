package com.axoid.retailbankingdummy.feature.transfer

// In :feature:transfer/src/main/java/com/axoid/retailbankingdummy/feature/transfer/TransferViewModel.kt


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.axoid.retailbankingdummy.feature.dashboard.DatabaseProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.UUID

class TransferViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(TransferState())
    val uiState = _uiState.asStateFlow()

    private val repository: TransferRepository

    init {
        // Initialize the repository with the DAO from our encrypted DB
        val accountDao = DatabaseProvider.getInstance(application).accountDao()
        repository = TransferRepository(accountDao)

        // Start observing the available accounts from the local database
        repository.getAccounts()
            .onEach { accounts ->
                _uiState.update { it.copy(availableAccounts = accounts) }
            }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: TransferIntent) {
        when (intent) {
            is TransferIntent.SelectFromAccount -> _uiState.update { it.copy(fromAccount = intent.account) }
            is TransferIntent.SelectToAccount -> _uiState.update { it.copy(toAccount = intent.account) }
            is TransferIntent.SetAmount -> handleAmountChange(intent.amount)
            is TransferIntent.SubmitTransfer -> beginTransferProcess()
            is TransferIntent.CloseDialog -> {
                // Reset the process state to Idle
                _uiState.update { it.copy(processState = ProcessState.Idle) }
            }
            // Other intents will be handled later
            else -> {}
        }
    }

    private fun handleAmountChange(amountString: String) {
        val amount = amountString.toBigDecimalOrNull() ?: BigDecimal.ZERO
        _uiState.update {
            it.copy(
                amountInput = amountString,
                amount = amount
            )
        }
    }

    private fun beginTransferProcess() {
        viewModelScope.launch {
            // --- IDEMPOTENCY KEY GENERATION ---
            // If we are not already in a submitting or failed state, generate a new key.
            // This ensures that a simple validation failure doesn't burn a key.
            if (uiState.value.transferId == null || uiState.value.processState is ProcessState.Success) {
                _uiState.update { it.copy(transferId = UUID.randomUUID().toString()) }
            }
            // Now, the state holds the unique idempotency key for this entire transaction flow.

            // 1. Validation State
            _uiState.update { it.copy(processState = ProcessState.ValidatingInput()) }
            val from = uiState.value.fromAccount
            val to = uiState.value.toAccount
            val amount = uiState.value.amount

            if (from == null || to == null) {
                _uiState.update { it.copy(processState = ProcessState.PermanentFailure("Select both accounts.")) }
                return@launch
            }
            if (amount <= BigDecimal.ZERO) {
                _uiState.update { it.copy(processState = ProcessState.PermanentFailure("Enter a valid amount.")) }
                return@launch
            }
            if (from.balance < amount) {
                _uiState.update { it.copy(processState = ProcessState.PermanentFailure("Insufficient funds.")) }
                return@launch
            }

            // 2. Calculating Fees State
            _uiState.update { it.copy(processState = ProcessState.CalculatingFees) }
            val fee = repository.calculateFees(amount, from)
            _uiState.update { it.copy(calculatedFee = fee, processState = ProcessState.ReadyToSubmit) }

            // 3. Submitting State
            // The user will see the "ReadyToSubmit" state on a confirmation dialog.
            // When they confirm, we will move to the Submitting state.
            // For now, let's trigger it directly to illustrate the flow.
            submit()
        }
    }

    private fun submit() {
        viewModelScope.launch {
            _uiState.update { it.copy(processState = ProcessState.Submitting) }

            val currentState = uiState.value
            val idempotencyKey = currentState.transferId ?: return@launch // Should never be null here

            try {
                repository.submitTransfer(
                    fromAccount = currentState.fromAccount!!,
                    toAccount = currentState.toAccount!!,
                    amount = currentState.amount,
                    idempotencyKey = idempotencyKey
                )
                _uiState.update { it.copy(processState = ProcessState.Success("Transfer complete!")) }

            } catch (e: Exception) {
                // This is where we'd handle a simulated network timeout.
                // The state is now TransientFailure, but the idempotencyKey is preserved.
                _uiState.update { it.copy(processState = ProcessState.TransientFailure("Network timeout. Please try again.")) }
            }
        }
    }

    fun retryTransfer() {
        // When the user hits "Retry", we simply call submit() again.
        // The ViewModel will use the *same idempotencyKey* that is already in the state.
        submit()
    }
}
