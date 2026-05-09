package com.dice3d.physics

import com.bulletphysics.dynamics.RigidBody
import com.bulletphysics.linearmath.Transform
import com.dice3d.model.DiceType
import javax.vecmath.Quat4f
import javax.vecmath.Vector3f

class DiceRigidBody(
    val diceType: DiceType,
    val body: RigidBody
) {
    companion object {
        internal const val SETTLE_LINEAR_THRESHOLD = 0.15f
        internal const val SETTLE_ANGULAR_THRESHOLD = 0.15f
        internal const val SETTLE_FRAMES_REQUIRED = 10
        internal const val IMPULSE_UP_MIN = 12f
        internal const val IMPULSE_UP_MAX = 25f
        internal const val IMPULSE_LATERAL_RANGE = 15f
        internal const val ANGULAR_IMPULSE_RANGE = 15f
    }

    private var settleFrameCount = 0

    fun isSettled(): Boolean {
        val linearVel = Vector3f()
        body.getLinearVelocity(linearVel)
        val angularVel = Vector3f()
        body.getAngularVelocity(angularVel)

        if (linearVel.length() < SETTLE_LINEAR_THRESHOLD && angularVel.length() < SETTLE_ANGULAR_THRESHOLD) {
            settleFrameCount++
        } else {
            settleFrameCount = 0
        }
        return settleFrameCount >= SETTLE_FRAMES_REQUIRED
    }

    fun applyRandomImpulse() {
        if (!body.isActive) body.activate()

        val transform = Transform()
        body.getWorldTransform(transform)
        val quat = Quat4f(
            (Math.random().toFloat() - 0.5f) * 2f,
            (Math.random().toFloat() - 0.5f) * 2f,
            (Math.random().toFloat() - 0.5f) * 2f,
            (Math.random().toFloat() - 0.5f) * 2f
        )
        quat.normalize()
        transform.setRotation(quat)
        body.setWorldTransform(transform)

        val upImpulse = IMPULSE_UP_MIN + Math.random().toFloat() * (IMPULSE_UP_MAX - IMPULSE_UP_MIN)
        val lateralX = (Math.random().toFloat() - 0.5f) * 2f * IMPULSE_LATERAL_RANGE
        val lateralZ = (Math.random().toFloat() - 0.5f) * 2f * IMPULSE_LATERAL_RANGE
        body.setLinearVelocity(Vector3f(lateralX, upImpulse, lateralZ))

        val angX = (Math.random().toFloat() - 0.5f) * 2f * ANGULAR_IMPULSE_RANGE
        val angY = (Math.random().toFloat() - 0.5f) * 2f * ANGULAR_IMPULSE_RANGE
        val angZ = (Math.random().toFloat() - 0.5f) * 2f * ANGULAR_IMPULSE_RANGE
        body.setAngularVelocity(Vector3f(angX, angY, angZ))
        settleFrameCount = 0
    }

    fun applyGravitySensor(dx: Float, dy: Float, dz: Float) {
        val out = Vector3f()
        body.getLinearVelocity(out)
        out.x += dy * 2f
        out.z += dx * 2f
        out.y -= dz * 0.5f
        body.setLinearVelocity(out)
    }

    fun getTransform(): Transform {
        val motionState = body.motionState
        val transform = Transform()
        if (motionState != null) {
            motionState.getWorldTransform(transform)
        } else {
            body.getWorldTransform(transform)
        }
        return transform
    }

    fun calculateUpFace(): Int {
        return DiceResultCalculator.calculateUpFace(diceType, getTransform())
    }

    fun resetPosition(x: Float, y: Float, z: Float) {
        val transform = Transform()
        transform.setIdentity()
        transform.origin.set(Vector3f(x, y, z))
        body.setWorldTransform(transform)
        body.setLinearVelocity(Vector3f(0f, 0f, 0f))
        body.setAngularVelocity(Vector3f(0f, 0f, 0f))
        body.forceActivationState(RigidBody.WANTS_DEACTIVATION)
        settleFrameCount = 0
    }
}
