package com.dice3d.physics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

class ThreadSafetyTest {

    @Test
    fun `concurrent read and write on CopyOnWriteArrayList does not crash`() {
        val list = CopyOnWriteArrayList<String>()
        val latch = CountDownLatch(2)
        val executor = Executors.newFixedThreadPool(2)
        var exceptionThrown = false

        executor.submit {
            try {
                for (i in 0 until 1000) { list.add("item-$i") }
            } catch (e: ConcurrentModificationException) { exceptionThrown = true }
            finally { latch.countDown() }
        }

        executor.submit {
            try {
                for (i in 0 until 1000) { for (item in list) { /* just iterate */ } }
            } catch (e: ConcurrentModificationException) { exceptionThrown = true }
            finally { latch.countDown() }
        }

        latch.await()
        executor.shutdown()
        assertFalse("CopyOnWriteArrayList should not throw ConcurrentModificationException", exceptionThrown)
    }

    @Test
    fun `snapshot iteration allows safe concurrent modification`() {
        val list = CopyOnWriteArrayList<Int>()
        for (i in 0..10) list.add(i)

        val snapshot = list.toList()
        list.clear()

        assertEquals("Snapshot should retain original data", 11, snapshot.size)
        assertTrue("Original list should be empty after clear", list.isEmpty())
    }

    private fun assertFalse(message: String, condition: Boolean) {
        org.junit.Assert.assertFalse(message, condition)
    }
}
