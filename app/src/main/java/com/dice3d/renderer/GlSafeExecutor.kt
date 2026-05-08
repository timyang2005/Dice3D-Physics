package com.dice3d.renderer

object GlSafeExecutor {
    fun executeSafely(block: () -> Unit): Boolean {
        return try {
            block()
            true
        } catch (_: Exception) {
            false
        }
    }

    fun <T> executeSafelyWithDefault(default: T, block: () -> T): T {
        return try {
            block()
        } catch (_: Exception) {
            default
        }
    }
}
