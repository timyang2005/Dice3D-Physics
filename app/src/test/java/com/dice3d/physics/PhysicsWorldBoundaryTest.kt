package com.dice3d.physics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PhysicsWorldBoundaryTest {

    @Test
    fun `wall X dimension is 15`() {
        assertEquals(15f, PhysicsWorld.WALL_X, 0.001f)
    }

    @Test
    fun `wall Z dimension is 10`() {
        assertEquals(10f, PhysicsWorld.WALL_Z, 0.001f)
    }

    @Test
    fun `wall Y ceiling dimension is 20`() {
        assertEquals(20f, PhysicsWorld.WALL_Y, 0.001f)
    }

    @Test
    fun `physics world creates six boundary planes`() {
        val world = PhysicsWorld()
        val numObjects = world.dynamicsWorld.getNumCollisionObjects()
        assertTrue(
            "Expected at least 6 boundary objects, got $numObjects",
            numObjects >= 6
        )
    }

    @Test
    fun `physics world initializes without errors`() {
        val world = PhysicsWorld()
        assertTrue(world.diceBodies.isEmpty())
    }
}
