package com.axoid.retailbankingdummy.core.crypto

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

/**
 * Manages the secure storage of a session token using Keystore-backed EncryptedSharedPreferences.
 * This MUST be in an Android Library because it requires Android's Context and security APIs.
 */
class SecureSessionManager(context: Context) {

    // 1. Get the recommended advanced Master Key. This is the new, non-deprecated way.
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    // 2. Pass the context and the master key ALIAS (not the key object itself)
    //    to EncryptedSharedPreferences.create().
    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        "secure_session_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_SESSION_TOKEN = "session_token"
    }

    /**
     * Securely saves the session token.
     */
    fun saveSessionToken(token: String) {
        with(sharedPreferences.edit()) {
            putString(KEY_SESSION_TOKEN, token)
            apply()
        }
    }

    /**
     * Retrieves the securely stored session token.
     * @return The token, or null if it doesn't exist.
     */
    fun getSessionToken(): String? {
        return sharedPreferences.getString(KEY_SESSION_TOKEN, null)
    }

    /**
     * Clears the session token (e.g., on logout).
     */
    fun clearSession() {
        with(sharedPreferences.edit()) {
            remove(KEY_SESSION_TOKEN)
            apply()
        }
    }
}
