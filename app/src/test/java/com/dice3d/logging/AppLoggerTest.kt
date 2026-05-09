package com.dice3d.logging

import android.util.Log
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.MockedStatic
import org.mockito.Mockito
import java.text.SimpleDateFormat
import java.util.Locale

class AppLoggerTest {

    private lateinit var logMock: MockedStatic<Log>
    private lateinit var logger: AppLogger

    @Before
    fun setup() {
        logMock = Mockito.mockStatic(Log::class.java)
        logMock.`when`<Int> {
            Log.println(Mockito.anyInt(), Mockito.any(), Mockito.any())
        }.thenReturn(0)
        resetSingleton()
        logger = AppLogger.getInstance()
    }

    @After
    fun tearDown() {
        logMock.close()
        resetSingleton()
    }

    private fun resetSingleton() {
        val field = AppLogger::class.java.getDeclaredField("instance")
        field.isAccessible = true
        field.set(null, null)
    }

    @Test
    fun `log entry is added to buffer`() {
        logger.log(Log.DEBUG, "TestTag", "test message")
        val entries = logger.getEntries()
        assertThat(entries).hasSize(1)
        assertThat(entries[0].tag).isEqualTo("TestTag")
        assertThat(entries[0].message).isEqualTo("test message")
        assertThat(entries[0].level).isEqualTo(Log.DEBUG)
    }

    @Test
    fun `multiple log entries maintain order`() {
        logger.log(Log.DEBUG, "Tag1", "first")
        logger.log(Log.INFO, "Tag2", "second")
        logger.log(Log.ERROR, "Tag3", "third")
        val entries = logger.getEntries()
        assertThat(entries).hasSize(3)
        assertThat(entries[0].message).isEqualTo("first")
        assertThat(entries[1].message).isEqualTo("second")
        assertThat(entries[2].message).isEqualTo("third")
    }

    @Test
    fun `buffer size is limited to MAX_BUFFER_SIZE`() {
        for (i in 1..AppLogger.MAX_BUFFER_SIZE + 50) {
            logger.log(Log.DEBUG, "Tag", "message $i")
        }
        val entries = logger.getEntries()
        assertThat(entries).hasSize(AppLogger.MAX_BUFFER_SIZE)
    }

    @Test
    fun `buffer discards oldest entries when full`() {
        for (i in 1..AppLogger.MAX_BUFFER_SIZE + 10) {
            logger.log(Log.DEBUG, "Tag", "message $i")
        }
        val entries = logger.getEntries()
        assertThat(entries.first().message).isEqualTo("message 11")
        assertThat(entries.last().message).isEqualTo("message ${AppLogger.MAX_BUFFER_SIZE + 10}")
    }

    @Test
    fun `clear empties the buffer`() {
        logger.log(Log.DEBUG, "Tag", "message")
        logger.clear()
        assertThat(logger.getEntries()).isEmpty()
    }

    @Test
    fun `log entry format contains level, tag and message`() {
        logger.log(Log.ERROR, "MyTag", "something broke")
        val entry = logger.getEntries().first()
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
        val formatted = entry.format(formatter)
        assertThat(formatted).contains("E/MyTag: something broke")
    }

    @Test
    fun `log entry format uses correct level strings`() {
        val levels = mapOf(
            Log.VERBOSE to "V",
            Log.DEBUG to "D",
            Log.INFO to "I",
            Log.WARN to "W",
            Log.ERROR to "E",
            Log.ASSERT to "A"
        )
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
        for ((level, levelStr) in levels) {
            logger.log(level, "Tag", "msg")
            val entry = logger.getEntries().last()
            assertThat(entry.format(formatter)).startsWith("${formatter.format(entry.timestamp)} $levelStr/Tag: msg")
        }
    }

    @Test
    fun `exportToString returns formatted entries`() {
        logger.log(Log.INFO, "App", "started")
        logger.log(Log.ERROR, "App", "crashed")
        val output = logger.exportToString()
        assertThat(output).contains("I/App: started")
        assertThat(output).contains("E/App: crashed")
    }

    @Test
    fun `exportToString returns empty string when buffer is empty`() {
        assertThat(logger.exportToString()).isEmpty()
    }

    @Test
    fun `getInstance returns singleton`() {
        val instance1 = AppLogger.getInstance()
        val instance2 = AppLogger.getInstance()
        assertThat(instance1).isSameInstanceAs(instance2)
    }

    @Test
    fun `companion convenience methods add entries`() {
        AppLogger.d("DTag", "debug msg")
        AppLogger.i("ITag", "info msg")
        AppLogger.w("WTag", "warn msg")
        AppLogger.e("ETag", "error msg")
        val entries = logger.getEntries()
        assertThat(entries).hasSize(4)
        assertThat(entries[0].level).isEqualTo(Log.DEBUG)
        assertThat(entries[1].level).isEqualTo(Log.INFO)
        assertThat(entries[2].level).isEqualTo(Log.WARN)
        assertThat(entries[3].level).isEqualTo(Log.ERROR)
    }
}
