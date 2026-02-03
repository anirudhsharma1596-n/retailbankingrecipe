// In :feature:transfer/src/main/java/com/axoid/retailbankingdummy/feature/transfer/TransferRepository.kt
package com.axoid.retailbankingdummy.feature.transfer

import com.axoid.retailbankingdummy.feature.dashboard.AccountDao
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

class TransferRepository(
    private val accountDao: AccountDao
    // We will inject the network service here later
) {

    /**
     * Gets the list of all available accounts from the local encrypted database.
     */
    fun getAccounts(): Flow<List<com.axoid.retailbankingdummy.core.model.Account>> {
        return accountDao.getAccounts()
    }

    /**
     * Simulates the network call to calculate transfer fees.
     * In a real app, this would be a network request.
     */
    suspend fun calculateFees(amount: BigDecimal, fromAccount: com.axoid.retailbankingdummy.core.model.Account): BigDecimal {
        // Simulate a network delay
        kotlinx.coroutines.delay(800)
        // Dummy fee logic: 1% of the transfer amount, capped at $5.00
        val fee = amount.multiply(BigDecimal("0.01")).min(BigDecimal("5.00"))
        return fee.setScale(2, java.math.RoundingMode.HALF_UP)
    }

    /**
     * Simulates the transfer submission to the backend.
     * This is where the idempotency key will be used.
     *
     * @param idempotencyKey A unique key for this specific transfer attempt.
     */
    suspend fun submitTransfer(
        fromAccount: com.axoid.retailbankingdummy.core.model.Account,
        toAccount: com.axoid.retailbankingdummy.core.model.Account,
        amount: BigDecimal,
        idempotencyKey: String
    ) {
        // Here, we would make the actual Retrofit call with the idempotencyKey in the header.
        // For now, we just simulate the network delay.
        println("SUBMITTING TRANSFER with Idempotency-Key: $idempotencyKey")
        kotlinx.coroutines.delay(1500)
        // In a real scenario, the network interceptor would simulate a failure here.
        println("SUCCESSFULLY SUBMITTED with Idempotency-Key: $idempotencyKey")
    }
}
