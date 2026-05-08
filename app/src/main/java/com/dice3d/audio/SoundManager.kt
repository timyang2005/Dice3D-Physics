package com.dice3d.audio

import android.content.Context
import android.media.SoundPool

class SoundManager(context: Context) {
    private val soundPool = SoundPool.Builder().setMaxStreams(6).build()
    private var rollSound1: Int = 0; private var rollSound2: Int = 0
    private var hitSound1: Int = 0; private var hitSound2: Int = 0
    var enabled: Boolean = true

    init {
        try {
            val resId1 = context.resources.getIdentifier("dice_roll_1", "raw", context.packageName)
            if (resId1 != 0) rollSound1 = soundPool.load(context, resId1, 1)
        } catch (_: Exception) {}
        try {
            val resId2 = context.resources.getIdentifier("dice_roll_2", "raw", context.packageName)
            if (resId2 != 0) rollSound2 = soundPool.load(context, resId2, 1)
        } catch (_: Exception) {}
        try {
            val resId3 = context.resources.getIdentifier("dice_hit_1", "raw", context.packageName)
            if (resId3 != 0) hitSound1 = soundPool.load(context, resId3, 1)
        } catch (_: Exception) {}
        try {
            val resId4 = context.resources.getIdentifier("dice_hit_2", "raw", context.packageName)
            if (resId4 != 0) hitSound2 = soundPool.load(context, resId4, 1)
        } catch (_: Exception) {}
    }

    fun playRoll() {
        if (!enabled) return
        try {
            val soundId = if (Math.random() > 0.5) rollSound1 else rollSound2
            if (soundId > 0) soundPool.play(soundId, 0.7f, 0.7f, 1, 0, 1.0f)
        } catch (_: Exception) {}
    }

    fun playHit(intensity: Float = 1.0f) {
        if (!enabled) return
        try {
            val soundId = if (Math.random() > 0.5) hitSound1 else hitSound2
            if (soundId > 0) soundPool.play(soundId, intensity.coerceIn(0.1f, 1.0f), intensity.coerceIn(0.1f, 1.0f), 1, 0, 1.0f)
        } catch (_: Exception) {}
    }

    fun release() { try { soundPool.release() } catch (_: Exception) {} }
}
