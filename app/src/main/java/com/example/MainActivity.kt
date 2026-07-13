package com.example

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.BackgroundGlows
import com.example.ui.components.DynamicIsland
import com.example.ui.screens.*
import com.example.ui.theme.PhoneDashboardTheme
import com.example.ui.theme.SystemSansSerif
import com.example.ui.theme.NeonBlue
import com.example.ui.viewmodel.DashboardViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current.applicationContext as Application
            val factory = DashboardViewModelFactory(context)
            val dashboardViewModel: DashboardViewModel = viewModel(factory = factory)

            PhoneDashboardTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    // 1. Interactive Ambient Gradient Glows (Bottom Layer)
                    BackgroundGlows(modifier = Modifier.fillMaxSize())

                    // Glass Dimming Vignette filter
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.25f))
                    )

                    // 2. Main Shell Layout with Transparent Scaffold
                    MainShell(viewModel = dashboardViewModel)

                    // 3. Dynamic Island overlay container (Front Layer)
                    val alertVisible by dashboardViewModel.alertVisible.collectAsStateWithLifecycle()
                    val alertMsg by dashboardViewModel.alertMessage.collectAsStateWithLifecycle()
                    val alertSubMsg by dashboardViewModel.alertSubMessage.collectAsStateWithLifecycle()

                    DynamicIsland(
                        visible = alertVisible,
                        message = alertMsg ?: "",
                        subMessage = alertSubMsg,
                        accentColor = NeonBlue,
                        onDismiss = { dashboardViewModel.dismissAlert() }
                    )
                }
            }
        }
    }
}

class DashboardViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun MainShell(viewModel: DashboardViewModel) {
    val navController = rememberNavController()
    var currentRoute by remember { mutableStateOf("dashboard") }

    val destinations = listOf("dashboard", "hardware", "battery", "network", "sensors", "tools", "settings")
    val destinationLabels = listOf("Dash", "HW", "Batt", "Net", "Sens", "Tools", "Set")
    val icons = listOf(
        Icons.Default.Dashboard,
        Icons.Default.DeveloperMode,
        Icons.Default.BatteryChargingFull,
        Icons.Default.Language,
        Icons.Default.CompassCalibration,
        Icons.Default.Build,
        Icons.Default.Settings
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "PHONE DASHBOARD",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = SystemSansSerif,
                    fontSize = 15.sp,
                    letterSpacing = 1.sp
                )
            }
        },
        bottomBar = {
            // Floating Glassmorphic Dock Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White.copy(alpha = 0.06f))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(24.dp))
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    destinations.forEachIndexed { idx, route ->
                        val isSelected = currentRoute == route
                        IconButton(
                            onClick = {
                                if (currentRoute != route) {
                                    currentRoute = route
                                    navController.navigate(route) {
                                        popUpTo("dashboard") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("nav_btn_$route")
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = icons[idx],
                                    contentDescription = destinationLabels[idx],
                                    tint = if (isSelected) NeonBlue else Color.White.copy(alpha = 0.5f),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = destinationLabels[idx],
                                    color = if (isSelected) NeonBlue else Color.White.copy(alpha = 0.5f),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = SystemSansSerif
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("dashboard") {
                HomeDashboardScreen(viewModel = viewModel)
            }
            composable("hardware") {
                CpuGpuRamScreen(viewModel = viewModel)
            }
            composable("battery") {
                BatteryStorageScreen(viewModel = viewModel)
            }
            composable("network") {
                NetworkScreen(viewModel = viewModel)
            }
            composable("sensors") {
                SensorsScreen(viewModel = viewModel)
            }
            composable("tools") {
                ToolsScreen(viewModel = viewModel)
            }
            composable("settings") {
                SettingsScreen(viewModel = viewModel)
            }
        }
    }
}
