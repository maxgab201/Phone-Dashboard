package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.DashboardViewModel
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun ToolsScreen(
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    var selectedToolTab by remember { mutableStateOf(0) }
    val tabs = listOf("Compass", "Level", "Ruler", "Cleaner", "Scanner")
    val icons = listOf(
        Icons.Default.Explore,
        Icons.Default.Build,
        Icons.Default.Straighten,
        Icons.Default.DeleteSweep,
        Icons.Default.QrCodeScanner
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Tab Selection Bar ---
        ScrollableTabRow(
            selectedTabIndex = selectedToolTab,
            containerColor = Color.Transparent,
            contentColor = Color.White,
            edgePadding = 0.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.then(
                        with(TabRowDefaults) {
                            Modifier.tabIndicatorOffset(tabPositions[selectedToolTab])
                        }
                    ),
                    color = NeonBlue
                )
            },
            divider = {}
        ) {
            tabs.forEachIndexed { idx, title ->
                Tab(
                    selected = selectedToolTab == idx,
                    onClick = { selectedToolTab = idx },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = icons[idx],
                                contentDescription = title,
                                tint = if (selectedToolTab == idx) NeonBlue else Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedToolTab == idx) Color.White else Color.White.copy(alpha = 0.5f)
                            )
                        }
                    },
                    modifier = Modifier.testTag("tool_tab_$idx")
                )
            }
        }

        // --- Active Tool Viewport ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            when (selectedToolTab) {
                0 -> CompassToolView(viewModel)
                1 -> SpiritLevelToolView(viewModel)
                2 -> RulerToolView()
                3 -> CacheCleanerToolView(viewModel)
                4 -> QrScannerToolView(viewModel)
            }
        }
    }
}

// --- 1. PREMIUM DIGITAL COMPASS ---
@Composable
fun CompassToolView(viewModel: DashboardViewModel) {
    val azimuth by viewModel.compassAzimuth.collectAsStateWithLifecycle()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = String.format(Locale.getDefault(), "%.0f°", azimuth),
            color = Color.White,
            fontSize = 42.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = SystemMonospace,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = getDirectionLabel(azimuth),
            color = NeonBlue,
            fontWeight = FontWeight.Bold,
            fontFamily = SystemSansSerif,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Compass Rose Rotating Dial
        Box(
            modifier = Modifier
                .size(240.dp)
                .graphicsLayer(rotationZ = -azimuth),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.minDimension / 2f

                // Outer Dial Circle
                drawCircle(
                    color = Color.White.copy(alpha = 0.1f),
                    radius = radius,
                    style = Stroke(width = 2.dp.toPx())
                )

                // Sub-ticks
                for (angle in 0 until 360 step 15) {
                    val angleRad = Math.toRadians(angle.toDouble()).toFloat()
                    val tickLen = if (angle % 90 == 0) 18.dp.toPx() else 8.dp.toPx()
                    val strokeW = if (angle % 90 == 0) 2.5.dp.toPx() else 1.dp.toPx()
                    val tickColor = if (angle % 90 == 0) NeonBlue else Color.White.copy(alpha = 0.3f)

                    val startOffset = Offset(
                        center.x + (radius - tickLen) * kotlin.math.sin(angleRad.toDouble()).toFloat(),
                        center.y - (radius - tickLen) * kotlin.math.cos(angleRad.toDouble()).toFloat()
                    )
                    val endOffset = Offset(
                        center.x + radius * kotlin.math.sin(angleRad.toDouble()).toFloat(),
                        center.y - radius * kotlin.math.cos(angleRad.toDouble()).toFloat()
                    )

                    drawLine(
                        color = tickColor,
                        start = startOffset,
                        end = endOffset,
                        strokeWidth = strokeW
                    )
                }
            }

            // Heading Text overlays inside the rotating dial
            Text(text = "N", color = CyberPink, fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.align(Alignment.TopCenter).padding(top = 18.dp))
            Text(text = "S", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 18.dp))
            Text(text = "E", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.align(Alignment.CenterEnd).padding(end = 18.dp))
            Text(text = "W", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.align(Alignment.CenterStart).padding(start = 18.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
        Icon(
            imageVector = Icons.Default.Navigation,
            contentDescription = "Pointer",
            tint = CyberPink,
            modifier = Modifier.size(36.dp)
        )
    }
}

