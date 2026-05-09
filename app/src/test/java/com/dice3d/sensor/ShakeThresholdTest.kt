package com.dice3d.sensor

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class ShakeThresholdTest {
    private lateinit var detector: ShakeDetectorLogic

    @Before
    fun setup() { detector = ShakeDetectorLogic() }

    @Test
    fun shakeDetector_shouldRequireSignificantMotion() {
        val minShakeThreshold = 20f
        detector.processReading(floatArrayOf(0f, 9.8f, 0f), 1000L)
        val moderateSpeed = floatArrayOf(2f, 12f, 2f)
        val speed = calculateSpeed(floatArrayOf(0f, 9.8f, 0f), moderateSpeed)
        assertThat(speed).named("Moderate motion speed should be below threshold").isLessThan(minShakeThreshold)
    }

    @Test
    fun shakeDetector_shouldTriggerOnStrongMotion() {
        val minShakeThreshold = 20f
        detector.processReading(floatArrayOf(0f, 9.8f, 0f), 1000L)
        val strongMotion = floatArrayOf(6f, 20f, 6f)
        val speed = calculateSpeed(floatArrayOf(0f, 9.8f, 0f), strongMotion)
        assertThat(speed).named("Strong motion speed should exceed threshold").isAtLeast(minShakeThreshold)
    }

    @Test
    fun shakeDetector_twoShakesRequired() {
        detector.processReading(floatArrayOf(0f, 9.8f, 0f), 1000L)
        detector.processReading(floatArrayOf(6f, 20f, 6f), 1060L)
        val result = detector.processReading(floatArrayOf(6f, 20f, 6f), 1200L)
        assertThat(result).named("Two strong shakes should trigger").isTrue()
    }

    @Test
    fun shakeDetector_singleStrongShakeNotEnough() {
        detector.processReading(floatArrayOf(0f, 9.8f, 0f), 1000L)
        val result = detector.processReading(floatArrayOf(6f, 20f, 6f), 1060L)
        assertThat(result).named("Single shake should not trigger").isFalse()
    }

    private fun calculateSpeed(before: FloatArray, after: FloatArray): Float {
        val dx = after[0] - before[0]
        val dy = after[1] - before[1]
        val dz = after[2] - before[2]
        return kotlin.math.sqrt((dx * dx + dy * dy + dz * dz).toDouble()).toFloat() * 10
    }
}
