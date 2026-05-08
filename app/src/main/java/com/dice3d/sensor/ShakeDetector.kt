package com.dice3d.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class ShakeDetector(context: Context, private val onShake: () -> Unit) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val logic = ShakeDetectorLogic()

    private val listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (logic.processReading(event.values, System.currentTimeMillis())) {
                onShake()
            }
        }
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    fun start() { sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI) }
    fun stop() { sensorManager.unregisterListener(listener) }
    fun getLatestAccelerometer(): FloatArray = floatArrayOf(logic.lastX, logic.lastY, logic.lastZ)
}
