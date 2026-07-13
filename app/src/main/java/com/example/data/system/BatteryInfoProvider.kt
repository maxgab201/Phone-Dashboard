package com.example.data.system

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class BatteryState(
    val level: Int = 0,
    val isCharging: Boolean = false,
    val temperature: Float = 0f,
    val voltage: Int = 0,
    val health: String = "Unknown",
    val technology: String = "Unknown"
)

class BatteryInfoProvider(private val context: Context) {
    private val _batteryState = MutableStateFlow(BatteryState())
    val batteryState: StateFlow<BatteryState> = _batteryState.asStateFlow()

    fun updateBatteryInfo() {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context.registerReceiver(null, ifilter)
        }

        batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val batteryPct: Float = level * 100 / scale.toFloat()
            
            val status: Int = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                                      status == BatteryManager.BATTERY_STATUS_FULL
                                      
            val temp: Int = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
            val voltage: Int = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
            
            val healthInt: Int = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0)
            val healthStr = when (healthInt) {
                BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
                BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
                BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
                BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
                BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Failure"
                BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
                else -> "Unknown"
            }
            
            val technology: String = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Unknown"

            _batteryState.value = BatteryState(
                level = batteryPct.toInt(),
                isCharging = isCharging,
                temperature = temp / 10f, // Temperature is in tenths of a degree centigrade
                voltage = voltage,
                health = healthStr,
                technology = technology
            )
        }
    }
}
