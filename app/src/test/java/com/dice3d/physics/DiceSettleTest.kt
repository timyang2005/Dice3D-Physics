package com.dice3d.physics

import com.bulletphysics.collision.shapes.BoxShape
import com.bulletphysics.dynamics.RigidBody
import com.bulletphysics.dynamics.RigidBodyConstructionInfo
import com.bulletphysics.linearmath.DefaultMotionState
import com.bulletphysics.linearmath.Transform
import com.dice3d.model.DiceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import javax.vecmath.Vector3f

class DiceSettleTest {

    private lateinit var dice: DiceRigidBody

    @Before
    fun setUp() {
        val shape = BoxShape(Vector3f(0.5f, 0.5f, 0.5f))
        val transform = Transform().apply { setIdentity() }
        val motionState = DefaultMotionState(transform)
        val inertia = Vector3f()
        shape.calculateLocalInertia(1f, inertia)
        val rbInfo = RigidBodyConstructionInfo(1f, motionState, shape, inertia)
        val body = RigidBody(rbInfo)
        dice = DiceRigidBody(DiceType.D6, body)
    }

    @Test
    fun `settle linear threshold is 0_15`() {
        assertEquals(0.15f, DiceRigidBody.SETTLE_LINEAR_THRESHOLD, 0.001f)
    }

    @Test
    fun `settle angular threshold is 0_15`() {
        assertEquals(0.15f, DiceRigidBody.SETTLE_ANGULAR_THRESHOLD, 0.001f)
    }

    @Test
    fun `settle frames required is 10`() {
        assertEquals(10, DiceRigidBody.SETTLE_FRAMES_REQUIRED)
    }

    @Test
    fun `dice settles with linear velocity between old and new threshold`() {
        dice.body.setLinearVelocity(Vector3f(0.1f, 0f, 0f))
        dice.body.setAngularVelocity(Vector3f(0f, 0f, 0f))

        repeat(10) { dice.isSettled() }

        assertTrue(dice.isSettled())
    }

    @Test
    fun `dice settles with angular velocity between old and new threshold`() {
        dice.body.setLinearVelocity(Vector3f(0f, 0f, 0f))
        dice.body.setAngularVelocity(Vector3f(0.1f, 0f, 0f))

        repeat(10) { dice.isSettled() }

        assertTrue(dice.isSettled())
    }

    @Test
    fun `dice does not settle with velocity above new threshold`() {
        dice.body.setLinearVelocity(Vector3f(0.2f, 0f, 0f))
        dice.body.setAngularVelocity(Vector3f(0f, 0f, 0f))

        repeat(15) { dice.isSettled() }

        assertFalse(dice.isSettled())
    }

    @Test
    fun `dice settles after exactly 10 frames of low velocity`() {
        dice.body.setLinearVelocity(Vector3f(0.01f, 0f, 0f))
        dice.body.setAngularVelocity(Vector3f(0f, 0f, 0f))

        repeat(9) {
            assertFalse(dice.isSettled())
        }
        assertTrue(dice.isSettled())
    }
}
