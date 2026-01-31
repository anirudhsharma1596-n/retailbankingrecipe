// in :feature:dashboard/src/main/java/com/axoid/retailbankingdummy/feature/dashboard/DashboardViewModel.kt
package com.axoid.retailbankingdummy.feature.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.axoid.retailbankingdummy.core.model.Account
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    // Initialize the repository with the DAO from our encrypted database provider.
    private val repository = DashboardRepository(
        DatabaseProvider.getInstance(application).accountDao()
    )

    // Expose the Flow of accounts from the repository as StateFlow for the UI.
    val accounts: StateFlow<List<Account>> = repository.accounts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList() // Start with an empty list.
        )

    init {
        // When the ViewModel is created, trigger a refresh from the network.
        refreshData()
    }

    private fun refreshData() {
        viewModelScope.launch {
            repository.refreshAccounts()
        }
    }
}
