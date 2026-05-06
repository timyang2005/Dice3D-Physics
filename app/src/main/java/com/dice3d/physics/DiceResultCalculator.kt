package com.dice3d.physics

import com.bulletphysics.linearmath.Transform
import com.dice3d.model.DiceType
import javax.vecmath.Matrix3f
import javax.vecmath.Vector3f
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object DiceResultCalculator {

    private val d6FaceNormals = listOf(
        Vector3f(0f, 1f, 0f), Vector3f(0f, -1f, 0f),
        Vector3f(1f, 0f, 0f), Vector3f(-1f, 0f, 0f),
        Vector3f(0f, 0f, 1f), Vector3f(0f, 0f, -1f)
    )
    private val d6FaceValues = listOf(1, 6, 3, 4, 2, 5)

    private val d4FaceNormals = listOf(
        Vector3f(1f, 1f, 1f), Vector3f(1f, -1f, -1f),
        Vector3f(-1f, 1f, -1f), Vector3f(-1f, -1f, 1f)
    )
    private val d4FaceValues = listOf(1, 2, 3, 4)

    private val d8FaceNormals = listOf(
        Vector3f(1f, 0f, 0f), Vector3f(-1f, 0f, 0f),
        Vector3f(0f, 1f, 0f), Vector3f(0f, -1f, 0f),
        Vector3f(0f, 0f, 1f), Vector3f(0f, 0f, -1f),
        Vector3f(0.577f, 0.577f, 0.577f), Vector3f(-0.577f, -0.577f, -0.577f)
    )
    private val d8FaceValues = listOf(1, 2, 3, 4, 5, 6, 7, 8)

    private val d12FaceNormals = generateDodecahedronFaceNormals()
    private val d12FaceValues = (1..12).toList()

    private val d20FaceNormals = generateIcosahedronFaceNormals()
    private val d20FaceValues = (1..20).toList()

    private val d10FaceNormals = generateD10FaceNormals()
    private val d10FaceValues = (1..10).toList()

    private val d100FaceNormals = d10FaceNormals
    private val d100FaceValues = listOf(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)

    fun calculateUpFace(diceType: DiceType, transform: Transform): Int {
        val upVector = Vector3f(0f, 1f, 0f)
        val rotation = Matrix3f()
        transform.getMatrix(rotation)
        val faceNormals = getFaceNormals(diceType)
        val faceValues = getFaceValues(diceType)
        var bestDot = -1f
        var bestIndex = 0
        for (i in faceNormals.indices) {
            val worldNormal = Vector3f(faceNormals[i])
            rotation.transform(worldNormal)
            worldNormal.normalize()
            val dot = worldNormal.dot(upVector)
            if (dot > bestDot) { bestDot = dot; bestIndex = i }
        }
        return faceValues[bestIndex]
    }

    private fun getFaceNormals(diceType: DiceType): List<Vector3f> = when (diceType) {
        DiceType.D4 -> d4FaceNormals; DiceType.D6 -> d6FaceNormals
        DiceType.D8 -> d8FaceNormals; DiceType.D10 -> d10FaceNormals
        DiceType.D12 -> d12FaceNormals; DiceType.D20 -> d20FaceNormals
        DiceType.D100 -> d100FaceNormals
    }

    private fun getFaceValues(diceType: DiceType): List<Int> = when (diceType) {
        DiceType.D4 -> d4FaceValues; DiceType.D6 -> d6FaceValues
        DiceType.D8 -> d8FaceValues; DiceType.D10 -> d10FaceValues
        DiceType.D12 -> d12FaceValues; DiceType.D20 -> d20FaceValues
        DiceType.D100 -> d100FaceValues
    }

    private fun generateIcosahedronFaceNormals(): List<Vector3f> {
        val phi = (1f + sqrt(5f)) / 2f
        val vertices = listOf(
            Vector3f(-1f, phi, 0f), Vector3f(1f, phi, 0f),
            Vector3f(-1f, -phi, 0f), Vector3f(1f, -phi, 0f),
            Vector3f(0f, -1f, phi), Vector3f(0f, 1f, phi),
            Vector3f(0f, -1f, -phi), Vector3f(0f, 1f, -phi),
            Vector3f(phi, 0f, -1f), Vector3f(phi, 0f, 1f),
            Vector3f(-phi, 0f, -1f), Vector3f(-phi, 0f, 1f)
        )
        vertices.forEach { it.normalize() }
        val faces = listOf(
            intArrayOf(0,11,5), intArrayOf(0,5,1), intArrayOf(0,1,7), intArrayOf(0,7,10), intArrayOf(0,10,11),
            intArrayOf(1,5,9), intArrayOf(5,11,4), intArrayOf(11,10,2), intArrayOf(10,7,6), intArrayOf(7,1,8),
            intArrayOf(3,9,4), intArrayOf(3,4,2), intArrayOf(3,2,6), intArrayOf(3,6,8), intArrayOf(3,8,9),
            intArrayOf(4,9,5), intArrayOf(2,4,11), intArrayOf(6,2,10), intArrayOf(8,6,7), intArrayOf(9,8,1)
        )
        return faces.map { face ->
            val e1 = Vector3f().apply { sub(vertices[face[1]], vertices[face[0]]) }
            val e2 = Vector3f().apply { sub(vertices[face[2]], vertices[face[0]]) }
            Vector3f().apply { cross(e1, e2); normalize() }
        }
    }

    private fun generateDodecahedronFaceNormals(): List<Vector3f> {
        val phi = (1f + sqrt(5f)) / 2f; val iphi = 1f / phi
        val normals = mutableListOf<Vector3f>()
        for (sx in intArrayOf(-1,1)) for (sy in intArrayOf(-1,1)) for (sz in intArrayOf(-1,1))
            normals.add(Vector3f(sx.toFloat(), sy.toFloat(), sz.toFloat()).apply { normalize() })
        for (sx in intArrayOf(-1,1)) for (sy in intArrayOf(-1,1)) {
            normals.add(Vector3f(0f, sx*iphi, sy*phi).apply { normalize() })
            normals.add(Vector3f(sx*iphi, sy*phi, 0f).apply { normalize() })
            normals.add(Vector3f(sx*phi, 0f, sy*iphi).apply { normalize() })
        }
        return normals
    }

    private fun generateD10FaceNormals(): List<Vector3f> {
        val normals = mutableListOf<Vector3f>()
        for (i in 0 until 5) {
            val angle = 2.0 * Math.PI * i / 5
            normals.add(Vector3f(cos(angle).toFloat(), 0.5f, sin(angle).toFloat()).apply { normalize() })
            normals.add(Vector3f(cos(angle).toFloat(), -0.5f, sin(angle).toFloat()).apply { normalize() })
        }
        return normals
    }
}
