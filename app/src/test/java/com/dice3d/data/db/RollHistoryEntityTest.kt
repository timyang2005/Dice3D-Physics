package com.dice3d.data.db

import org.junit.Assert.assertEquals
import org.junit.Test

class RollHistoryEntityTest {

    @Test
    fun `entity stores all fields correctly`() {
        val entity = RollHistoryEntity(
            id = 1L,
            timestamp = 1000L,
            diceTypeName = "D6",
            diceCount = 2,
            values = "3, 5",
            total = 8
        )
        assertEquals(1L, entity.id)
        assertEquals(1000L, entity.timestamp)
        assertEquals("D6", entity.diceTypeName)
        assertEquals(2, entity.diceCount)
        assertEquals("3, 5", entity.values)
        assertEquals(8, entity.total)
    }

    @Test
    fun `entity default id is zero`() {
        val entity = RollHistoryEntity(
            timestamp = 1000L,
            diceTypeName = "D20",
            diceCount = 1,
            values = "15",
            total = 15
        )
        assertEquals(0L, entity.id)
    }

    @Test
    fun `values string format matches expected pattern`() {
        val results = listOf(3, 5, 2)
        val valuesString = results.joinToString(", ")
        assertEquals("3, 5, 2", valuesString)
    }
}
