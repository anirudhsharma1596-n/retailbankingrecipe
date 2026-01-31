package com.axoid.retailbankingdummy.feature.dashboard

// in :feature:dashboard/src/main/java/com/axoid/retailbankingdummy/feature/dashboard/EncryptedDatabase.kt


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.axoid.retailbankingdummy.core.model.Account

@Database(entities = [Account::class], version = 1)
@TypeConverters(Converters::class) // We'll create this next
abstract class EncryptedDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
}
