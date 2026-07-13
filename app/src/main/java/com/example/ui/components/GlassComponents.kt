package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

// --- DYNAMIC BACKGROUND GLOWS ---
// Draws floating radial glow spheres in the background using native canvas.
// Fades to transparent at outer bounds to simulate extreme camera blur.
@Composable
fun BackgroundGlows(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "GlowMovement")

    // Animate positions of 3 distinct orb centers
    val xOffset1 by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "x1"
    )
    val yOffset1 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "y1"
    )

    val xOffset2 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "x2"
    )
    val yOffset2 by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(14000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "y2"
    )

    val xOffset3 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(22000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "x3"
    )
    val yOffset3 by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(16000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "y3"
    )

    Canvas(modifier = modifier.fillMaxSize().background(ObsidianBackground)) {
        val width = size.width
        val height = size.height

        // Glowing Orb 1 (Neon Blue)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(NeonBlue.copy(alpha = 0.25f), Color.Transparent),
                center = Offset(width * xOffset1, height * yOffset1),
                radius = width * 0.45f
            ),
            center = Offset(width * xOffset1, height * yOffset1),
            radius = width * 0.45f
        )

        // Glowing Orb 2 (Neon Purple)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(NeonPurple.copy(alpha = 0.20f), Color.Transparent),
                center = Offset(width * xOffset2, height * yOffset2),
                radius = width * 0.50f
            ),
            center = Offset(width * xOffset2, height * yOffset2),
            radius = width * 0.50f
        )

        // Glowing Orb 3 (Cyber Pink)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(CyberPink.copy(alpha = 0.15f), Color.Transparent),
                center = Offset(width * xOffset3, height * yOffset3),
                radius = width * 0.40f
            ),
            center = Offset(width * xOffset3, height * yOffset3),
            radius = width * 0.40f
        )
    }
}

// --- GLASSMORPHIC CARD ---
// Transparent backing with a bright thin border to simulate reflected light, and shadow.
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    borderAlpha: Float = 0.18f,
    onClick: (() -> Unit)? = null,
    testTag: String = "",
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = remember { mutableStateOf(false) }

    // Click micro-animation (Scale down slightly on touch)
    val scale by animateFloatAsState(
        targetValue = if (isPressed.value) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium),
        label = "scale"
    )

    var cardModifier = modifier
        .graphicsLayer(scaleX = scale, scaleY = scale)
        .testTag(testTag)
        .clip(RoundedCornerShape(cornerRadius))
        .background(
            Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.07f),
                    Color.White.copy(alpha = 0.02f)
                )
            )
        )
        .border(
            width = 1.dp,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = borderAlpha),
                    Color.White.copy(alpha = borderAlpha * 0.3f)
                )
            ),
            shape = RoundedCornerShape(cornerRadius)
        )

    if (onClick != null) {
        cardModifier = cardModifier.clickable(
            interactionSource = interactionSource,
            indication = null, // Custom ripple or scale handles the feedback
            onClick = onClick
        )
    }

    Column(
        modifier = cardModifier.padding(16.dp),
        content = content
    )
}

// --- GLASS BUTTON ---
@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = NeonBlue,
    testTag: String = ""
) {
    Box(
        modifier = modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.15f),
                        accentColor.copy(alpha = 0.05f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.40f),
                        accentColor.copy(alpha = 0.10f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontFamily = SystemSansSerif,
            fontSize = 14.sp,
            letterSpacing = 0.5.sp
        )
    }
}

// --- REAL-TIME GLOWING LINE GRAPH ---
// Rendered on Canvas with ambient shadow casting.
@Composable
fun LiveGraph(
    data: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = NeonBlue,
    maxVal: Float = 100f,
    minVal: Float = 0f
) {
    Canvas(modifier = modifier) {
        if (data.size < 2) return@Canvas

        val width = size.width
        val height = size.height
        val sizeDiff = maxVal - minVal

        val points = data.mapIndexed { idx, value ->
            val x = (idx.toFloat() / (data.size - 1)) * width
            // Inverse Y because Canvas (0,0) is top-left
            val normalizedVal = (value - minVal) / sizeDiff
            val y = height - (normalizedVal.coerceIn(0f, 1f) * height)
            Offset(x, y)
        }

        // Draw background gradient under the curve
        val fillPath = Path().apply {
            moveTo(0f, height)
            lineTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                lineTo(points[i].x, points[i].y)
            }
            lineTo(width, height)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    lineColor.copy(alpha = 0.25f),
                    Color.Transparent
                )
            )
        )

        // Draw glowing line outline
        val linePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                lineTo(points[i].x, points[i].y)
            }
        }

        drawPath(
            path = linePath,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw live endpoint pulse circle
        val endPoint = points.last()
        drawCircle(
            color = lineColor,
            radius = 5.dp.toPx(),
            center = endPoint
        )
        drawCircle(
            color = lineColor.copy(alpha = 0.4f),
            radius = 10.dp.toPx(),
            center = endPoint,
            style = Stroke(width = 1.5.dp.toPx())
        )
    }
}

// --- CIRCULAR GAUGE COMPONENT ---
@Composable
fun CircularGauge(
    percentage: Float,
    label: String,
    valueText: String,
    modifier: Modifier = Modifier,
    accentColor: Color = NeonBlue,
    strokeWidth: Dp = 10.dp
) {
    val animatedPercentage by animateFloatAsState(
        targetValue = percentage,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "GaugeAnim"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(100.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val sizeVal = size.minDimension
                val strokePx = strokeWidth.toPx()
                val radius = (sizeVal - strokePx) / 2f

                // Track Background
                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    radius = radius,
                    style = Stroke(width = strokePx)
                )

                // Foreground Fill Arc (starting from top, i.e., -90 degrees)
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(accentColor.copy(alpha = 0.4f), accentColor, accentColor.copy(alpha = 0.4f)),
                        center = center
                    ),
                    startAngle = -90f,
                    sweepAngle = (animatedPercentage / 100f) * 360f,
                    useCenter = false,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = valueText,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SystemMonospace,
                    fontSize = 14.sp
                )
                Text(
                    text = label,
                    color = Color.White.copy(alpha = 0.5f),
                    fontFamily = SystemSansSerif,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// --- DYNAMIC ISLAND NOTIFICATION PILL ---
@Composable
fun DynamicIsland(
    visible: Boolean,
    message: String,
    subMessage: String = "",
    accentColor: Color = NeonBlue,
    onDismiss: () -> Unit
) {
    LaunchedEffect(visible) {
        if (visible) {
            delay(3500)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 40.dp)
            .statusBarsPadding()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(accentColor.copy(alpha = 0.6f), Color.Transparent)
                    ),
                    shape = RoundedCornerShape(28.dp)
                ),
            color = Color(0xEB0A0B10), // Heavy glass dark background
            shape = RoundedCornerShape(28.dp),
            tonalElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Alert",
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = message,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = SystemSansSerif,
                        fontSize = 14.sp
                    )
                    if (subMessage.isNotEmpty()) {
                        Text(
                            text = subMessage,
                            color = Color.White.copy(alpha = 0.6f),
                            fontFamily = SystemSansSerif,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
