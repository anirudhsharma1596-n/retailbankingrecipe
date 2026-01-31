package com.axoid.retailbankingdummy.core.model

import androidx.room.PrimaryKey
import java.math.BigDecimal

@androidx.room.Entity(tableName = "accounts")
data class Account(
    @PrimaryKey val id: String,
    val type: String, // e.g., "Checking", "Savings"
    val balance: BigDecimal,
    val accountNumber: String
)