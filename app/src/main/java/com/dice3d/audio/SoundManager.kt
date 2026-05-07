package com.dice3d.audio

import android.content.Context
import android.media.SoundPool

class SoundManager(context: Context) {
    private val soundPool = SoundPool.Builder().setMaxStreams(6).build()
    private var rollSound1: Int = 0; private var rollSound2: Int = 0
    private var hitSound1: Int = 0; private var hitSound2: Int = 0
    var enabled: Boolean = true

    init {
        try { rollSound1 = soundPool.load(context, context.resources.getIdentifier("dice_roll_1","raw",context.packageName),1) } catch(_:Exception) {}
        try { rollSound2 = soundPool.load(context, context.resources.getIdentifier("dice_roll_2","raw",context.packageName),1) } catch(_:Exception) {}
        try { hitSound1 = soundPool.load(context, context.resources.getIdentifier("dice_hit_1","raw",context.packageName),1) } catch(_:Exception) {}
        try { hitSound2 = soundPool.load(context, context.resources.getIdentifier("dice_hit_2","raw",context.packageName),1) } catch(_:Exception) {}
    }

    fun playRoll() { if(!enabled) return; try { soundPool.play(if(Math.random()>0.5) rollSound1 else rollSound2, 0.7f,0.7f,1,0,1.0f) } catch(_:Exception){} }
    fun playHit(intensity: Float = 1.0f) { if(!enabled) return; try { soundPool.play(if(Math.random()>0.5) hitSound1 else hitSound2, intensity.coerceIn(0.1f,1.0f),intensity.coerceIn(0.1f,1.0f),1,0,1.0f) } catch(_:Exception){} }
    fun release() { soundPool.release() }
}
