package com.dice3d.physics

import com.bulletphysics.collision.broadphase.AxisSweep3
import com.bulletphysics.collision.dispatch.CollisionDispatcher
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration
import com.bulletphysics.collision.shapes.StaticPlaneShape
import com.bulletphysics.dynamics.DiscreteDynamicsWorld
import com.bulletphysics.dynamics.RigidBody
import com.bulletphysics.dynamics.RigidBodyConstructionInfo
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver
import com.bulletphysics.linearmath.DefaultMotionState
import com.bulletphysics.linearmath.Transform
import java.util.concurrent.CopyOnWriteArrayList
import javax.vecmath.Vector3f

class PhysicsWorld {

    val dynamicsWorld: DiscreteDynamicsWorld
    val diceBodies: CopyOnWriteArrayList<DiceRigidBody> = CopyOnWriteArrayList()

    companion object {
        private const val GRAVITY = -30f
        internal const val WALL_X = 15f
        internal const val WALL_Z = 10f
        internal const val WALL_Y = 20f
        private const val FLOOR_RESTITUTION = 0.3f
        private const val FLOOR_FRICTION = 0.8f
    }

    init {
        val collisionConfig = DefaultCollisionConfiguration()
        val dispatcher = CollisionDispatcher(collisionConfig)
        val overlappingPairCache = AxisSweep3(
            Vector3f(-100f, -100f, -100f),
            Vector3f(100f, 100f, 100f),
            1024
        )
        val solver = SequentialImpulseConstraintSolver()
        dynamicsWorld = DiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfig)
        dynamicsWorld.setGravity(Vector3f(0f, GRAVITY, 0f))
        createBoundaries()
    }

    private fun createBoundaries() {
        createStaticPlane(Vector3f(0f, 1f, 0f), 0f)
        createStaticPlane(Vector3f(1f, 0f, 0f), -WALL_X)
        createStaticPlane(Vector3f(-1f, 0f, 0f), -WALL_X)
        createStaticPlane(Vector3f(0f, 0f, 1f), -WALL_Z)
        createStaticPlane(Vector3f(0f, 0f, -1f), -WALL_Z)
        createStaticPlane(Vector3f(0f, -1f, 0f), -WALL_Y)
    }

    private fun createStaticPlane(normal: Vector3f, constant: Float) {
        val shape = StaticPlaneShape(normal, constant)
        val transform = Transform().apply { setIdentity() }
        val motionState = DefaultMotionState(transform)
        val rbInfo = RigidBodyConstructionInfo(0f, motionState, shape, Vector3f(0f, 0f, 0f))
        val body = RigidBody(rbInfo)
        body.restitution = FLOOR_RESTITUTION
        body.friction = FLOOR_FRICTION
        dynamicsWorld.addRigidBody(body)
    }

    fun addDice(dice: DiceRigidBody) {
        diceBodies.add(dice)
        dynamicsWorld.addRigidBody(dice.body)
    }

    fun removeDice(dice: DiceRigidBody) {
        diceBodies.remove(dice)
        dynamicsWorld.removeRigidBody(dice.body)
    }

    fun clearAllDice() {
        diceBodies.forEach { dynamicsWorld.removeRigidBody(it.body) }
        diceBodies.clear()
    }

    fun stepSimulation(timeStep: Float, timeScale: Float = 1f) {
        dynamicsWorld.stepSimulation(timeStep * timeScale, 5)
    }

    fun areAllDiceSettled(): Boolean {
        return diceBodies.isNotEmpty() && diceBodies.all { it.isSettled() }
    }

    fun getDiceResults(): List<Int> {
        return diceBodies.map { it.calculateUpFace() }
    }

    fun applyRandomImpulseToAll() {
        diceBodies.forEach { it.applyRandomImpulse() }
    }

    fun applyGravitySensor(dx: Float, dy: Float, dz: Float) {
        diceBodies.forEach { it.applyGravitySensor(dx, dy, dz) }
    }

    fun destroy() {
        clearAllDice()
    }
}
