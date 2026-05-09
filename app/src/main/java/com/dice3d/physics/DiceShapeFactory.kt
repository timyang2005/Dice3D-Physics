package com.dice3d.physics

import com.bulletphysics.collision.shapes.BoxShape
import com.bulletphysics.collision.shapes.CollisionShape
import com.bulletphysics.collision.shapes.ConvexHullShape
import com.dice3d.model.DiceType
import com.bulletphysics.util.ObjectArrayList
import javax.vecmath.Vector3f
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object DiceShapeFactory {

    private const val D6_HALF_EXTENT = 0.5f

    fun createShape(diceType: DiceType): CollisionShape {
        return when (diceType) {
            DiceType.D4 -> createTetrahedron()
            DiceType.D6 -> BoxShape(Vector3f(D6_HALF_EXTENT, D6_HALF_EXTENT, D6_HALF_EXTENT))
            DiceType.D8 -> createOctahedron()
            DiceType.D10 -> createPentagonalTrapezohedron()
            DiceType.D12 -> createDodecahedron()
            DiceType.D20 -> createIcosahedron()
            DiceType.D100 -> createPentagonalTrapezohedron()
        }
    }

    fun createShape(diceType: DiceType, scale: Float): CollisionShape {
        val baseShape = createShape(diceType)
        baseShape.setLocalScaling(Vector3f(scale, scale, scale))
        return baseShape
    }

    private fun toObjectArrayList(vertices: List<Vector3f>): ObjectArrayList<Vector3f> {
        val list = ObjectArrayList<Vector3f>(vertices.size)
        vertices.forEach { list.add(it) }
        return list
    }

    private fun createTetrahedron(): ConvexHullShape {
        val s = 0.7f
        return ConvexHullShape(toObjectArrayList(listOf(
            Vector3f(s, s, s), Vector3f(s, -s, -s),
            Vector3f(-s, s, -s), Vector3f(-s, -s, s)
        )))
    }

    private fun createOctahedron(): ConvexHullShape {
        val s = 0.7f
        return ConvexHullShape(toObjectArrayList(listOf(
            Vector3f(s, 0f, 0f), Vector3f(-s, 0f, 0f),
            Vector3f(0f, s, 0f), Vector3f(0f, -s, 0f),
            Vector3f(0f, 0f, s), Vector3f(0f, 0f, -s)
        )))
    }

    private fun createDodecahedron(): ConvexHullShape {
        val phi = (1f + sqrt(5f)) / 2f
        val scale = 0.35f
        val vertices = mutableListOf<Vector3f>()
        listOf(
            floatArrayOf(1f, 1f, 1f), floatArrayOf(1f, 1f, -1f),
            floatArrayOf(1f, -1f, 1f), floatArrayOf(1f, -1f, -1f),
            floatArrayOf(-1f, 1f, 1f), floatArrayOf(-1f, 1f, -1f),
            floatArrayOf(-1f, -1f, 1f), floatArrayOf(-1f, -1f, -1f)
        ).forEach { v -> vertices.add(Vector3f(v[0] * scale, v[1] * scale, v[2] * scale)) }
        listOf(
            floatArrayOf(0f, 1f / phi, phi), floatArrayOf(0f, 1f / phi, -phi),
            floatArrayOf(0f, -1f / phi, phi), floatArrayOf(0f, -1f / phi, -phi),
            floatArrayOf(1f / phi, phi, 0f), floatArrayOf(1f / phi, -phi, 0f),
            floatArrayOf(-1f / phi, phi, 0f), floatArrayOf(-1f / phi, -phi, 0f),
            floatArrayOf(phi, 0f, 1f / phi), floatArrayOf(phi, 0f, -1f / phi),
            floatArrayOf(-phi, 0f, 1f / phi), floatArrayOf(-phi, 0f, -1f / phi)
        ).forEach { v -> vertices.add(Vector3f(v[0] * scale, v[1] * scale, v[2] * scale)) }
        return ConvexHullShape(toObjectArrayList(vertices))
    }

    private fun createIcosahedron(): ConvexHullShape {
        val phi = (1f + sqrt(5f)) / 2f
        val scale = 0.35f
        return ConvexHullShape(toObjectArrayList(listOf(
            Vector3f(-1f * scale, phi * scale, 0f), Vector3f(1f * scale, phi * scale, 0f),
            Vector3f(-1f * scale, -phi * scale, 0f), Vector3f(1f * scale, -phi * scale, 0f),
            Vector3f(0f, -1f * scale, phi * scale), Vector3f(0f, 1f * scale, phi * scale),
            Vector3f(0f, -1f * scale, -phi * scale), Vector3f(0f, 1f * scale, -phi * scale),
            Vector3f(phi * scale, 0f, -1f * scale), Vector3f(phi * scale, 0f, 1f * scale),
            Vector3f(-phi * scale, 0f, -1f * scale), Vector3f(-phi * scale, 0f, 1f * scale)
        )))
    }

    private fun createPentagonalTrapezohedron(): ConvexHullShape {
        val n = 10
        val scale = 0.5f
        val vertices = mutableListOf<Vector3f>()
        for (i in 0 until n) {
            val angle1 = 2.0 * Math.PI * i / n
            val angle2 = 2.0 * Math.PI * (i + 0.5) / n
            vertices.add(Vector3f((0.7f * scale * cos(angle1)).toFloat(), 0.3f * scale, (0.7f * scale * sin(angle1)).toFloat()))
            vertices.add(Vector3f((1.0f * scale * cos(angle2)).toFloat(), -0.3f * scale, (1.0f * scale * sin(angle2)).toFloat()))
        }
        vertices.add(Vector3f(0f, 1.2f * scale, 0f))
        vertices.add(Vector3f(0f, -1.2f * scale, 0f))
        return ConvexHullShape(toObjectArrayList(vertices))
    }
}
