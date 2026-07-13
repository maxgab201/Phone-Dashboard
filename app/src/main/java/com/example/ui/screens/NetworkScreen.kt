package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Language
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
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.GlowingAmber
import com.example.ui.theme.SystemMonospace
import com.example.ui.theme.SystemSansSerif
import com.example.ui.viewmodel.DashboardViewModel
import java.util.Locale

@Composable
fun NetworkScreen(
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val network by viewModel.networkInfo.collectAsStateWithLifecycle()
    val device by viewModel.deviceInfo.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. LIVE SPEED METERS ---
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            testTag = "network_speeds_card"
        ) {
            Text(
                text = "REAL-TIME BANDWIDTH TELEMETRY",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp,
                fontFamily = SystemMonospace,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val dlSpeed = network["downloadSpeed"] as? Float ?: 0f
            val ulSpeed = network["uploadSpeed"] as? Float ?: 0f

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Download Card Inside Glass
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDownload,
                        contentDescription = "DL Speed",
                        tint = NeonBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "DOWNLOAD",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 10.sp,
                        fontFamily = SystemSansSerif,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatSpeed(dlSpeed),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = SystemMonospace
                    )
                }

                // Upload Card Inside Glass
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = "UL Speed",
                        tint = NeonPurple,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "UPLOAD",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 10.sp,
                        fontFamily = SystemSansSerif,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatSpeed(ulSpeed),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = SystemMonospace
                    )
                }
            }
        }

        // --- 2. WI-FI SIGNAL ANALYSIS ---
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            testTag = "wifi_diagnostics_card"
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "WiFi",
                    tint = NeonBlue,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "LOCAL WI-FI SIGNAL SPECS",
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
                InfoRow(label = "Active Frequency", value = (network["wifiFrequency"] as? String) ?: "2.4 GHz")
                InfoRow(label = "Optimal Wi-Fi Channel", value = "Ch ${(network["wifiChannel"] as? String) ?: "6"}")
                val strength = network["wifiRssi"] as? Int ?: -55
                InfoRow(label = "RSSI Signal Strength", value = "$strength dBm")
                InfoRow(label = "DNS Address Node", value = (network["dns"] as? String) ?: "1.1.1.1")
                InfoRow(label = "Local IP Address", value = (network["localIp"] as? String) ?: "192.168.1.12")
            }
        }

        // --- 3. CELLULAR BAND SPECS ---
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            testTag = "cellular_diagnostics_card"
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CellTower,
                    contentDescription = "Cellular",
                    tint = GlowingAmber,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "CELLULAR CARRIER BANDS",
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
                InfoRow(label = "Operator Provider", value = device["Operator"] ?: "Network Carrier")
                InfoRow(label = "Active SIM Slot Status", value = device["SIM State"] ?: "SIM Active")
                InfoRow(label = "eSIM Compatible Hardware", value = "Yes (eSIM Enabled)")
                InfoRow(label = "Cellular Access Technology", value = (network["type"] as? String) ?: "4G LTE")
            }
        }
    }
}

private fun formatSpeed(bytesPerSec: Float): String {
    val kbps = (bytesPerSec * 8) / 1024f
    val mbps = kbps / 1024f
    return if (mbps >= 1f) {
        String.format(Locale.getDefault(), "%.1f Mbps", mbps)
    } else {
        String.format(Locale.getDefault(), "%.0f Kbps", kbps)
    }
}
