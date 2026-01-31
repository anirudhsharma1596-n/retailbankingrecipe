// in :feature:dashboard/src/main/java/com/axoid/retailbankingdummy/feature/dashboard/DashboardRepository.kt
package com.axoid.retailbankingdummy.feature.dashboard

import com.axoid.retailbankingdummy.core.model.Account
import com.axoid.retailbankingdummy.core.network.GithubApiProvider
import kotlinx.coroutines.flow.Flow

class DashboardRepository(private val accountDao: AccountDao) {

    // The UI will observe this Flow. It always gets data from the DB.
    val accounts: Flow<List<Account>> = accountDao.getAccounts()

    // This function refreshes the data from the network and saves it to the DB.
    suspend fun refreshAccounts() {
        try {
            // Fetch from the mock API
            val freshAccounts = GithubApiProvider.githubApiService.getAccounts()
            // Save the fresh data into our encrypted Room database
            accountDao.saveAccounts(freshAccounts)
        } catch (e: Exception) {
            // If the network fails, the app will just show the old data from the DB.
            e.printStackTrace()
        }
    }
}
