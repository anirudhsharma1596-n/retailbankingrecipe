package com.axoid.retailbankingdummy.feature.dashboard

// in :feature:dashboard/src/main/java/com/axoid/retailbankingdummy/feature/dashboard/DatabaseProvider.kt

import android.content.Context
import androidx.room.Room
import com.axoid.retailbankingdummy.core.crypto.KeyProvider
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory


object DatabaseProvider {

    private var instance: EncryptedDatabase? = null

    fun getInstance(context: Context): EncryptedDatabase {
        return instance ?: synchronized(this) {
            instance ?: buildDatabase(context).also { instance = it }
        }
    }

    private fun buildDatabase(context: Context): EncryptedDatabase {
        // 1. Get the encryption key securely from our :core:crypto module
        val passphrase = KeyProvider.getOrCreate(context, "db_passphrase")

        // 2. Create the SQLCipher factory, passing in the key
        val factory = SupportOpenHelperFactory(passphrase)

        // 3. Build the Room database, providing the factory
        return Room.databaseBuilder(
            context.applicationContext,
            EncryptedDatabase::class.java,
            "banking.db"
        )
            .openHelperFactory(factory) // This is what enables encryption
            .fallbackToDestructiveMigration()
            .build()
    }
}
