package com.example.data.system

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SensorData(
    val name: String,
    val values: FloatArray,
    val unit: String
)

class SensorsInfoProvider(context: Context) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    
    private val _sensorsData = MutableStateFlow<Map<Int, SensorData>>(emptyMap())
    val sensorsData: StateFlow<Map<Int, SensorData>> = _sensorsData.asStateFlow()
    
    val availableSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)

    fun startListening() {
        val typesToListen = listOf(
            Sensor.TYPE_ACCELEROMETER,
            Sensor.TYPE_GYROSCOPE,
            Sensor.TYPE_MAGNETIC_FIELD,
            Sensor.TYPE_LIGHT,
            Sensor.TYPE_PROXIMITY,
            Sensor.TYPE_PRESSURE,
            Sensor.TYPE_AMBIENT_TEMPERATURE
        )
        
        for (type in typesToListen) {
            val sensor = sensorManager.getDefaultSensor(type)
            if (sensor != null) {
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val type = it.sensor.type
            val unit = when(type) {
                Sensor.TYPE_ACCELEROMETER -> "m/s²"
                Sensor.TYPE_GYROSCOPE -> "rad/s"
                Sensor.TYPE_MAGNETIC_FIELD -> "μT"
                Sensor.TYPE_LIGHT -> "lx"
                Sensor.TYPE_PROXIMITY -> "cm"
                Sensor.TYPE_PRESSURE -> "hPa"
                Sensor.TYPE_AMBIENT_TEMPERATURE -> "°C"
                else -> ""
            }
            
            val newData = SensorData(
                name = it.sensor.name,
                values = it.values.clone(),
                unit = unit
            )
            
            val currentMap = _sensorsData.value.toMutableMap()
            currentMap[type] = newData
            _sensorsData.value = currentMap
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }
}
