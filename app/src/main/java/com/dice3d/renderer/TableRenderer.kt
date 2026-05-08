package com.dice3d.renderer

import android.content.Context
import android.opengl.GLES30
import com.dice3d.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class TableRenderer(context: Context) {
    private var program: Int = 0
    private var verticesBuffer: FloatBuffer
    private var mMVPMatrixHandle: Int = 0
    private var mPositionHandle: Int = 0
    private var mBrightnessHandle: Int = 0
    var brightness: Float = 1.0f
    private var initialized = false

    init {
        val vertices = floatArrayOf(-30f,0f,-20f, 30f,0f,-20f, 30f,0f,20f, -30f,0f,-20f, 30f,0f,20f, -30f,0f,20f)
        verticesBuffer = createFloatBuffer(vertices)
        GlSafeExecutor.executeSafely {
            program = ShaderProgram.buildProgram(context, R.raw.table_vertex, R.raw.table_fragment)
            mMVPMatrixHandle = GLES30.glGetUniformLocation(program, "uMVPMatrix")
            mPositionHandle = GLES30.glGetAttribLocation(program, "aPosition")
            mBrightnessHandle = GLES30.glGetUniformLocation(program, "uBrightness")
            initialized = program != 0
        }
    }

    private fun createFloatBuffer(data: FloatArray): FloatBuffer {
        val buffer = ByteBuffer.allocateDirect(data.size*4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        buffer.put(data).position(0); return buffer
    }

    fun draw() {
        if (!initialized || program == 0) return
        GlSafeExecutor.executeSafely {
            GLES30.glUseProgram(program)
            GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0)
            GLES30.glUniform1f(mBrightnessHandle, brightness)
            GLES30.glVertexAttribPointer(mPositionHandle, 3, GLES30.GL_FLOAT, false, 3*4, verticesBuffer)
            GLES30.glEnableVertexAttribArray(mPositionHandle)
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 6)
        }
    }
}
