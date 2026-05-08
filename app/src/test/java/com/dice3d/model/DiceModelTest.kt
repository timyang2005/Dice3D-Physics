package com.dice3d.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DiceTypeTest {

    @Test
    fun `fromFaces returns correct type for known face counts`() {
        assertEquals(DiceType.D4, DiceType.fromFaces(4))
        assertEquals(DiceType.D6, DiceType.fromFaces(6))
        assertEquals(DiceType.D8, DiceType.fromFaces(8))
        assertEquals(DiceType.D10, DiceType.fromFaces(10))
        assertEquals(DiceType.D12, DiceType.fromFaces(12))
        assertEquals(DiceType.D20, DiceType.fromFaces(20))
        assertEquals(DiceType.D100, DiceType.fromFaces(100))
    }

    @Test
    fun `fromFaces returns D6 for unknown face count`() {
        assertEquals(DiceType.D6, DiceType.fromFaces(7))
        assertEquals(DiceType.D6, DiceType.fromFaces(0))
    }

    @Test
    fun `all dice types have correct face count`() {
        assertEquals(4, DiceType.D4.faces)
        assertEquals(6, DiceType.D6.faces)
        assertEquals(8, DiceType.D8.faces)
        assertEquals(10, DiceType.D10.faces)
        assertEquals(12, DiceType.D12.faces)
        assertEquals(20, DiceType.D20.faces)
        assertEquals(100, DiceType.D100.faces)
    }

    @Test
    fun `all dice types have model names`() {
        DiceType.entries.forEach { type ->
            assertTrue(type.modelName.endsWith(".obj"))
        }
    }
}

class DiceConfigTest {

    @Test
    fun `default config is D6 with 1 die`() {
        val config = DiceConfig()
        assertEquals(DiceType.D6, config.diceType)
        assertEquals(1, config.count)
    }

    @Test
    fun `copy with new type preserves other fields`() {
        val config = DiceConfig(count = 3)
        val newConfig = config.copy(diceType = DiceType.D20)
        assertEquals(DiceType.D20, newConfig.diceType)
        assertEquals(3, newConfig.count)
    }

    @Test
    fun `contrast color is dark for light backgrounds`() {
        val lightColor = androidx.compose.ui.graphics.Color.White
        val numberColor = lightColor.computeContrastNumberColor()
        assertTrue(numberColor.red < 0.5f)
    }

    @Test
    fun `contrast color is light for dark backgrounds`() {
        val darkColor = androidx.compose.ui.graphics.Color.Black
        val numberColor = darkColor.computeContrastNumberColor()
        assertTrue(numberColor.red > 0.5f)
    }
}
