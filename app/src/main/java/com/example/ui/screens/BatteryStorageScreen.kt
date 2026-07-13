package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.GlassCard
import com.example.ui.components.LiveGraph
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.GlowingAmber
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.SystemMonospace
import com.example.ui.theme.SystemSansSerif
import com.example.ui.viewmodel.DashboardViewModel
import java.util.Locale

@Composable
fun BatteryStorageScreen(
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val battery by viewModel.batteryInfo.collectAsStateWithLifecycle()
    val storage by viewModel.storageInfo.collectAsStateWithLifecycle()
    val batteryTempHistory by viewModel.batteryTempHistory.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. BATTERY CELL HEALTH & TEMPERATURE HISTORY ---
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            testTag = "battery_diagnostics_card"
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.BatteryFull,
                    contentDescription = "Battery",
                    tint = EmeraldGlow,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "BATTERY CELLS & TEMPERATURE",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SystemSansSerif,
                    fontSize = 14.sp
                )
            }

            val isCharging = battery["isCharging"] as? Boolean ?: false
            val level = battery["level"] as? Int ?: 100
            val temp = battery["temperature"] as? Float ?: 30f
            val voltage = battery["voltage"] as? Float ?: 4.2f
            val power = battery["powerWatts"] as? Float ?: 1.5f

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CELL CAPACITY",
                        color = Color.White.copy(alpha = 0.5f),
                        fontFamily = SystemSansSerif,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$level %",
                        color = if (isCharging) EmeraldGlow else GlowingAmber,
                        fontWeight = FontWeight.Bold,
                        fontFamily = SystemMonospace,
                        fontSize = 24.sp
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "BATTERY TEMP",
                        color = Color.White.copy(alpha = 0.5f),
                        fontFamily = SystemSansSerif,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = String.format(Locale.getDefault(), "%.1f °C", temp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = SystemMonospace,
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Real-time canvas graph for Battery Temperature
            LiveGraph(
                data = batteryTempHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                lineColor = EmeraldGlow,
                maxVal = 45f,
                minVal = 20f
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                InfoRow(label = "Health Rating", value = (battery["health"] as? String) ?: "Healthy")
                InfoRow(label = "Cell Voltage", value = String.format(Locale.getDefault(), "%.3f Volts", voltage))
                InfoRow(label = "Charging State", value = (battery["chargeType"] as? String) ?: "Discharging")
                InfoRow(label = "Active Power Flow", value = String.format(Locale.getDefault(), "%.2f Watts", power))
            }
        }

        // --- 2. STORAGE PARTITION SCANNER ---
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            testTag = "storage_diagnostics_card"
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Storage,
                    contentDescription = "Storage",
                    tint = NeonPurple,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "STORAGE DISK ANALYSIS",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SystemSansSerif,
                    fontSize = 14.sp
                )
            }

            val total = storage["total"] as? Long ?: 128_000_000_000L
            val used = storage["used"] as? Long ?: 45_000_000_000L
            val free = storage["available"] as? Long ?: 83_000_000_000L
            val percent = storage["percentUsed"] as? Float ?: 40f

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "USED STORAGE",
                        color = Color.White.copy(alpha = 0.5f),
                        fontFamily = SystemSansSerif,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatBytes(used),
                        color = NeonPurple,
                        fontWeight = FontWeight.Bold,
                        fontFamily = SystemMonospace,
                        fontSize = 22.sp
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "TOTAL COMPACITY",
                        color = Color.White.copy(alpha = 0.5f),
                        fontFamily = SystemSansSerif,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatBytes(total),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = SystemMonospace,
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Disk Usage Linear Slider Bar (Premium Custom Canvas Track)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .padding(vertical = 2.dp)
            ) {
                // Background Track
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    val radius = size.height / 2f
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.05f),
                        size = size,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius, radius)
                    )
                    // Filled Segment
                    drawRoundRect(
                        color = NeonPurple,
                        size = androidx.compose.ui.geometry.Size(size.width * (percent / 100f), size.height),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius, radius)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                InfoRow(label = "Available Free Space", value = formatBytes(free))
                InfoRow(label = "Used Space Percent", value = String.format(Locale.getDefault(), "%.1f %%", percent))
                InfoRow(label = "Storage Directory", value = "/data/partition/user")
            }
        }
    }
}

private fun formatBytes(bytes: Long): String {
    val kb = bytes / 1024L
    val mb = kb / 1024L
    val gb = mb.toFloat() / 1024f
    return if (gb >= 1f) {
        String.format(Locale.getDefault(), "%.1f GB", gb)
    } else if (mb >= 1L) {
        "$mb MB"
    } else {
        "$kb KB"
    }
}
