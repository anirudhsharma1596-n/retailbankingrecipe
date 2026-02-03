package com.axoid.retailbankingdummy.feature.transfer

// In :feature:transfer/src/main/java/com/axoid/retailbankingdummy/feature/transfer/TransferScreen.kt


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(
    transferViewModel: TransferViewModel = viewModel()
) {
    val uiState by transferViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Move Money") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp) // Adds space between items
        ) {
            // From Account Selector
            AccountSelector(
                label = "From Account",
                selectedAccount = uiState.fromAccount,
                allAccounts = uiState.availableAccounts,
                onAccountSelected = { account ->
                    transferViewModel.onIntent(TransferIntent.SelectFromAccount(account))
                }
            )

            // To Account Selector
            AccountSelector(
                label = "To Account",
                selectedAccount = uiState.toAccount,
                // Filter out the selected "from" account so you can't transfer to yourself
                allAccounts = uiState.availableAccounts.filter { it.id != uiState.fromAccount?.id },
                onAccountSelected = { account ->
                    transferViewModel.onIntent(TransferIntent.SelectToAccount(account))
                }
            )

            // Amount Input
            AmountInput(
                amountInput = uiState.amountInput,
                onAmountChange = { amountString ->
                    transferViewModel.onIntent(TransferIntent.SetAmount(amountString))
                }
            )

            Spacer(Modifier.height(8.dp)) // Extra space before button

            // Submit Button
            SubmitButton(
                processState = uiState.processState,
                onSubmit = {
                    transferViewModel.onIntent(TransferIntent.SubmitTransfer)
                }
            )

            // Activate the dialog system
            ProcessStateDialog(
                processState = uiState.processState,
                onIntent = transferViewModel::onIntent
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSelector(
    label: String,
    selectedAccount: com.axoid.retailbankingdummy.core.model.Account?,
    allAccounts: List<com.axoid.retailbankingdummy.core.model.Account>,
    onAccountSelected: (com.axoid.retailbankingdummy.core.model.Account) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedAccount?.let { "${it.accountType} (...${it.accountNumber.takeLast(4)})" } ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor() // This is important
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            allAccounts.forEach { account ->
                DropdownMenuItem(
                    text = { Text("${account.accountType} - $${"%.2f".format(account.balance)}") },
                    onClick = {
                        onAccountSelected(account)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun AmountInput(
    amountInput: String,
    onAmountChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = amountInput,
        onValueChange = onAmountChange,
        label = { Text("Amount") },
        prefix = { Text("$") },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun SubmitButton(
    processState: ProcessState,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    // The button is disabled if a transfer is already in progress.
    val isEnabled = processState is ProcessState.Idle || processState is ProcessState.ReadyToSubmit

    Button(
        onClick = onSubmit,
        enabled = isEnabled,
        modifier = modifier.fillMaxWidth()
    ) {
        // Show a progress indicator when in a loading state
        if (processState is ProcessState.ValidatingInput ||
            processState is ProcessState.CalculatingFees ||
            processState is ProcessState.Submitting
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text("Review Transfer")
        }
    }
}

@Composable
fun ProcessStateDialog(
    processState: ProcessState,
    onIntent: (TransferIntent) -> Unit
) {
    when (processState) {
        // Show a confirmation dialog when fees are calculated
        is ProcessState.ReadyToSubmit -> {
            ConfirmationDialog(
                onConfirm = { onIntent(TransferIntent.SubmitTransfer) },
                onDismiss = { onIntent(TransferIntent.CloseDialog) }
            )
        }

        // Show a success dialog
        is ProcessState.Success -> {
            ResultDialog(
                title = "Success!",
                message = processState.message,
                onDismiss = { onIntent(TransferIntent.CloseDialog) }
            )
        }

        // Show a permanent failure dialog (non-recoverable)
        is ProcessState.PermanentFailure -> {
            ResultDialog(
                title = "Error",
                message = processState.reason,
                onDismiss = { onIntent(TransferIntent.CloseDialog) }
            )
        }

        // Show a transient failure dialog (recoverable, with a Retry option)
        is ProcessState.TransientFailure -> {
            TransientFailureDialog(
                reason = processState.reason,
                onRetry = { onIntent(TransferIntent.Retry) },
                onDismiss = { onIntent(TransferIntent.CloseDialog) }
            )
        }

        // For all other states (Idle, Submitting, etc.), show nothing.
        else -> Unit
    }
}

@Composable
fun ConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Transfer") },
        text = { Text("The calculated fee has been determined. Are you sure you want to proceed with the transfer?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ResultDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Composable
fun TransientFailureDialog(
    reason: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Transfer Failed") },
        text = { Text("$reason\n\nWould you like to try again?") },
        // The confirm button is now the "Retry" action
        confirmButton = {
            Button(onClick = onRetry) {
                Text("Retry")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}



