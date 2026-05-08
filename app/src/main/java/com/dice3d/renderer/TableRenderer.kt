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
    private var mIsDarkModeHandle: Int = 0
    var brightness: Float = 1.0f
    var isDarkMode: Boolean = false

    init {
        val vertices = floatArrayOf(-30f,0f,-20f, 30f,0f,-20f, 30f,0f,20f, -30f,0f,-20f, 30f,0f,20f, -30f,0f,20f)
        verticesBuffer = createFloatBuffer(vertices)
        initShader(context)
    }

    private fun createFloatBuffer(data: FloatArray): FloatBuffer {
        val buffer = ByteBuffer.allocateDirect(data.size*4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        buffer.put(data).position(0); return buffer
    }

    private fun initShader(context: Context) {
        program = ShaderProgram.buildProgram(context, R.raw.table_vertex, R.raw.table_fragment)
        mMVPMatrixHandle = GLES30.glGetUniformLocation(program, "uMVPMatrix")
        mPositionHandle = GLES30.glGetAttribLocation(program, "aPosition")
        mBrightnessHandle = GLES30.glGetUniformLocation(program, "uBrightness")
        mIsDarkModeHandle = GLES30.glGetUniformLocation(program, "uIsDarkMode")
    }

    fun draw() {
        GLES30.glUseProgram(program)
        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0)
        GLES30.glUniform1f(mBrightnessHandle, brightness)
        GLES30.glUniform1f(mIsDarkModeHandle, if(isDarkMode) 1.0f else 0.0f)
        GLES30.glVertexAttribPointer(mPositionHandle, 3, GLES30.GL_FLOAT, false, 3*4, verticesBuffer)
        GLES30.glEnableVertexAttribArray(mPositionHandle)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 6)
    }
}
