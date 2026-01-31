package com.axoid.retailbankingdummy

// In :app/src/main/java/com/axoid/retailbankingdummy/RetailBankingDummyApplication.kt


import android.app.Application



class RetailBankingDummyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // This is the crucial step.
        // It loads the native SQLCipher libraries into memory.
//        SQLiteDatabase.loadLibs(this)
        System.loadLibrary("sqlcipher")
    }
}
