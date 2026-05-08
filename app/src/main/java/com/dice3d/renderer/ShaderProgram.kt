package com.dice3d.renderer

import android.content.Context
import android.opengl.GLES30
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.InputStream

object ShaderProgram {
    private const val TAG = "ShaderProgram"

    fun buildProgram(context: Context, vertexResId: Int, fragmentResId: Int): Int {
        return try {
            buildProgram(readRawResource(context, vertexResId), readRawResource(context, fragmentResId))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to build program from resources", e)
            0
        }
    }

    fun buildProgram(vertexSource: String, fragmentSource: String): Int {
        val vertexShader = compileShader(GLES30.GL_VERTEX_SHADER, vertexSource)
        if (vertexShader == 0) return 0
        val fragmentShader = compileShader(GLES30.GL_FRAGMENT_SHADER, fragmentSource)
        if (fragmentShader == 0) { GLES30.glDeleteShader(vertexShader); return 0 }
        val program = GLES30.glCreateProgram()
        if (program == 0) { GLES30.glDeleteShader(vertexShader); GLES30.glDeleteShader(fragmentShader); return 0 }
        GLES30.glAttachShader(program, vertexShader)
        GLES30.glAttachShader(program, fragmentShader)
        GLES30.glLinkProgram(program)
        val linkStatus = IntArray(1)
        GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == 0) {
            Log.e(TAG, "Failed to link program: ${GLES30.glGetProgramInfoLog(program)}")
            GLES30.glDeleteProgram(program)
            return 0
        }
        return program
    }

    private fun compileShader(type: Int, source: String): Int {
        val shader = GLES30.glCreateShader(type)
        if (shader == 0) return 0
        GLES30.glShaderSource(shader, source)
        GLES30.glCompileShader(shader)
        val compileStatus = IntArray(1)
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] == 0) {
            Log.e(TAG, "Failed to compile shader: ${GLES30.glGetShaderInfoLog(shader)}")
            GLES30.glDeleteShader(shader)
            return 0
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
