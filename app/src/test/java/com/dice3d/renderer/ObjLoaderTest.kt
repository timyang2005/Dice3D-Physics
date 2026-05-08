package com.dice3d.renderer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ObjLoaderTest {

    @Test
    fun `triangulated quad produces 6 vertices from 4 original`() {
        val objContent = """
            v 0.0 0.0 0.0
            v 1.0 0.0 0.0
            v 1.0 1.0 0.0
            v 0.0 1.0 0.0
            vt 0.0 0.0
            vt 1.0 0.0
            vt 1.0 1.0
            vt 0.0 1.0
            f 1/1 2/2 3/3 4/4
        """.trimIndent()
        val result = ObjLoader.parseFromString(objContent, 1.0f)
        assertNotNull(result)
        assertEquals(6, result!!.vertexCount)
    }

    @Test
    fun `triangle face produces 3 vertices`() {
        val objContent = """
            v 0.0 0.0 0.0
            v 1.0 0.0 0.0
            v 0.5 1.0 0.0
            vt 0.0 0.0
            vt 1.0 0.0
            vt 0.5 1.0
            f 1/1 2/2 3/3
        """.trimIndent()
        val result = ObjLoader.parseFromString(objContent, 1.0f)
        assertNotNull(result)
        assertEquals(3, result!!.vertexCount)
    }

    @Test
    fun `multiple quads produce correct vertex count`() {
        val objContent = """
            v -1.0 -1.0 1.0
            v 1.0 -1.0 1.0
            v 1.0 1.0 1.0
            v -1.0 1.0 1.0
            v -1.0 -1.0 -1.0
            v 1.0 -1.0 -1.0
            v 1.0 1.0 -1.0
            v -1.0 1.0 -1.0
            vt 0.0 0.0
            vt 1.0 0.0
            vt 1.0 1.0
            vt 0.0 1.0
            f 1/1 2/2 3/3 4/4
            f 5/1 8/4 7/3 6/2
        """.trimIndent()
        val result = ObjLoader.parseFromString(objContent, 1.0f)
        assertNotNull(result)
        assertEquals(12, result!!.vertexCount)
    }

    @Test
    fun `scale factor is applied to vertices`() {
        val objContent = """
            v 1.0 2.0 3.0
            v 4.0 5.0 6.0
            v 7.0 8.0 9.0
            vt 0.0 0.0
            vt 1.0 0.0
            vt 0.5 1.0
            f 1/1 2/2 3/3
        """.trimIndent()
        val result = ObjLoader.parseFromString(objContent, 0.5f)
        assertNotNull(result)
        assertEquals(0.5f, result!!.vertices[0], 0.001f)
        assertEquals(1.0f, result.vertices[1], 0.001f)
        assertEquals(1.5f, result.vertices[2], 0.001f)
    }

    @Test
    fun `normals are generated for triangulated quads`() {
        val objContent = """
            v -1.0 0.0 -1.0
            v 1.0 0.0 -1.0
            v 1.0 0.0 1.0
            v -1.0 0.0 1.0
            vt 0.0 0.0
            vt 1.0 0.0
            vt 1.0 1.0
            vt 0.0 1.0
            f 1/1 2/2 3/3 4/4
        """.trimIndent()
        val result = ObjLoader.parseFromString(objContent, 1.0f)
        assertNotNull(result)
        assertEquals(result.vertexCount * 3, result.normals.size)
    }

    @Test
    fun `face without texture coords still loads`() {
        val objContent = """
            v 0.0 0.0 0.0
            v 1.0 0.0 0.0
            v 0.5 1.0 0.0
            f 1 2 3
        """.trimIndent()
        val result = ObjLoader.parseFromString(objContent, 1.0f)
        assertNotNull(result)
        assertEquals(3, result!!.vertexCount)
    }

    @Test
    fun `pentagon face is triangulated into 3 triangles`() {
        val objContent = """
            v 0.0 0.0 0.0
            v 1.0 0.0 0.0
            v 1.5 0.5 0.0
            v 0.5 1.0 0.0
            v -0.5 0.5 0.0
            vt 0.0 0.0
            vt 1.0 0.0
            vt 1.0 1.0
            vt 0.5 1.0
            vt 0.0 0.5
            f 1/1 2/2 3/3 4/4 5/5
        """.trimIndent()
        val result = ObjLoader.parseFromString(objContent, 1.0f)
        assertNotNull(result)
        assertEquals(9, result!!.vertexCount)
    }
}
