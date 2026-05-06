package com.dice3d.haptic

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class HapticManager(context: Context) {
    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
    } else { @Suppress("DEPRECATION") context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }
    var enabled: Boolean = true

    fun onDiceHit(intensity: Float = 1.0f) {
        if(!enabled) return
        val amplitude = (intensity.coerceIn(0.1f,1.0f)*255).toInt().coerceIn(1,255)
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) vibrator.vibrate(VibrationEffect.createOneShot(30L,amplitude))
        else @Suppress("DEPRECATION") vibrator.vibrate(30L)
    }

    fun onRoll() {
        if(!enabled) return
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) vibrator.vibrate(VibrationEffect.createOneShot(50L,128))
        else @Suppress("DEPRECATION") vibrator.vibrate(50L)
    }
}