private fun getDirectionLabel(azimuth: Float): String {
    return when (azimuth) {
        in 337.5..360.0, in 0.0..22.5 -> "NORTH"
        in 22.5..67.5 -> "NORTH-EAST"
        in 67.5..112.5 -> "EAST"
        in 112.5..157.5 -> "SOUTH-EAST"
        in 157.5..202.5 -> "SOUTH"
        in 202.5..247.5 -> "SOUTH-WEST"
        in 247.5..292.5 -> "WEST"
        else -> "NORTH-WEST"
    }
}

// --- 2. SPIRIT LEVEL GAUGE ---
@Composable
fun SpiritLevelToolView(viewModel: DashboardViewModel) {
    val pitch by viewModel.pitchAngle.collectAsStateWithLifecycle()
    val roll by viewModel.rollAngle.collectAsStateWithLifecycle()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("PITCH", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(String.format(Locale.getDefault(), "%.1f°", pitch), color = NeonPurple, fontSize = 22.sp, fontFamily = SystemMonospace, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("ROLL", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(String.format(Locale.getDefault(), "%.1f°", roll), color = NeonBlue, fontSize = 22.sp, fontFamily = SystemMonospace, fontWeight = FontWeight.Bold)
            }
        }

        // Circular Spirit Level Bullseye
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.03f))
                .border(2.dp, Color.White.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                
                // Bullseye target inner rings
                drawCircle(color = Color.White.copy(alpha = 0.05f), radius = 24.dp.toPx(), style = Stroke(width = 1.dp.toPx()))
                drawCircle(color = Color.White.copy(alpha = 0.05f), radius = 50.dp.toPx(), style = Stroke(width = 1.dp.toPx()))

                // Center crosshair axes
                drawLine(color = Color.White.copy(alpha = 0.05f), start = Offset(0f, center.y), end = Offset(size.width, center.y))
                drawLine(color = Color.White.copy(alpha = 0.05f), start = Offset(center.x, 0f), end = Offset(center.x, size.height))
            }

            // Moving fluid level bubble
            // Map pitch/roll (-45..45 degrees) to pixels inside the circular container bounds
            val maxOffsetPx = with(LocalDensity.current) { 80.dp.toPx() }
            val xOffset = (roll / 45f).coerceIn(-1f, 1f) * maxOffsetPx
            // Inverse Y because pitch up tilts bubble backward
            val yOffset = (-pitch / 45f).coerceIn(-1f, 1f) * maxOffsetPx

            Box(
                modifier = Modifier
                    .offset(
                        x = with(LocalDensity.current) { xOffset.toDp() },
                        y = with(LocalDensity.current) { yOffset.toDp() }
                    )
                    .size(36.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(EmeraldGlow, EmeraldGlow.copy(alpha = 0.3f))
                        ),
                        CircleShape
                    )
                    .border(1.5.dp, Color.White.copy(alpha = 0.6f), CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = if (Math.abs(pitch) < 1.5f && Math.abs(roll) < 1.5f) "LEVEL ALIGNED" else "ADJUST DEVICE",
            color = if (Math.abs(pitch) < 1.5f && Math.abs(roll) < 1.5f) EmeraldGlow else GlowingAmber,
            fontWeight = FontWeight.Bold,
            fontFamily = SystemSansSerif,
            fontSize = 12.sp
        )
    }
}

