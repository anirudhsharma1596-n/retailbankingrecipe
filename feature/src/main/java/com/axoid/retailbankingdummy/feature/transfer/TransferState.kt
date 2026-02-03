package com.axoid.retailbankingdummy.feature.transfer

// In :feature:transfer/src/main/java/com/axoid/retailbankingdummy/feature/transfer/TransferState.kt



import com.axoid.retailbankingdummy.core.model.Account
import java.math.BigDecimal

/**
 * Represents all possible user actions (Intents) on the transfer screen.
 */
sealed interface TransferIntent {
    data class SelectFromAccount(val account: Account) : TransferIntent
    data class SelectToAccount(val account: Account) : TransferIntent
    data class SetAmount(val amount: String) : TransferIntent
    data object SubmitTransfer : TransferIntent
    data object Retry : TransferIntent
    data class SubmitTwoFactorAuth(val code: String) : TransferIntent
    data object CloseDialog : TransferIntent
}

/**
 * Represents the complete state of the transfer screen at any given time.
 * This is an immutable data class.
 */
data class TransferState(
    // Data properties
    val availableAccounts: List<Account> = emptyList(),
    val fromAccount: Account? = null,
    val toAccount: Account? = null,
    val amount: BigDecimal = BigDecimal.ZERO,
    val amountInput: String = "",
    val calculatedFee: BigDecimal = BigDecimal.ZERO,
    val transferId: String? = null, // To track the idempotent transaction

    // UI State properties
    val processState: ProcessState = ProcessState.Idle
)

/**
 * A sealed interface representing the current step or status of the transfer process.
 * This is the state machine as defined in the project specs.
 */
sealed interface ProcessState {
    data object Idle : ProcessState // The initial state, ready for input.

    data class ValidatingInput(val message: String? = null) : ProcessState // Checking if accounts/amount are valid.

    data object CalculatingFees : ProcessState // Simulating a network call to get fees.

    data object ReadyToSubmit : ProcessState // All input is valid, fee is calculated.

    data object Submitting : ProcessState // The transfer is in-flight to the server.

    data object AwaitingTwoFactorAuth : ProcessState // Simulating a 2FA requirement.

    data class Success(val message: String) : ProcessState // The transfer is complete.

    data class PermanentFailure(val reason: String) : ProcessState // An unrecoverable error (e.g., insufficient funds).

    data class TransientFailure(val reason: String) : ProcessState // A recoverable error (e.g., network timeout).
}
