package com.dice3d.renderer

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.util.Log
import com.bulletphysics.linearmath.Transform
import com.dice3d.model.DiceConfig
import com.dice3d.model.DiceType
import com.dice3d.physics.PhysicsWorld
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import javax.vecmath.Quat4f
import kotlin.math.sin
import kotlin.math.cos

class DiceRenderer(private val context: Context, private val physicsWorld: PhysicsWorld, private val cameraController: CameraController) : GLSurfaceView.Renderer {
    private val diceModels = mutableMapOf<DiceType, DiceModel>()
    private var tableRenderer: TableRenderer? = null
    private var shadowRenderer: ShadowRenderer? = null
    private var lightAzimuth = -30f; private var lightElevation = 50f; private var lightDistance = 100f
    var isDarkMode = false
    var diceConfig: DiceConfig = DiceConfig()
    private var lightX = 0f; private var lightY = 0f; private var lightZ = 0f
    private var glInitialized = false

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GlSafeExecutor.executeSafely {
            GLES30.glClearColor(0.3f, 0.3f, 0.3f, 1.0f)
            GLES30.glEnable(GLES30.GL_DEPTH_TEST)
            GLES30.glDisable(GLES30.GL_CULL_FACE)
            GLES30.glEnable(GLES30.GL_BLEND)
            GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA)
            MatrixState.setInitStack()
            GlSafeExecutor.executeSafely { diceModels[DiceType.D6] = DiceModel(context, DiceType.D6) }
            tableRenderer = TableRenderer(context)
            shadowRenderer = ShadowRenderer(context)
            glInitialized = true
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GlSafeExecutor.executeSafely {
            GLES30.glViewport(0, 0, width, height)
            MatrixState.setProjectFrustum(-width.toFloat() / height, width.toFloat() / height, -1f, 1f, 2f, 200f)
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        if (!glInitialized) return
        GlSafeExecutor.executeSafely {
            val bgColor = if (isDarkMode) 0.07f else 0.3f
            GLES30.glClearColor(bgColor, bgColor, bgColor, 1.0f)
            GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT or GLES30.GL_COLOR_BUFFER_BIT)
            updateLightPosition()
            MatrixState.setLightLocation(lightX, lightY, lightZ)
            cameraController.applyToMatrixState()
            tableRenderer?.let { table ->
                table.brightness = if (isDarkMode) 0.4f else 1.0f
                MatrixState.pushMatrix(); table.draw(); MatrixState.popMatrix()
            }
            drawAllDice()
        }
    }

    private fun updateLightPosition() {
        val elevRad = Math.toRadians(lightElevation.toDouble()); val azimRad = Math.toRadians(lightAzimuth.toDouble())
        lightX = (-lightDistance * cos(elevRad) * sin(azimRad)).toFloat()
        lightY = (lightDistance * sin(elevRad)).toFloat()
        lightZ = (-lightDistance * cos(elevRad) * cos(azimRad)).toFloat()
    }

    private fun drawAllDice() {
        GLES30.glEnable(GLES30.GL_BLEND); GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA)
        val bodyColor = floatArrayOf(diceConfig.bodyColor.red, diceConfig.bodyColor.green, diceConfig.bodyColor.blue)
        val numColor = floatArrayOf(diceConfig.numberColor.red, diceConfig.numberColor.green, diceConfig.numberColor.blue)
        val snapshot = physicsWorld.diceBodies.toList()
        for (dice in snapshot) {
            val model = diceModels[dice.diceType] ?: continue
            if (model.vertexCount == 0) continue
            model.setColors(bodyColor, numColor)
            val transform = dice.getTransform()
            MatrixState.pushMatrix(); applyPhysicsTransform(transform); model.draw(); MatrixState.popMatrix()
            MatrixState.pushMatrix(); applyPhysicsTransform(transform); shadowRenderer?.draw(model.verticesBuffer, model.vertexCount); MatrixState.popMatrix()
        }
    }

    private fun applyPhysicsTransform(transform: Transform) {
        MatrixState.translate(transform.origin.x, transform.origin.y, transform.origin.z)
        val rotation = Quat4f(); transform.getRotation(rotation)
        if (rotation.x != 0f || rotation.y != 0f || rotation.z != 0f) {
            val aa = fromQuatToAxisAngle(rotation); MatrixState.rotate(aa[0], aa[1], aa[2], aa[3])
        }
    }

    private fun fromQuatToAxisAngle(q: Quat4f): FloatArray {
        val sitaHalf = Math.acos(q.w.toDouble()); val sinHalf = Math.sin(sitaHalf)
        if (Math.abs(sinHalf) < 0.0001) return floatArrayOf(0f, 1f, 0f, 0f)
        return floatArrayOf(Math.toDegrees(sitaHalf * 2).toFloat(), (q.x / sinHalf).toFloat(), (q.y / sinHalf).toFloat(), (q.z / sinHalf).toFloat())
    }
}
