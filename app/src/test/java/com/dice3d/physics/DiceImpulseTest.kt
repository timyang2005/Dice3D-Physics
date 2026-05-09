package com.dice3d.physics

import com.bulletphysics.collision.shapes.BoxShape
import com.bulletphysics.dynamics.RigidBody
import com.bulletphysics.dynamics.RigidBodyConstructionInfo
import com.bulletphysics.linearmath.DefaultMotionState
import com.bulletphysics.linearmath.Transform
import com.dice3d.model.DiceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import javax.vecmath.Quat4f
import javax.vecmath.Vector3f

class DiceImpulseTest {

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
    fun `impulse up min is 12`() {
        assertEquals(12f, DiceRigidBody.IMPULSE_UP_MIN, 0.001f)
    }

    @Test
    fun `impulse up max is 25`() {
        assertEquals(25f, DiceRigidBody.IMPULSE_UP_MAX, 0.001f)
    }

    @Test
    fun `impulse lateral range is 15`() {
        assertEquals(15f, DiceRigidBody.IMPULSE_LATERAL_RANGE, 0.001f)
    }

    @Test
    fun `angular impulse range is 15`() {
        assertEquals(15f, DiceRigidBody.ANGULAR_IMPULSE_RANGE, 0.001f)
    }

    @Test
    fun `applyRandomImpulse produces varied lateral X directions`() {
        val lateralXValues = mutableListOf<Float>()

        repeat(100) {
            dice.applyRandomImpulse()
            val vel = Vector3f()
            dice.body.getLinearVelocity(vel)
            lateralXValues.add(vel.x)
        }

        assertTrue(lateralXValues.any { it > 0 })
        assertTrue(lateralXValues.any { it < 0 })
    }

    @Test
    fun `applyRandomImpulse produces varied lateral Z directions`() {
        val lateralZValues = mutableListOf<Float>()

        repeat(100) {
            dice.applyRandomImpulse()
            val vel = Vector3f()
            dice.body.getLinearVelocity(vel)
            lateralZValues.add(vel.z)
        }

        assertTrue(lateralZValues.any { it > 0 })
        assertTrue(lateralZValues.any { it < 0 })
    }

    @Test
    fun `applyRandomImpulse up component is within new range`() {
        val upValues = mutableListOf<Float>()

        repeat(100) {
            dice.applyRandomImpulse()
            val vel = Vector3f()
            dice.body.getLinearVelocity(vel)
            upValues.add(vel.y)
        }

        assertTrue(upValues.all { it >= 12f && it <= 25f })
    }

    @Test
    fun `applyRandomImpulse applies random initial rotation`() {
        val rotations = mutableListOf<Quat4f>()

        repeat(50) {
            dice.resetPosition(0f, 5f, 0f)
            dice.applyRandomImpulse()
            val transform = Transform()
            dice.body.getWorldTransform(transform)
            val quat = Quat4f()
            transform.getRotation(quat)
            rotations.add(Quat4f(quat))
        }

        val identityQuat = Quat4f(0f, 0f, 0f, 1f)
        val nonIdentityCount = rotations.count {
            Math.abs(it.x - identityQuat.x) > 0.01f ||
            Math.abs(it.y - identityQuat.y) > 0.01f ||
            Math.abs(it.z - identityQuat.z) > 0.01f
        }

        assertTrue(nonIdentityCount > 25)
    }
}
