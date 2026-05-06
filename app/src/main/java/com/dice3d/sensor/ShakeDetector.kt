package com.dice3d.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class ShakeDetector(context: Context, private val onShake: () -> Unit) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private var lastX = 0f; private var lastY = 0f; private var lastZ = 0f
    private var lastUpdateTime = 0L; private var lastShakeTime = 0L

    companion object { private const val SHAKE_THRESHOLD = 12f; private const val SHAKE_COOLDOWN_MS = 800L; private const val MIN_INTERVAL_MS = 50L }

    private val listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastUpdateTime < MIN_INTERVAL_MS) return
            lastUpdateTime = currentTime
            val x=event.values[0]; val y=event.values[1]; val z=event.values[2]
            val deltaX=x-lastX; val deltaY=y-lastY; val deltaZ=z-lastZ
            lastX=x; lastY=y; lastZ=z
            val speed = sqrt((deltaX*deltaX+deltaY*deltaY+deltaZ*deltaZ).toDouble()).toFloat()*10
            if (speed>SHAKE_THRESHOLD && currentTime-lastShakeTime>SHAKE_COOLDOWN_MS) { lastShakeTime=currentTime; onShake() }
        }
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    fun start() { sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI) }
    fun stop() { sensorManager.unregisterListener(listener) }
    fun getLatestAccelerometer(): FloatArray = floatArrayOf(lastX, lastY, lastZ)
}
