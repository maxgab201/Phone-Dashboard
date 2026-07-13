package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DisplaySettings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.GlowingAmber
import com.example.ui.theme.SystemMonospace
import com.example.ui.theme.SystemSansSerif
import com.example.ui.viewmodel.DashboardViewModel

@Composable
fun SettingsScreen(
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val alphaVal by viewModel.widgetTransparency.collectAsStateWithLifecycle()
    val isCelsius by viewModel.temperatureUnitCelsius.collectAsStateWithLifecycle()
    val intervalMs by viewModel.updateIntervalMs.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. PERSONALIZATION CARD ---
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            testTag = "personalization_settings_card"
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DisplaySettings,
                    contentDescription = "Personalization",
                    tint = NeonBlue,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "DASHBOARD PERSONALIZATION",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SystemSansSerif,
                    fontSize = 14.sp
                )
            }

            // Transparency adjust Slider
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Widget Glass Translucency", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    Text(text = String.format("%.0f%%", alphaVal * 1000f), color = NeonBlue, fontFamily = SystemMonospace, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = alphaVal,
                    onValueChange = { viewModel.widgetTransparency.value = it },
                    valueRange = 0.04f..0.25f,
                    modifier = Modifier.testTag("translucency_slider")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Temperature unit switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Temperature Units Celsius (°C)", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                Switch(
                    checked = isCelsius,
                    onCheckedChange = { viewModel.temperatureUnitCelsius.value = it },
                    modifier = Modifier.testTag("temp_unit_switch")
                )
            }
        }

        // --- 2. ANALYTICS FREQUENCY ---
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            testTag = "frequency_settings_card"
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DisplaySettings,
                    contentDescription = "Update Interval",
                    tint = NeonPurple,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "TELEMETRY REFRESH REGIME",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SystemSansSerif,
                    fontSize = 14.sp
                )
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Update Intervals Rate", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    Text(text = "${intervalMs} ms", color = NeonPurple, fontFamily = SystemMonospace, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = intervalMs.toFloat(),
                    onValueChange = { viewModel.updateIntervalMs.value = it.toLong() },
                    valueRange = 500f..5000f,
                    steps = 8,
                    modifier = Modifier.testTag("update_interval_slider")
                )
            }
        }

        // --- 3. EXPORT / REPORT SHARING ---
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            testTag = "export_settings_card"
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Export Report",
                    tint = GlowingAmber,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "EXPORT SYSTEM REPORT DATA",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SystemSansSerif,
                    fontSize = 14.sp
                )
            }

            Text(
                text = "Generate and share a structured JSON or CSV format diagnostic telemetry dump with external storage or email nodes.",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GlassButton(
                    text = "SHARE CSV DUMP",
                    onClick = {
                        val report = viewModel.generateExportContent("CSV")
                        dispatchShareIntent(context, report, "text/csv")
                        viewModel.triggerIslandAlert("CSV Report Shared", "CSV Diagnostic telemetry dispatched successfully.")
                    },
                    accentColor = GlowingAmber,
                    modifier = Modifier.weight(1f),
                    testTag = "share_csv_button"
                )

                GlassButton(
                    text = "SHARE JSON DUMP",
                    onClick = {
                        val report = viewModel.generateExportContent("JSON")
                        dispatchShareIntent(context, report, "application/json")
                        viewModel.triggerIslandAlert("JSON Report Shared", "JSON Diagnostic telemetry dispatched successfully.")
                    },
                    accentColor = NeonPurple,
                    modifier = Modifier.weight(1f),
                    testTag = "share_json_button"
                )
            }
        }

        // --- 4. SOFTWARE INFO FOOTER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Version",
                tint = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "PHONE DASHBOARD ENGINE V1.0.0 (STABLE)",
                color = Color.White.copy(alpha = 0.3f),
                fontSize = 10.sp,
                fontFamily = SystemMonospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun dispatchShareIntent(context: android.content.Context, content: String, mimeType: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, content)
        type = "text/plain" // Plain text works universally for code/csv shares
    }
    val shareIntent = Intent.createChooser(sendIntent, "Export Diagnostics")
    context.startActivity(shareIntent)
}
