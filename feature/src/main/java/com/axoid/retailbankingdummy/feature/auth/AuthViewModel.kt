package com.axoid.retailbankingdummy.feature.auth

// In :feature:auth/src/main/java/com/axoid/retailbankingdummy/feature/auth/AuthViewModel.kt



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axoid.retailbankingdummy.core.crypto.SecureSessionManager
import com.axoid.retailbankingdummy.core.network.GithubApiProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.UUID
import javax.net.ssl.SSLPeerUnverifiedException

class AuthViewModel(private val sessionManager: SecureSessionManager) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try{
                val user = GithubApiProvider.githubApiService.getUser(username)
                val token = "token_for_${user.login}_${UUID.randomUUID()}"
                sessionManager.saveSessionToken(token)
                _authState.value = AuthState.Success
            }catch (e: Exception){
                if(e is SSLPeerUnverifiedException){
                    _authState.value = AuthState.Error("Security Error: Untrusted connection.")
                }else{
                    _authState.value = AuthState.Error("Login failed: ${e.message}")
                }
            }
            // Simulate network call
            delay(1500)

            // Mocked authentication logic
            if (username.equals("user", ignoreCase = true) && password == "password123") {
                val token = "token_${UUID.randomUUID()}"
                sessionManager.saveSessionToken(token)
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error("Invalid username or password.")
            }
        }
    }
}

// Define the states for the UI
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}
