package com.dice3d.renderer

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES30
import android.opengl.GLUtils
import com.dice3d.R
import com.dice3d.model.DiceType
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class DiceModel(context: Context, diceType: DiceType) {
    private var program: Int = 0
    var verticesBuffer: FloatBuffer; private set
    var normalsBuffer: FloatBuffer; private set
    var texCoordsBuffer: FloatBuffer; private set
    var vertexCount: Int = 0; private set
    private var mMVPMatrixHandle: Int = 0
    private var mModelMatrixHandle: Int = 0
    private var mPositionHandle: Int = 0
    private var mNormalHandle: Int = 0
    private var mTextureCoordHandle: Int = 0
    private var mLightLocationHandle: Int = 0
    private var mCameraHandle: Int = 0
    private var mDiceColorHandle: Int = 0
    private var mTextureHandle: Int = 0
    private var diceColor = floatArrayOf(0.9f, 0.15f, 0.15f)
    private var textureId: Int = 0
    private var initialized = false

    init {
        val modelData = GlSafeExecutor.executeSafelyWithDefault(null) { ObjLoader.load(context, diceType.modelName) }
        if (modelData != null) {
            vertexCount = modelData.vertexCount
            verticesBuffer = createFloatBuffer(modelData.vertices)
            normalsBuffer = createFloatBuffer(modelData.normals)
            texCoordsBuffer = createFloatBuffer(modelData.texCoords)
        } else {
            verticesBuffer = createFloatBuffer(FloatArray(0))
            normalsBuffer = createFloatBuffer(FloatArray(0))
            texCoordsBuffer = createFloatBuffer(FloatArray(0))
        }
        GlSafeExecutor.executeSafely {
            textureId = loadTexture(context, R.drawable.face_1)
            initShader(context)
            initialized = program != 0
        }
    }

    private fun createFloatBuffer(data: FloatArray): FloatBuffer {
        val buffer = ByteBuffer.allocateDirect(data.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        buffer.put(data).position(0); return buffer
    }

    private fun loadTexture(context: Context, resourceId: Int): Int {
        val textureIds = IntArray(1)
        GLES30.glGenTextures(1, textureIds, 0)
        val textureId = textureIds[0]
        if (textureId == 0) return 0
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT)
        val options = BitmapFactory.Options().apply { inPreferredConfig = android.graphics.Bitmap.Config.ARGB_8888 }
        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)
        if (bitmap == null) {
            GLES30.glDeleteTextures(1, textureIds, 0)
            return 0
        }
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()
        return textureId
    }

    fun setTextureResourceId(context: Context, resourceId: Int) {
        GlSafeExecutor.executeSafely {
            if (textureId != 0) {
                GLES30.glDeleteTextures(1, intArrayOf(textureId), 0)
            }
            textureId = loadTexture(context, resourceId)
        }
    }

    private fun initShader(context: Context) {
        program = ShaderProgram.buildProgram(context, R.raw.dice_vertex, R.raw.dice_fragment)
        if (program == 0) return
        mMVPMatrixHandle = GLES30.glGetUniformLocation(program, "uMVPMatrix")
        mModelMatrixHandle = GLES30.glGetUniformLocation(program, "uMMatrix")
        mPositionHandle = GLES30.glGetAttribLocation(program, "aPosition")
        mNormalHandle = GLES30.glGetAttribLocation(program, "aNormal")
        mTextureCoordHandle = GLES30.glGetAttribLocation(program, "aTextureCoord")
        mLightLocationHandle = GLES30.glGetUniformLocation(program, "uLightLocation")
        mCameraHandle = GLES30.glGetUniformLocation(program, "uCamera")
        mDiceColorHandle = GLES30.glGetUniformLocation(program, "uDiceColor")
        mTextureHandle = GLES30.glGetUniformLocation(program, "uTexture")
    }

    fun setColors(bodyColor: FloatArray, numColor: FloatArray) { diceColor = bodyColor }

    fun draw() {
        if (!initialized || program == 0 || vertexCount == 0) return
        GlSafeExecutor.executeSafely {
            GLES30.glUseProgram(program)
            GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0)
            GLES30.glUniformMatrix4fv(mModelMatrixHandle, 1, false, MatrixState.getMMatrix(), 0)
            MatrixState.lightPositionFB?.let { GLES30.glUniform3fv(mLightLocationHandle, 1, it) }
            MatrixState.cameraFB?.let { GLES30.glUniform3fv(mCameraHandle, 1, it) }
            GLES30.glUniform3fv(mDiceColorHandle, 1, diceColor, 0)
            if (textureId != 0) {
                GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId)
                GLES30.glUniform1i(mTextureHandle, 0)
            }
            GLES30.glVertexAttribPointer(mPositionHandle, 3, GLES30.GL_FLOAT, false, 3 * 4, verticesBuffer)
            GLES30.glVertexAttribPointer(mNormalHandle, 3, GLES30.GL_FLOAT, false, 3 * 4, normalsBuffer)
            GLES30.glVertexAttribPointer(mTextureCoordHandle, 2, GLES30.GL_FLOAT, false, 2 * 4, texCoordsBuffer)
            GLES30.glEnableVertexAttribArray(mPositionHandle)
            GLES30.glEnableVertexAttribArray(mNormalHandle)
            GLES30.glEnableVertexAttribArray(mTextureCoordHandle)
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexCount)
        }
    }
}
