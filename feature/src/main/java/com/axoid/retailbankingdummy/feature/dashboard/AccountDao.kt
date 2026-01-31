package com.axoid.retailbankingdummy.feature.dashboard

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.axoid.retailbankingdummy.core.model.Account
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts")
    fun getAccounts(): Flow<List<Account>>

    @Upsert
    suspend fun saveAccounts(accounts: List<Account>)
}