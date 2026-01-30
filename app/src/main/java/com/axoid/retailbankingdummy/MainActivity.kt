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
import com.axoid.retailbankingdummy.core.security.HostileEnvironmentChecker
import com.axoid.retailbankingdummy.feature.auth.LoginScreen
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
            var isLoggedIn by remember { mutableStateOf(false) }


            RetailBankingDummyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (hostile) {
                        // If the environment is hostile, show an error screen
                        HostileEnvironmentScreen(modifier = Modifier.padding(innerPadding))
                    } else if (!isLoggedIn) {
                        // If not logged in, show the LoginScreen
                        LoginScreen(
                            onLoginSuccess = { isLoggedIn = true }
                        )
                    } else {
                        // After successful login, show the main content
                        Greeting(
                            name = "Android",
                            modifier = Modifier.padding(innerPadding)
                        )
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