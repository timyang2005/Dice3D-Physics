package com.dice3d.renderer

import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

object MatrixState {
    private val mProjMatrix = FloatArray(16)
    private val mVMatrix = FloatArray(16)
    private var currMatrix = FloatArray(16)
    var lightLocation = floatArrayOf(0f, 0f, 0f)
    var cameraFB: FloatBuffer? = null
    var lightPositionFB: FloatBuffer? = null
    private val mStack = Array(10) { FloatArray(16) }
    private var stackTop = -1

    fun setInitStack() { currMatrix = FloatArray(16); Matrix.setRotateM(currMatrix, 0, 0f, 1f, 0f, 0f) }
    fun pushMatrix() { stackTop++; mStack[stackTop] = currMatrix.copyOf() }
    fun popMatrix() { currMatrix = mStack[stackTop].copyOf(); stackTop-- }
    fun translate(x: Float, y: Float, z: Float) { Matrix.translateM(currMatrix, 0, x, y, z) }
    fun rotate(angle: Float, x: Float, y: Float, z: Float) { Matrix.rotateM(currMatrix, 0, angle, x, y, z) }
    fun scale(x: Float, y: Float, z: Float) { Matrix.scaleM(currMatrix, 0, x, y, z) }

    fun setCamera(cx: Float, cy: Float, cz: Float, tx: Float, ty: Float, tz: Float, upx: Float, upy: Float, upz: Float) {
        Matrix.setLookAtM(mVMatrix, 0, cx, cy, cz, tx, ty, tz, upx, upy, upz)
        val cameraLocation = floatArrayOf(cx, cy, cz)
        val llbb = ByteBuffer.allocateDirect(3 * 4).order(ByteOrder.nativeOrder())
        cameraFB = llbb.asFloatBuffer().apply { put(cameraLocation); position(0) }
    }

    fun setProjectFrustum(left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float) {
        Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far)
    }

    fun getFinalMatrix(): FloatArray {
        val mMVPMatrix = FloatArray(16)
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, currMatrix, 0)
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0)
        return mMVPMatrix
    }

    fun getMMatrix(): FloatArray = currMatrix

    fun getViewProjMatrix(): FloatArray {
        val result = FloatArray(16)
        Matrix.multiplyMM(result, 0, mProjMatrix, 0, mVMatrix, 0)
        return result
    }

    fun setLightLocation(x: Float, y: Float, z: Float) {
        lightLocation = floatArrayOf(x, y, z)
        val llbb = ByteBuffer.allocateDirect(3 * 4).order(ByteOrder.nativeOrder())
        lightPositionFB = llbb.asFloatBuffer().apply { put(lightLocation); position(0) }
    }
}
