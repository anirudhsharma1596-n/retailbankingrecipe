package com.axoid.retailbankingdummy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.axoid.retailbankingdummy.core.security.HostileEnvironmentChecker
import com.axoid.retailbankingdummy.feature.auth.LoginScreen
import com.axoid.retailbankingdummy.feature.dashboard.DashboardScreen
import com.axoid.retailbankingdummy.feature.transfer.TransferScreen
import com.axoid.retailbankingdummy.ui.theme.RetailBankingDummyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // --- Run the Hostile Environment Check ---
        val checker = HostileEnvironmentChecker()
        val isHostile = checker.isHostile()
        // ----------------------------------------

        enableEdgeToEdge()
        setContent {
            var hostile by remember { mutableStateOf(isHostile) }

            RetailBankingDummyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (hostile) {
                        // If the environment is hostile, show an error screen
                        HostileEnvironmentScreen(modifier = Modifier.padding(innerPadding))
                    } else  {
                        Box(modifier = Modifier.padding(innerPadding)) {
                            AppNavigation()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HostileEnvironmentScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Application cannot run in this environment.",
            color = Color.Red,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// Greeting and GreetingPreview composables remain the same...


@Composable
fun AppNavigation() {
    val navController = androidx.navigation.compose.rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        // Clear back stack so user can't go back to login
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("dashboard") {
            DashboardScreen(
                // When the transfer button is clicked, navigate to the "transfer" route
                onNavigateToTransfer = {
                    navController.navigate("transfer")
                }
            )
        }

        // --- ADD THIS NEW DESTINATION ---
        composable("transfer") {
            TransferScreen() // Our new screen is now part of the navigation graph
        }
    }
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RetailBankingDummyTheme {
        Greeting("Android")
    }
}