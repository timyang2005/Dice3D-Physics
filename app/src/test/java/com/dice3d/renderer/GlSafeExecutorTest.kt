package com.dice3d.renderer

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class GlSafeExecutorTest {

    @Test
    fun executeSafely_returnsTrueWhenNoException() {
        val result = GlSafeExecutor.executeSafely { }
        assertThat(result).isTrue()
    }

    @Test
    fun executeSafely_returnsFalseWhenExceptionThrown() {
        val result = GlSafeExecutor.executeSafely { throw RuntimeException("test") }
        assertThat(result).isFalse()
    }

    @Test
    fun executeSafelyWithDefault_returnsResultWhenNoException() {
        val result = GlSafeExecutor.executeSafelyWithDefault(0) { 42 }
        assertThat(result).isEqualTo(42)
    }

    @Test
    fun executeSafelyWithDefault_returnsDefaultWhenExceptionThrown() {
        val result = GlSafeExecutor.executeSafelyWithDefault(0) { throw RuntimeException("test") }
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun executeSafely_catchesRuntimeException() {
        var caught = false
        GlSafeExecutor.executeSafely { throw RuntimeException("gl error"); caught = true }
        assertThat(caught).isFalse()
    }

    @Test
    fun executeSafely_catchesIllegalArgumentException() {
        val result = GlSafeExecutor.executeSafely { throw IllegalArgumentException("bad arg") }
        assertThat(result).isFalse()
    }
}
