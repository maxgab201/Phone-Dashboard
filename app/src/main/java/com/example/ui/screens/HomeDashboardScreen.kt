package com.example.ui.screens

import android.os.Build
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CircularGauge
import com.example.ui.components.GlassCard
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.GlowingAmber
import com.example.ui.theme.CyberPink
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.SystemMonospace
import com.example.ui.theme.SystemSansSerif
import com.example.ui.viewmodel.DashboardViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeDashboardScreen(
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val cpu by viewModel.cpuInfo.collectAsStateWithLifecycle()
    val ram by viewModel.ramInfo.collectAsStateWithLifecycle()
    val storage by viewModel.storageInfo.collectAsStateWithLifecycle()
    val battery by viewModel.batteryInfo.collectAsStateWithLifecycle()
    val network by viewModel.networkInfo.collectAsStateWithLifecycle()
    
    val flashlightOn by viewModel.isFlashlightOn.collectAsStateWithLifecycle()
    val widgetAlpha by viewModel.widgetTransparency.collectAsStateWithLifecycle()

    var timeText by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf("") }

    // Real-time clock update
    LaunchedEffect(Unit) {
        while (true) {
            val cal = Calendar.getInstance()
            timeText = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(cal.time)
            dateText = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(cal.time)
            kotlinx.coroutines.delay(1000)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- GIANT DIGITAL CLOCK SECTION ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = timeText,
                color = Color.White,
                fontSize = 54.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = SystemMonospace,
                letterSpacing = (-1).sp,
                modifier = Modifier.testTag("clock_text")
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Calendar",
                    tint = NeonBlue,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = dateText,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp,
                    fontFamily = SystemSansSerif,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // --- CORE TELEMETRY CIRCULAR GAUGES ---
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            testTag = "gauges_card"
        ) {
            Text(
                text = "SYSTEM ENGINE HEALTH",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp,
                fontFamily = SystemMonospace,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val cpuUsage = cpu["usage"] as? Float ?: 0f
                CircularGauge(
                    percentage = cpuUsage,
                    label = "CPU",
                    valueText = String.format(Locale.getDefault(), "%.0f%%", cpuUsage),
                    accentColor = NeonBlue
                )

                val ramPercent = ram["percentUsed"] as? Float ?: 0f
                CircularGauge(
                    percentage = ramPercent,
                    label = "RAM",
                    valueText = String.format(Locale.getDefault(), "%.0f%%", ramPercent),
                    accentColor = NeonPurple
                )

                val batteryLevel = battery["level"] as? Int ?: 0
                CircularGauge(
                    percentage = batteryLevel.toFloat(),
                    label = "BATT",
                    valueText = "$batteryLevel%",
                    accentColor = if (battery["isCharging"] as? Boolean == true) EmeraldGlow else GlowingAmber
                )

                val storagePercent = storage["percentUsed"] as? Float ?: 0f
                CircularGauge(
                    percentage = storagePercent,
                    label = "DISK",
                    valueText = String.format(Locale.getDefault(), "%.0f%%", storagePercent),
                    accentColor = CyberPink
                )
            }
        }

        // --- SMART CONTROL TOGGLES MATRIX ---
        Text(
            text = "QUICK CONTROL CENTER",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 11.sp,
            fontFamily = SystemMonospace,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            item {
                QuickToggleWidget(
                    title = "Flashlight",
                    subtitle = if (flashlightOn) "ACTIVE" else "OFF",
                    icon = Icons.Default.FlashlightOn,
                    isActive = flashlightOn,
                    activeColor = GlowingAmber,
                    onClick = { viewModel.toggleFlashlight() },
                    testTag = "flashlight_widget"
                )
            }
            item {
                val netType = network["type"] as? String ?: "Offline"
                QuickToggleWidget(
                    title = "Network",
                    subtitle = netType.uppercase(Locale.getDefault()),
                    icon = if (netType == "Offline") Icons.Default.CloudOff else Icons.Default.Wifi,
                    isActive = netType != "Offline",
                    activeColor = NeonBlue,
                    onClick = {
                        viewModel.triggerIslandAlert("Network Status", "Using $netType cellular/WiFi bands.")
                    },
                    testTag = "network_widget"
                )
            }
            item {
                val chargeType = battery["chargeType"] as? String ?: "Discharging"
                val isCharging = battery["isCharging"] as? Boolean ?: false
                QuickToggleWidget(
                    title = "Charging",
                    subtitle = if (isCharging) chargeType.uppercase(Locale.getDefault()) else "DISCHARGING",
                    icon = if (isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryAlert,
                    isActive = isCharging,
                    activeColor = EmeraldGlow,
                    onClick = {
                        val temp = battery["temperature"] as? Float ?: 0f
                        val volt = battery["voltage"] as? Float ?: 0f
                        viewModel.triggerIslandAlert("Battery Core Status", "Temp: ${temp}°C | Voltage: ${volt}V")
                    },
                    testTag = "battery_widget"
                )
            }
            item {
                QuickToggleWidget(
                    title = "Theme Vibe",
                    subtitle = "DARK SLATE",
                    icon = Icons.Default.DarkMode,
                    isActive = true,
                    activeColor = NeonPurple,
                    onClick = {
                        viewModel.triggerIslandAlert("Holographic Theme", "Dark Obsidian Slate is the default hardware optimized skin.")
                    },
                    testTag = "theme_widget"
                )
            }
        }
    }
}

@Composable
fun QuickToggleWidget(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isActive: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    testTag: String = ""
) {
    Box(
        modifier = Modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isActive) {
                    Brush.verticalGradient(
                        colors = listOf(
                            activeColor.copy(alpha = 0.18f),
                            activeColor.copy(alpha = 0.05f)
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.04f),
                            Color.White.copy(alpha = 0.01f)
                        )
                    )
                }
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        if (isActive) activeColor.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.1f),
                        if (isActive) activeColor.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.02f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (isActive) activeColor.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isActive) activeColor else Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SystemSansSerif,
                    fontSize = 13.sp
                )
                Text(
                    text = subtitle,
                    color = if (isActive) activeColor else Color.White.copy(alpha = 0.4f),
                    fontWeight = FontWeight.Bold,
                    fontFamily = SystemMonospace,
                    fontSize = 10.sp
                )
            }
        }
    }
}
