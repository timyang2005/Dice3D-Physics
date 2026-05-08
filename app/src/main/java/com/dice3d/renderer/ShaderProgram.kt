package com.dice3d.renderer

import android.content.Context
import android.opengl.GLES30
import java.io.ByteArrayOutputStream
import java.io.InputStream

object ShaderProgram {
    fun buildProgram(context: Context, vertexResId: Int, fragmentResId: Int): Int {
        return buildProgram(readRawResource(context, vertexResId), readRawResource(context, fragmentResId))
    }

    fun buildProgram(vertexSource: String, fragmentSource: String): Int {
        val vertexShader = compileShader(GLES30.GL_VERTEX_SHADER, vertexSource)
        val fragmentShader = compileShader(GLES30.GL_FRAGMENT_SHADER, fragmentSource)
        val program = GLES30.glCreateProgram()
        check(program != 0) { "Failed to create program" }
        GLES30.glAttachShader(program, vertexShader)
        GLES30.glAttachShader(program, fragmentShader)
        GLES30.glLinkProgram(program)
        val linkStatus = IntArray(1)
        GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == 0) {
            GLES30.glDeleteProgram(program)
            throw RuntimeException("Failed to link program: ${GLES30.glGetProgramInfoLog(program)}")
        }
        return program
    }

    private fun compileShader(type: Int, source: String): Int {
        val shader = GLES30.glCreateShader(type)
        check(shader != 0) { "Failed to create shader" }
        GLES30.glShaderSource(shader, source)
        GLES30.glCompileShader(shader)
        val compileStatus = IntArray(1)
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] == 0) {
            val log = GLES30.glGetShaderInfoLog(shader)
            GLES30.glDeleteShader(shader)
            throw RuntimeException("Failed to compile shader: $log")
        }
        return shader
    }

    private fun readRawResource(context: Context, resId: Int): String {
        val inputStream: InputStream = context.resources.openRawResource(resId)
        val baos = ByteArrayOutputStream()
        var b: Int = inputStream.read()
        while (b != -1) { baos.write(b); b = inputStream.read() }
        inputStream.close()
        return baos.toString("UTF-8")
    }
}
