package com.example

import android.app.Application
import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.util.Locale
import java.util.TimeZone

class CaterersApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Ensure India Standard Time (IST - Asia/Kolkata) across the entire application
        try {
            val ist = TimeZone.getTimeZone("Asia/Kolkata")
            TimeZone.setDefault(ist)
            Locale.setDefault(Locale("en", "IN"))
        } catch (_: Exception) {}

        try {
            val webViewWasmDir = java.io.File(cacheDir, "WebView/Default/HTTP Cache/Code Cache/wasm")
            if (!webViewWasmDir.exists()) {
                webViewWasmDir.mkdirs()
            }
        } catch (_: Exception) {}

        initFirebase(this)
        com.example.util.FcmNotificationManager.initialize(this)
    }

    companion object {
        private const val TAG = "CaterersApplication"

        fun initFirebase(context: Context) {
            try {
                if (FirebaseApp.getApps(context).isEmpty()) {
                    try {
                        FirebaseApp.initializeApp(context)
                    } catch (e: Exception) {
                        Log.d(TAG, "Auto Firebase init skipped: ${e.message}")
                    }

                    if (FirebaseApp.getApps(context).isNotEmpty()) {
                        Log.i(TAG, "FirebaseApp initialized successfully.")
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Unable to initialize FirebaseApp: ${e.message}")
            }
        }
    }
}
