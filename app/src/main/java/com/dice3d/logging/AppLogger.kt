package com.dice3d.logging

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppLogger private constructor() {

    private val buffer = ArrayDeque<LogEntry>(MAX_BUFFER_SIZE + 1)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)

    data class LogEntry(
        val timestamp: Long,
        val level: Int,
        val tag: String,
        val message: String
    ) {
        fun format(formatter: SimpleDateFormat): String {
            val levelStr = when (level) {
                Log.VERBOSE -> "V"
                Log.DEBUG -> "D"
                Log.INFO -> "I"
                Log.WARN -> "W"
                Log.ERROR -> "E"
                Log.ASSERT -> "A"
                else -> "?"
            }
            return "${formatter.format(Date(timestamp))} $levelStr/$tag: $message"
        }
    }

    @Synchronized
    fun log(level: Int, tag: String, message: String) {
        Log.println(level, tag, message)
        val entry = LogEntry(System.currentTimeMillis(), level, tag, message)
        buffer.addLast(entry)
        if (buffer.size > MAX_BUFFER_SIZE) {
            buffer.removeFirst()
        }
    }

    @Synchronized
    fun getEntries(): List<LogEntry> = buffer.toList()

    @Synchronized
    fun clear() {
        buffer.clear()
    }

    fun exportToString(): String {
        val entries = getEntries()
        val sb = StringBuilder()
        for (entry in entries) {
            sb.appendLine(entry.format(dateFormat))
        }
        return sb.toString()
    }

    companion object {
        const val MAX_BUFFER_SIZE = 200

        @Volatile
        private var instance: AppLogger? = null

        fun getInstance(): AppLogger {
            return instance ?: synchronized(this) {
                instance ?: AppLogger().also { instance = it }
            }
        }

        fun d(tag: String, message: String) = getInstance().log(Log.DEBUG, tag, message)
        fun i(tag: String, message: String) = getInstance().log(Log.INFO, tag, message)
        fun w(tag: String, message: String) = getInstance().log(Log.WARN, tag, message)
        fun e(tag: String, message: String) = getInstance().log(Log.ERROR, tag, message)
        fun v(tag: String, message: String) = getInstance().log(Log.VERBOSE, tag, message)
    }
}
