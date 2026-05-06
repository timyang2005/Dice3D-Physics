package com.dice3d.renderer

import android.content.Context
import android.opengl.GLES30
import com.dice3d.R
import java.nio.FloatBuffer

class ShadowRenderer(context: Context) {
    private var program: Int = 0
    private var mProjCameraMatrixHandle: Int = 0
    private var mModelMatrixHandle: Int = 0
    private var mLightLocationHandle: Int = 0
    private var mPositionHandle: Int = 0

    init {
        program = ShaderProgram.buildProgram(context, R.raw.shadow_vertex, R.raw.shadow_fragment)
        mProjCameraMatrixHandle = GLES30.glGetUniformLocation(program, "uMProjCameraMatrix")
        mModelMatrixHandle = GLES30.glGetUniformLocation(program, "uMMatrix")
        mLightLocationHandle = GLES30.glGetUniformLocation(program, "uLightLocation")
        mPositionHandle = GLES30.glGetAttribLocation(program, "aPosition")
    }

    fun draw(verticesBuffer: FloatBuffer, vertexCount: Int) {
        GLES30.glUseProgram(program)
        GLES30.glUniformMatrix4fv(mProjCameraMatrixHandle, 1, false, MatrixState.getViewProjMatrix(), 0)
        GLES30.glUniformMatrix4fv(mModelMatrixHandle, 1, false, MatrixState.getMMatrix(), 0)
        MatrixState.lightPositionFB?.let { GLES30.glUniform3fv(mLightLocationHandle, 1, it) }
        GLES30.glVertexAttribPointer(mPositionHandle, 3, GLES30.GL_FLOAT, false, 3*4, verticesBuffer)
        GLES30.glEnableVertexAttribArray(mPositionHandle)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexCount)
    }
}
