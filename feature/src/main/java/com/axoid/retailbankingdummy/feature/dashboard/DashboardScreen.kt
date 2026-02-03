// in :feature:dashboard/src/main/java/com/axoid/retailbankingdummy/feature/dashboard/DashboardScreen.kt
package com.axoid.retailbankingdummy.feature.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axoid.retailbankingdummy.core.model.Account
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToTransfer:()-> Unit
) {
    val viewModel: DashboardViewModel = viewModel()
    val accounts by viewModel.accounts.collectAsState()

    Scaffold(
        modifier = Modifier.padding(32.dp),
        topBar = {
            TopAppBar(
                title = { Text(text = "Accounts") },
                // Add an action button to the top bar
                actions = {
                    IconButton(onClick = onNavigateToTransfer) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Transfer Money"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (accounts.isEmpty()) {
            // Show a loading or empty state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Loading accounts...")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(accounts) { account ->
                    AccountCard(account = account)
                }
            }
        }
    }
}

@Composable
fun AccountCard(account: Account) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = account.type, style = MaterialTheme.typography.headlineSmall)
                Text(text = "Acct: ${account.accountNumber}", style = MaterialTheme.typography.bodyMedium)
            }
            Text(
                text = currencyFormat.format(account.balance),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
