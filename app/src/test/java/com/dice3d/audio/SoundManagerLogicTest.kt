package com.dice3d.audio

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SoundManagerLogicTest {

    @Test
    fun `playRoll with zero sound IDs does not crash`() {
        val logic = SoundManagerLogic(rollSound1 = 0, rollSound2 = 0, hitSound1 = 0, hitSound2 = 0)
        logic.playRoll()
        assertFalse("Should not attempt play with zero sound ID", logic.lastPlayAttempted)
    }

    @Test
    fun `playHit with zero sound IDs does not crash`() {
        val logic = SoundManagerLogic(rollSound1 = 0, rollSound2 = 0, hitSound1 = 0, hitSound2 = 0)
        logic.playHit(0.5f)
        assertFalse("Should not attempt play with zero sound ID", logic.lastPlayAttempted)
    }

    @Test
    fun `playRoll with valid sound IDs attempts play`() {
        val logic = SoundManagerLogic(rollSound1 = 1, rollSound2 = 2, hitSound1 = 3, hitSound2 = 4)
        logic.playRoll()
        assertTrue("Should attempt play with valid sound ID", logic.lastPlayAttempted)
    }

    @Test
    fun `playHit with valid sound IDs attempts play`() {
        val logic = SoundManagerLogic(rollSound1 = 1, rollSound2 = 2, hitSound1 = 3, hitSound2 = 4)
        logic.playHit(0.5f)
        assertTrue("Should attempt play with valid sound ID", logic.lastPlayAttempted)
    }

    @Test
    fun `disabled sound manager does not attempt play`() {
        val logic = SoundManagerLogic(rollSound1 = 1, rollSound2 = 2, hitSound1 = 3, hitSound2 = 4, enabled = false)
        logic.playRoll()
        assertFalse("Disabled manager should not attempt play", logic.lastPlayAttempted)
    }

    @Test
    fun `one valid roll sound is sufficient`() {
        val logic = SoundManagerLogic(rollSound1 = 1, rollSound2 = 0, hitSound1 = 0, hitSound2 = 0)
        var attempted = false
        for (i in 0..20) { logic.playRoll(); if (logic.lastPlayAttempted) attempted = true }
        assertTrue("At least one roll sound valid should attempt play", attempted)
    }
}
