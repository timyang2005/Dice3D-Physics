package com.dice3d.renderer

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import kotlin.math.sqrt

class DiceGLSurfaceView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : GLSurfaceView(context, attrs) {
    var onRollRequested: (() -> Unit)? = null
    private val cameraController = CameraController()
    private var previousX = 0f; private var previousY = 0f; private var previousSpan = 0f
    private val touchSlop = 10; private var isDragging = false; private var downX = 0f; private var downY = 0f

    fun getCameraController(): CameraController = cameraController

    init { setEGLContextClientVersion(3); preserveEGLContextOnPause = true }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x; val y = event.y; val pointerCount = event.pointerCount
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> { previousX=x; previousY=y; downX=x; downY=y; isDragging=false }
            MotionEvent.ACTION_POINTER_DOWN -> { previousSpan=getSpan(event) }
            MotionEvent.ACTION_MOVE -> {
                val dx=x-previousX; val dy=y-previousY
                if (!isDragging && (kotlin.math.abs(x-downX)>touchSlop || kotlin.math.abs(y-downY)>touchSlop)) isDragging=true
                if (isDragging) {
                    when {
                        pointerCount==1 -> cameraController.rotate(dx,dy)
                        pointerCount==2 -> { val cs=getSpan(event); if(previousSpan>0) cameraController.zoom(cs-previousSpan); previousSpan=cs; cameraController.pan(dx,dy) }
                    }
                }
                previousX=x; previousY=y
            }
            MotionEvent.ACTION_UP -> { if(!isDragging) onRollRequested?.invoke() }
        }
        return true
    }

    private fun getSpan(event: MotionEvent): Float {
        if(event.pointerCount<2) return 0f
        val dx=event.getX(0)-event.getX(1); val dy=event.getY(0)-event.getY(1)
        return sqrt((dx*dx+dy*dy).toDouble()).toFloat()
    }
}
