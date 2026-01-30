package com.axoid.retailbankingdummy.core.security

// In :core:security/src/main/java/com/axoid/retailbankingdummy/core/security/HostileEnvironmentChecker.kt



import android.os.Build
import java.io.File

/**
 * A utility to check for potentially hostile environments.
 * This class MUST live in an Android Library module because it uses Android-specific APIs.
 */
class HostileEnvironmentChecker {

    fun isRooted(): Boolean {
        // Simple checks. More robust libraries exist for this (e.g., RootBeer).
        val suspiciousPaths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su"
        )
        return suspiciousPaths.any { File(it).exists() }
    }

    fun isDebuggerAttached(): Boolean {
        // Uses the Android Debug class.
        return android.os.Debug.isDebuggerConnected()
    }

    fun isEmulator(): Boolean {
        // Checks various build properties that are common in emulators.
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk" == Build.PRODUCT)
    }

    /**
     * Runs a full diagnostic check.
     * @return true if the environment is considered hostile.
     */
    fun isHostile(): Boolean {
        // In a real app, you might want to avoid running these checks in debug builds.
        // if (BuildConfig.DEBUG) return false

        return isRooted() || isDebuggerAttached() || isEmulator()
    }
}
