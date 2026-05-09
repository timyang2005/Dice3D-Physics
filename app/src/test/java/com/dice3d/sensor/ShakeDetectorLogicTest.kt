package com.dice3d.sensor

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ShakeDetectorLogicTest {
    private lateinit var detector: ShakeDetectorLogic

    @Before
    fun setup() { detector = ShakeDetectorLogic() }

    @Test
    fun `first sensor reading does not trigger shake`() {
        val gravityReading = floatArrayOf(0f, 9.8f, 0f)
        val result = detector.processReading(gravityReading, 1000L)
        assertFalse("First reading should not trigger shake", result)
    }

    @Test
    fun `second reading with small delta does not trigger shake`() {
        detector.processReading(floatArrayOf(0f, 9.8f, 0f), 1000L)
        val result = detector.processReading(floatArrayOf(0.1f, 9.9f, 0.1f), 1060L)
        assertFalse("Small delta should not trigger shake", result)
    }

    @Test
    fun `single shake does not trigger - requires two shakes`() {
        detector.processReading(floatArrayOf(0f, 9.8f, 0f), 1000L)
        val result = detector.processReading(floatArrayOf(5f, 15f, 5f), 1060L)
        assertFalse("Single shake should not trigger - requires two shakes", result)
    }

    @Test
    fun `two shakes within cooldown triggers shake`() {
        detector.processReading(floatArrayOf(0f, 9.8f, 0f), 1000L)
        detector.processReading(floatArrayOf(5f, 15f, 5f), 1060L)
        val result = detector.processReading(floatArrayOf(5f, 15f, 5f), 1200L)
        assertTrue("Two shakes within cooldown should trigger shake", result)
    }

    @Test
    fun `readings too close in time are ignored`() {
        detector.processReading(floatArrayOf(0f, 9.8f, 0f), 1000L)
        val result = detector.processReading(floatArrayOf(5f, 15f, 5f), 1010L)
        assertFalse("Readings too close in time should be ignored", result)
    }

    @Test
    fun `shake cooldown prevents rapid re-triggering`() {
        detector.processReading(floatArrayOf(0f, 9.8f, 0f), 1000L)
        detector.processReading(floatArrayOf(5f, 15f, 5f), 1060L)
        detector.processReading(floatArrayOf(5f, 15f, 5f), 1200L)
        val result = detector.processReading(floatArrayOf(5f, 15f, 5f), 1300L)
        assertFalse("Shake cooldown should prevent rapid re-triggering", result)
    }

    @Test
    fun `shake can trigger again after cooldown`() {
        detector.processReading(floatArrayOf(0f, 9.8f, 0f), 1000L)
        detector.processReading(floatArrayOf(5f, 15f, 5f), 1060L)
        detector.processReading(floatArrayOf(5f, 15f, 5f), 1200L)
        val result = detector.processReading(floatArrayOf(5f, 15f, 5f), 2500L)
        assertTrue("Shake should trigger again after cooldown", result)
    }

    @Test
    fun `reset clears shake count`() {
        detector.processReading(floatArrayOf(0f, 9.8f, 0f), 1000L)
        detector.processReading(floatArrayOf(5f, 15f, 5f), 1060L)
        detector.reset()
        detector.processReading(floatArrayOf(0f, 9.8f, 0f), 2000L)
        val result = detector.processReading(floatArrayOf(5f, 15f, 5f), 2060L)
        assertFalse("After reset, single shake should not trigger", result)
    }
}
