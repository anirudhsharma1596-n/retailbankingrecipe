package com.axoid.retailbankingdummy.core.crypto

import android.content.Context
import android.util.Base64 // 1. Import the correct Base64 class
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.SecureRandom

// This object provides a secure way to get or create keys for encryption.
object KeyProvider {
    fun getOrCreate(context: Context, alias: String): ByteArray {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        // EncryptedSharedPreferences to store the actual database key
        val sharedPreferences = EncryptedSharedPreferences.create(
            context,
            "secure_db_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val existingKey = sharedPreferences.getString(alias, null)

        return if (existingKey != null) {
            // 2. Use the standard Android Base64 decoder
            // If key exists, decode it from Base64
            Base64.decode(existingKey, Base64.DEFAULT)
        } else {
            // If not, generate a new random key
            val newKey = ByteArray(32).apply {
                SecureRandom().nextBytes(this)
            }
            // 3. Use the standard Android Base64 encoder
            // Save it to EncryptedSharedPreferences in Base64 format
            sharedPreferences.edit()
                .putString(alias, Base64.encodeToString(newKey, Base64.DEFAULT))
                .apply()
            newKey
        }
    }
}
