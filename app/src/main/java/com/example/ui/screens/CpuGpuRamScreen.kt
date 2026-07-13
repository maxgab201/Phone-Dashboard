package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.Speed
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
import com.example.ui.theme.CyberPink
import com.example.ui.theme.SystemMonospace
import com.example.ui.theme.SystemSansSerif
import com.example.ui.viewmodel.DashboardViewModel
import java.util.Locale

@Composable
fun CpuGpuRamScreen(
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val cpu by viewModel.cpuInfo.collectAsStateWithLifecycle()
    val ram by viewModel.ramInfo.collectAsStateWithLifecycle()
    val gpu by viewModel.gpuInfo.collectAsStateWithLifecycle()

    val cpuHistory by viewModel.cpuHistory.collectAsStateWithLifecycle()
    val ramHistory by viewModel.ramHistory.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. CPU HARDWARE GRAPH CARD ---
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            testTag = "cpu_diagnostics_card"
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = "CPU",
                    tint = NeonBlue,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "CPU ENGINE DIAGNOSTICS",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SystemSansSerif,
                    fontSize = 14.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CORE LOAD",
                        color = Color.White.copy(alpha = 0.5f),
                        fontFamily = SystemSansSerif,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    val cpuUsage = cpu["usage"] as? Float ?: 0f
                    Text(
                        text = String.format(Locale.getDefault(), "%.1f %%", cpuUsage),
                        color = NeonBlue,
                        fontWeight = FontWeight.Bold,
                        fontFamily = SystemMonospace,
                        fontSize = 24.sp
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "CORES ACTIVE",
                        color = Color.White.copy(alpha = 0.5f),
                        fontFamily = SystemSansSerif,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${cpu["cores"] ?: "8"} CORES",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = SystemMonospace,
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Real-time canvas graph for CPU
            LiveGraph(
                data = cpuHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                lineColor = NeonBlue
            )

            Spacer(modifier = Modifier.height(12.dp))

            // CPU properties list
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Architecture:",
                    color = Color.White.copy(alpha = 0.6f),
                    fontFamily = SystemSansSerif,
                    fontSize = 12.sp
                )
                Text(
                    text = (cpu["architecture"] as? String) ?: "AArch64",
                    color = Color.White,
                    fontFamily = SystemMonospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "CPU Chip Model:",
                    color = Color.White.copy(alpha = 0.6f),
                    fontFamily = SystemSansSerif,
                    fontSize = 12.sp
                )
                Text(
                    text = (cpu["model"] as? String) ?: "ARM Cortex",
                    color = Color.White,
                    fontFamily = SystemMonospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // --- 2. RAM & SWAP GRAPH CARD ---
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            testTag = "ram_diagnostics_card"
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Memory,
                    contentDescription = "RAM",
                    tint = NeonPurple,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "RAM & SWAP METRICS",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SystemSansSerif,
                    fontSize = 14.sp
                )
            }

            val totalBytes = ram["total"] as? Long ?: 8_000_000_000L
            val usedBytes = ram["used"] as? Long ?: 4_000_000_000L
            val availableBytes = ram["available"] as? Long ?: 4_000_000_000L
            val percentUsed = ram["percentUsed"] as? Float ?: 50f

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "USED MEMORY",
                        color = Color.White.copy(alpha = 0.5f),
                        fontFamily = SystemSansSerif,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatBytes(usedBytes),
                        color = NeonPurple,
                        fontWeight = FontWeight.Bold,
                        fontFamily = SystemMonospace,
                        fontSize = 20.sp
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "TOTAL INSTALLED",
                        color = Color.White.copy(alpha = 0.5f),
                        fontFamily = SystemSansSerif,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatBytes(totalBytes),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = SystemMonospace,
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Real-time canvas graph for RAM
            LiveGraph(
                data = ramHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                lineColor = NeonPurple
            )

            Spacer(modifier = Modifier.height(12.dp))

            val swapTotal = ram["swapTotal"] as? Long ?: 1_000_000_000L
            val swapUsed = ram["swapUsed"] as? Long ?: 200_000_000L
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Swap Space (Total/Used):",
                    color = Color.White.copy(alpha = 0.6f),
                    fontFamily = SystemSansSerif,
                    fontSize = 12.sp
                )
                Text(
                    text = "${formatBytes(swapUsed)} / ${formatBytes(swapTotal)}",
                    color = Color.White,
                    fontFamily = SystemMonospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "System Low-Mem Alert:",
                    color = Color.White.copy(alpha = 0.6f),
                    fontFamily = SystemSansSerif,
                    fontSize = 12.sp
                )
                Text(
                    text = if (ram["isLowMemory"] as? Boolean == true) "CRITICAL WARNING" else "STABLE",
                    color = if (ram["isLowMemory"] as? Boolean == true) Color.Red else Color.Green,
                    fontFamily = SystemMonospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // --- 3. GPU SHADER GRAPHICS ENGINE CARD ---
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            testTag = "gpu_diagnostics_card"
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SettingsSuggest,
                    contentDescription = "GPU",
                    tint = CyberPink,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "GPU RENDERING CORE",
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
                InfoRow(label = "GPU Renderer Chip", value = gpu["Renderer"] ?: "Adreno Core")
                InfoRow(label = "OpenGL ES Version", value = gpu["OpenGL ES"] ?: "3.2 API")
                InfoRow(label = "Vulkan Support", value = gpu["Vulkan Version"] ?: "1.1 API")
                InfoRow(label = "Real-Time Display Output", value = gpu["Estimated FPS"] ?: "60 FPS")
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.6f),
            fontFamily = SystemSansSerif,
            fontSize = 12.sp
        )
        Text(
            text = value,
            color = Color.White,
            fontFamily = SystemMonospace,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
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
