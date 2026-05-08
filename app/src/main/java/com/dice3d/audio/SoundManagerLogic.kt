package com.dice3d.audio

class SoundManagerLogic(
    private val rollSound1: Int,
    private val rollSound2: Int,
    private val hitSound1: Int,
    private val hitSound2: Int,
    var enabled: Boolean = true
) {
    var lastPlayAttempted = false; private set

    fun playRoll() {
        lastPlayAttempted = false
        if (!enabled) return
        val soundId = if (Math.random() > 0.5) rollSound1 else rollSound2
        if (soundId <= 0) return
        lastPlayAttempted = true
    }

    fun playHit(intensity: Float = 1.0f) {
        lastPlayAttempted = false
        if (!enabled) return
        val soundId = if (Math.random() > 0.5) hitSound1 else hitSound2
        if (soundId <= 0) return
        lastPlayAttempted = true
    }
}
