package com.dice3d

import android.app.Application
import com.dice3d.logging.AppLogger
import com.dice3d.logging.CrashLogManager

class DiceApp : Application() {
    lateinit var crashLogManager: CrashLogManager

    override fun onCreate() {
        super.onCreate()
        crashLogManager = CrashLogManager(this)
        crashLogManager.install()
        AppLogger.i(TAG, "DiceApp initialized")
    }

    companion object {
        private const val TAG = "DiceApp"
    }
}
