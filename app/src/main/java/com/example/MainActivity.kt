package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val viewModel: MainViewModel by viewModels()
                val currentScreen by viewModel.currentScreen.collectAsState()
                val uiNotification by viewModel.uiNotification.collectAsState()

                // Display active updates as secure local toasts so as not to clutter the screen
                LaunchedEffect(uiNotification) {
                    uiNotification?.let { msg ->
                        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()
                        viewModel.dismissNotification()
                    }
                }

                val userState by viewModel.userState.collectAsState()

                if (!userState.isOnboardingCompleted) {
                    OnboardingScreen(viewModel)
                } else {
                    // Adapt UI colors dynamically based on the current page's aesthetic theme
                    val navBgColor = when (currentScreen) {
                        "chat" -> Color(0xFFF1F5F9)
                        "dashboard" -> Color(0xFF0A0A0A)
                        "research" -> Color(0xFF070914)
                        "image" -> Color(0xFF070914)
                        "games" -> Color(0xFF0D0E15)
                        "profile" -> Color(0xFF0F0F12)
                        "coding" -> Color.Black
                        else -> Color(0xFF131525)
                    }

                    val isLightBar = currentScreen == "chat"
                    val activeIndicatorColor = if (isLightBar) Color(0xFFE2E8F0) else Color(0xFF1E2F4E)
                    val tintSelected = if (isLightBar) Color(0xFF0288D1) else Color.Cyan
                    val tintUnselected = if (isLightBar) Color(0xFF64748B) else Color.Gray

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            Column {
                                NavigationBar(
                                    containerColor = navBgColor,
                                    contentColor = if (isLightBar) Color(0xFF0F172A) else Color.White,
                                    modifier = Modifier.height(72.dp)
                                ) {
                                    val isDashboardFamily = currentScreen == "dashboard" || currentScreen == "profile" || currentScreen == "coding"
                                    NavigationBarItem(
                                        selected = isDashboardFamily,
                                        onClick = { viewModel.navigateTo("dashboard") },
                                        icon = { 
                                            Icon(
                                                Icons.Filled.Dashboard, 
                                                contentDescription = "Dashboard", 
                                                tint = if (isDashboardFamily) tintSelected else tintUnselected
                                            ) 
                                        },
                                        label = { 
                                            Text(
                                                "Dashboard", 
                                                fontSize = 11.sp, 
                                                color = if (isDashboardFamily) tintSelected else tintUnselected,
                                                fontWeight = if (isDashboardFamily) FontWeight.Bold else FontWeight.Normal
                                            ) 
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            indicatorColor = activeIndicatorColor
                                        )
                                    )

                                    val isChatActive = currentScreen == "chat"
                                    NavigationBarItem(
                                        selected = isChatActive,
                                        onClick = { viewModel.navigateTo("chat") },
                                        icon = { 
                                            Icon(
                                                Icons.Filled.Forum, 
                                                contentDescription = "AI Chat", 
                                                tint = if (isChatActive) tintSelected else tintUnselected
                                            ) 
                                        },
                                        label = { 
                                            Text(
                                                "AI Chat", 
                                                fontSize = 11.sp, 
                                                color = if (isChatActive) tintSelected else tintUnselected,
                                                fontWeight = if (isChatActive) FontWeight.Bold else FontWeight.Normal
                                            ) 
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            indicatorColor = activeIndicatorColor
                                        )
                                    )

                                    val isResearchActive = currentScreen == "research"
                                    NavigationBarItem(
                                        selected = isResearchActive,
                                        onClick = { viewModel.navigateTo("research") },
                                        icon = { 
                                            Icon(
                                                Icons.Filled.Language, 
                                                contentDescription = "Research", 
                                                tint = if (isResearchActive) tintSelected else tintUnselected
                                            ) 
                                        },
                                        label = { 
                                            Text(
                                                "Research", 
                                                fontSize = 11.sp, 
                                                color = if (isResearchActive) tintSelected else tintUnselected,
                                                fontWeight = if (isResearchActive) FontWeight.Bold else FontWeight.Normal
                                            ) 
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            indicatorColor = activeIndicatorColor
                                        )
                                    )

                                    val isImageActive = currentScreen == "image"
                                    NavigationBarItem(
                                        selected = isImageActive,
                                        onClick = { viewModel.navigateTo("image") },
                                        icon = { 
                                            Icon(
                                                Icons.Filled.Palette, 
                                                contentDescription = "Studios", 
                                                tint = if (isImageActive) tintSelected else tintUnselected
                                            ) 
                                        },
                                        label = { 
                                            Text(
                                                "Studios", 
                                                fontSize = 11.sp, 
                                                color = if (isImageActive) tintSelected else tintUnselected,
                                                fontWeight = if (isImageActive) FontWeight.Bold else FontWeight.Normal
                                            ) 
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            indicatorColor = activeIndicatorColor
                                        )
                                    )

                                    val isGamesActive = currentScreen == "games"
                                    NavigationBarItem(
                                        selected = isGamesActive,
                                        onClick = { viewModel.navigateTo("games") },
                                        icon = { 
                                            Icon(
                                                Icons.Filled.SportsEsports, 
                                                contentDescription = "Games", 
                                                tint = if (isGamesActive) tintSelected else tintUnselected
                                            ) 
                                        },
                                        label = { 
                                            Text(
                                                "Arcade", 
                                                fontSize = 11.sp, 
                                                color = if (isGamesActive) tintSelected else tintUnselected,
                                                fontWeight = if (isGamesActive) FontWeight.Bold else FontWeight.Normal
                                            ) 
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            indicatorColor = activeIndicatorColor
                                        )
                                    )
                                }
                                // Respect bottom navigation safety gestures on edge-to-edge screens with dynamic palette matching
                                Spacer(
                                    modifier = Modifier
                                        .background(navBgColor)
                                        .windowInsetsBottomHeight(WindowInsets.navigationBars)
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = innerPadding.calculateBottomPadding())
                        ) {
                            when (currentScreen) {
                                "dashboard" -> DashboardScreen(viewModel)
                                "chat" -> ChatScreen(viewModel)
                                "research" -> ResearchScreen(viewModel)
                                "image" -> StudioScreen(viewModel)
                                "games" -> GamesScreen(viewModel)
                                "profile" -> ProfileScreen(viewModel)
                                "coding" -> CodingScreen(viewModel)
                                else -> DashboardScreen(viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