// --- 3. HARDWARE CALIBRATED SCREEN RULER ---
@Composable
fun RulerToolView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "METRIC SCREEN RULER",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 11.sp,
            fontFamily = SystemMonospace,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Draw scale calibrated to screen densities (tick marks every 1mm and 1cm)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(Color.White.copy(alpha = 0.02f), RoundedCornerShape(16.dp))
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Density metrics: On Android, 1 inch = 160 density points (dp).
                // 1 cm = 1 inch / 2.54 = 63 dp.
                // We draw centimeter labels and millimeter divisions.
                val pixelsPerCm = 63.dp.toPx()
                val pixelsPerMm = pixelsPerCm / 10f

                var cmCount = 0
                var currentX = 0f

                while (currentX < width) {
                    val isCm = cmCount % 10 == 0
                    val isHalfCm = cmCount % 5 == 0 && !isCm

                    val tickHeight = when {
                        isCm -> 40.dp.toPx()
                        isHalfCm -> 25.dp.toPx()
                        else -> 12.dp.toPx()
                    }

                    val tickColor = when {
                        isCm -> NeonBlue
                        else -> Color.White.copy(alpha = 0.25f)
                    }

                    // Draw Tick on top edge
                    drawLine(
                        color = tickColor,
                        start = Offset(currentX, 0f),
                        end = Offset(currentX, tickHeight),
                        strokeWidth = if (isCm) 2.dp.toPx() else 1.dp.toPx()
                    )

                    // Centimeter labels
                    if (isCm) {
                        val labelNum = cmCount / 10
                        if (labelNum > 0) {
                            // Draw static labels via canvas text or simple coordinates
                        }
                    }

                    currentX += pixelsPerMm
                    cmCount++
                }
            }

            // Row of centimeter labels overlaying top
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 46.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Spacer(modifier = Modifier.width(63.dp))
                for (i in 1..8) {
                    Text(
                        text = "$i cm",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontFamily = SystemMonospace,
                        modifier = Modifier.width(63.dp),
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}

// --- 4. CACHE SWEEP CLEANER ---
@Composable
fun CacheCleanerToolView(viewModel: DashboardViewModel) {
    val scanning by viewModel.isScanningCache.collectAsStateWithLifecycle()
    val sizeText by viewModel.cacheScannedSize.collectAsStateWithLifecycle()

    val infiniteTransition = rememberInfiniteTransition(label = "Sweep")
    val rotAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        ),
        label = "Rot"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.size(160.dp),
            contentAlignment = Alignment.Center
        ) {
            // Rotating outer radar line when scanning
            if (scanning) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(rotAngle)
                        .border(
                            width = 2.dp,
                            brush = Brush.sweepGradient(
                                colors = listOf(NeonBlue, Color.Transparent)
                            ),
                            shape = CircleShape
                        )
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(2.dp, Color.White.copy(alpha = 0.05f), CircleShape)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Trash",
                    tint = if (scanning) NeonBlue else Color.White,
                    modifier = Modifier.size(42.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = sizeText,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SystemMonospace
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = if (scanning) "SWEEPING DEVICE CACHE FILES..." else "TEMPORARY CACHE DIRECTORIES",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 11.sp,
            fontFamily = SystemMonospace,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        GlassButton(
            text = if (scanning) "CLEANING STATE..." else "START SWEEP CLEANER",
            onClick = { viewModel.runCacheCleaner() },
            accentColor = NeonBlue,
            testTag = "run_cleaner_button"
        )
    }
}

// --- 5. LENS QR SCANNER SIMULATOR ---
@Composable
fun QrScannerToolView(viewModel: DashboardViewModel) {
    val active by viewModel.isQrScannerActive.collectAsStateWithLifecycle()
    val scanResult by viewModel.qrScanResult.collectAsStateWithLifecycle()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if (!active && scanResult == null) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = "QR Scanner",
                tint = NeonPurple,
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "HOLOGRAPHIC QR SCANNING BARCODE",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                fontFamily = SystemMonospace,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            GlassButton(
                text = "ACTIVATE SCANNER",
                onClick = { viewModel.toggleQrScanner(true) },
                accentColor = NeonPurple,
                testTag = "activate_qr_button"
            )
        } else if (active) {
            // Visual viewport scanner frame
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.Black.copy(alpha = 0.3f))
                    .border(2.dp, NeonPurple, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Moving scan laser line
                val infiniteTransition = rememberInfiniteTransition(label = "Laser")
                val laserYPercent by infiniteTransition.animateFloat(
                    initialValue = 0.1f,
                    targetValue = 0.9f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1500, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "laser"
                )

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val lineY = laserYPercent * size.height
                    drawLine(
                        color = NeonPurple,
                        start = Offset(0f, lineY),
                        end = Offset(size.width, lineY),
                        strokeWidth = 3.dp.toPx()
                    )
                }

                Text(
                    text = "ALIGN QR LENS",
                    color = Color.White.copy(alpha = 0.7f),
                    fontFamily = SystemMonospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )

                // Trigger automatic mock result in 1.5s
                LaunchedEffect(Unit) {
                    viewModel.simulateQrCodeScan()
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "SWEEPING DECODING...", color = NeonPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = SystemMonospace)
        } else if (scanResult != null) {
            // Decoded results card
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = EmeraldGlow,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "DECODED STRING SUCCESSFULLY", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = SystemMonospace)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = scanResult!!,
                color = EmeraldGlow,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = SystemMonospace,
                modifier = Modifier.padding(horizontal = 24.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            GlassButton(
                text = "SCAN ANOTHER",
                onClick = { viewModel.toggleQrScanner(true) },
                accentColor = NeonPurple
            )
        }
    }
}
