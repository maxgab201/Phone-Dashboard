package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.GlassCard
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.GlowingAmber
import com.example.ui.theme.CyberPink
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.SystemMonospace
import com.example.ui.theme.SystemSansSerif
import com.example.ui.viewmodel.DashboardViewModel
import java.util.Locale

@Composable
fun SensorsScreen(
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val accel by viewModel.sensorAccelerometer.collectAsStateWithLifecycle()
    val gyro by viewModel.sensorGyroscope.collectAsStateWithLifecycle()
    val mag by viewModel.sensorMagnetometer.collectAsStateWithLifecycle()
    val lux by viewModel.sensorLight.collectAsStateWithLifecycle()
    val pressure by viewModel.sensorPressure.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    // Keep dynamic rolling histories for visual waves
    val accelHistory = remember { mutableStateListOf<FloatArray>() }
    val gyroHistory = remember { mutableStateListOf<FloatArray>() }

    // Tick lists on change
    LaunchedEffect(accel) {
        accelHistory.add(accel.clone())
        if (accelHistory.size > 40) accelHistory.removeAt(0)
    }

    LaunchedEffect(gyro) {
        gyroHistory.add(gyro.clone())
        if (gyroHistory.size > 40) gyroHistory.removeAt(0)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. ACCELEROMETER TRIPLET WITH LIVE WAVE ---
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            testTag = "accelerometer_sensor_card"
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CompassCalibration,
                    contentDescription = "Accelerometer",
                    tint = NeonBlue,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "3-AXIS ACCELEROMETER (m/s²)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SystemSansSerif,
                    fontSize = 14.sp
                )
            }

            // Numeric Coordinate display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CoordStat(label = "X-AXIS", value = accel[0], color = CyberPink)
                CoordStat(label = "Y-AXIS", value = accel[1], color = EmeraldGlow)
                CoordStat(label = "Z-AXIS", value = accel[2], color = NeonBlue)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Wave Canvas showing X, Y, Z curves overlapping
            MultiWaveCanvas(
                histories = accelHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                maxBounds = 15f
            )
        }

        // --- 2. GYROSCOPE DYNAMICS WITH LIVE WAVE ---
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            testTag = "gyroscope_sensor_card"
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CompassCalibration,
                    contentDescription = "Gyroscope",
                    tint = NeonPurple,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "GYROSCOPE ANGULAR FORCE (rad/s)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SystemSansSerif,
                    fontSize = 14.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CoordStat(label = "PITCH (X)", value = gyro[0], color = CyberPink)
                CoordStat(label = "ROLL (Y)", value = gyro[1], color = EmeraldGlow)
                CoordStat(label = "YAW (Z)", value = gyro[2], color = NeonPurple)
            }

            Spacer(modifier = Modifier.height(16.dp))

            MultiWaveCanvas(
                histories = gyroHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                maxBounds = 5f
            )
        }

        // --- 3. AMBIENT & BAROMETRIC ENVIRONMENTAL METERS ---
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            testTag = "environmental_sensors_card"
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CompassCalibration,
                    contentDescription = "Environment",
                    tint = GlowingAmber,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "ENVIRONMENTAL SENSOR CELLS",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SystemSansSerif,
                    fontSize = 14.sp
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                InfoRow(label = "Luminosity Lux Sensor", value = String.format(Locale.getDefault(), "%.1f Lux", lux))
                InfoRow(label = "Barometer Air Pressure", value = String.format(Locale.getDefault(), "%.2f hPa", pressure))
                InfoRow(label = "Magnetic Node Vectors", value = String.format(Locale.getDefault(), "X:%.0f | Y:%.0f | Z:%.0f", mag[0], mag[1], mag[2]))
            }
        }
    }
}

@Composable
fun CoordStat(label: String, value: Float, color: Color) {
    Column {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.5f),
            fontFamily = SystemSansSerif,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = String.format(Locale.getDefault(), "%+.3f", value),
            color = color,
            fontWeight = FontWeight.Bold,
            fontFamily = SystemMonospace,
            fontSize = 14.sp
        )
    }
}

// Custom canvas paints three coordinates line tracking simultaneously
@Composable
fun MultiWaveCanvas(
    histories: List<FloatArray>,
    modifier: Modifier = Modifier,
    maxBounds: Float = 10f
) {
    Canvas(modifier = modifier) {
        if (histories.size < 2) return@Canvas

        val w = size.width
        val h = size.height
        val midY = h / 2f

        val colors = listOf(CyberPink, EmeraldGlow, NeonBlue)

        // Draw horizontal reference baseline
        drawLine(
            color = Color.White.copy(alpha = 0.1f),
            start = Offset(0f, midY),
            end = Offset(w, midY),
            strokeWidth = 1f
        )

        // Draw each dimension line (X, Y, Z)
        for (dim in 0..2) {
            val path = Path()
            val firstVal = histories.first()[dim]
            val firstY = midY - (firstVal / maxBounds) * midY
            path.moveTo(0f, firstY.coerceIn(0f, h))

            for (idx in 1 until histories.size) {
                val x = (idx.toFloat() / (histories.size - 1)) * w
                val valItem = histories[idx][dim]
                val y = midY - (valItem / maxBounds) * midY
                path.lineTo(x, y.coerceIn(0f, h))
            }

            drawPath(
                path = path,
                color = colors[dim],
                style = Stroke(width = 1.5.dp.toPx())
            )
        }
    }
}
