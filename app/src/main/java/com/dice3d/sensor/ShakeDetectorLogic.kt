package com.dice3d.sensor

import kotlin.math.sqrt

class ShakeDetectorLogic {
    internal var lastX = 0f; internal var lastY = 0f; internal var lastZ = 0f
    private var lastUpdateTime = 0L; private var lastShakeTime = 0L
    private var isFirstReading = true

    companion object {
        private const val SHAKE_THRESHOLD = 12f
        private const val SHAKE_COOLDOWN_MS = 800L
        private const val MIN_INTERVAL_MS = 50L
    }

    fun processReading(values: FloatArray, currentTime: Long): Boolean {
        if (values.size < 3) return false
        val x = values[0]; val y = values[1]; val z = values[2]

        if (isFirstReading) {
            lastX = x; lastY = y; lastZ = z
            lastUpdateTime = currentTime
            isFirstReading = false
            return false
        }

        if (currentTime - lastUpdateTime < MIN_INTERVAL_MS) return false
        lastUpdateTime = currentTime

        val deltaX = x - lastX; val deltaY = y - lastY; val deltaZ = z - lastZ
        lastX = x; lastY = y; lastZ = z

        val speed = sqrt((deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ).toDouble()).toFloat() * 10
        if (speed > SHAKE_THRESHOLD && currentTime - lastShakeTime > SHAKE_COOLDOWN_MS) {
            lastShakeTime = currentTime
            return true
        }
        return false
    }

    fun reset() {
        isFirstReading = true
        lastX = 0f; lastY = 0f; lastZ = 0f
        lastUpdateTime = 0L; lastShakeTime = 0L
    }
}
