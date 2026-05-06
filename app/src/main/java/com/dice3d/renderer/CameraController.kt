package com.dice3d.renderer

import kotlin.math.cos
import kotlin.math.sin

class CameraController {
    var distance: Float = 20f; private set
    var elevationAngle: Float = 45f; private set
    var azimuthAngle: Float = 180f; private set
    var targetX: Float = 0f; private set
    var targetY: Float = 0f; private set
    var targetZ: Float = 0f; private set
    var cameraX: Float = 0f; private set
    var cameraY: Float = 0f; private set
    var cameraZ: Float = 0f; private set

    companion object {
        private const val MIN_DISTANCE = 8f; private const val MAX_DISTANCE = 60f
        private const val MIN_ELEVATION = 5f; private const val MAX_ELEVATION = 89f
        private const val DEFAULT_DISTANCE = 20f; private const val DEFAULT_ELEVATION = 45f
        private const val DEFAULT_AZIMUTH = 180f
        private const val PAN_SENSITIVITY = 0.02f; private const val ROTATION_SENSITIVITY = 0.3f
        private const val ZOOM_SENSITIVITY = 0.05f
    }

    fun updateCameraPosition() {
        val elevRad = Math.toRadians(elevationAngle.toDouble())
        val azimRad = Math.toRadians(azimuthAngle.toDouble())
        cameraX = (targetX - distance*cos(elevRad)*sin(azimRad)).toFloat()
        cameraY = (targetY + distance*sin(elevRad)).toFloat()
        cameraZ = (targetZ - distance*cos(elevRad)*cos(azimRad)).toFloat()
    }

    fun rotate(dx: Float, dy: Float) {
        azimuthAngle += dx * ROTATION_SENSITIVITY
        elevationAngle -= dy * ROTATION_SENSITIVITY
        elevationAngle = elevationAngle.coerceIn(MIN_ELEVATION, MAX_ELEVATION)
    }

    fun zoom(delta: Float) {
        distance *= if (delta>0) (1f+ZOOM_SENSITIVITY) else (1f-ZOOM_SENSITIVITY)
        distance = distance.coerceIn(MIN_DISTANCE, MAX_DISTANCE)
    }

    fun pan(dx: Float, dy: Float) {
        val elevRad = Math.toRadians(elevationAngle.toDouble())
        val azimRad = Math.toRadians(azimuthAngle.toDouble())
        val rightX = cos(azimRad).toFloat(); val rightZ = -sin(azimRad).toFloat()
        targetX += (-dx*rightX + dy*sin(elevRad)*sin(azimRad)).toFloat() * PAN_SENSITIVITY
        targetY += (dy*cos(elevRad)).toFloat() * PAN_SENSITIVITY
        targetZ += (-dx*rightZ + dy*sin(elevRad)*cos(azimRad)).toFloat() * PAN_SENSITIVITY
        targetX = targetX.coerceIn(-15f,15f); targetY = targetY.coerceIn(-5f,15f); targetZ = targetZ.coerceIn(-10f,10f)
    }

    fun resetToDefault() { distance=DEFAULT_DISTANCE; elevationAngle=DEFAULT_ELEVATION; azimuthAngle=DEFAULT_AZIMUTH; targetX=0f; targetY=0f; targetZ=0f }

    fun applyToMatrixState() {
        updateCameraPosition()
        MatrixState.setCamera(cameraX, cameraY, cameraZ, targetX, targetY, targetZ, 0f, 1f, 0f)
    }
}
