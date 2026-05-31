package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel

@Composable
fun ProfileScreen(viewModel: MainViewModel) {
    val userState by viewModel.userState.collectAsState()
    val scrollState = rememberScrollState()
    var selectedTier by remember { mutableStateOf("Ultra") } // Free, Pro, Ultra, Enterprise

    val bgGradient = Brush.verticalGradient(
        colors = listOf(Color.Black, Color(0xFF0F0F12))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Back button to return to dashboard
        IconButton(
            onClick = { viewModel.navigateTo("dashboard") },
            modifier = Modifier
                .clip(CircleShape)
                .background(Color(0xFF222222))
        ) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Avatar header block
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(if (userState.isLoggedIn) Color(0xFF0F2D37) else Color(0xFF222222))
                    .border(2.dp, if (userState.isLoggedIn) Color.Cyan else Color.LightGray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (userState.isLoggedIn) {
                    Icon(Icons.Filled.Verified, contentDescription = null, tint = Color.Cyan, modifier = Modifier.size(48.dp))
                } else {
                    Icon(Icons.Filled.AccountCircle, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(56.dp))
                }
            }

            Text(
                text = if (userState.isLoggedIn) (userState.googleDisplayName ?: "Nexus Connected") else "Guest Pioneer",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp)
            )

            Text(
                text = if (userState.isLoggedIn) (userState.googleEmail ?: "") else "Offline Sandboxed Session",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "PLATFORM CAPABILITY LEVELS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Cyan,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Subscriptions selection grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Free", "Pro", "Ultra", "Enterprise").forEach { tier ->
                val isSelected = selectedTier == tier
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTier = tier },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFF1E2E4E) else Color(0xFF121424)
                    ),
                    border = BorderStroke(1.dp, if (isSelected) Color.Cyan else Color(0x19FFFFFF))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = tier,
                            color = if (isSelected) Color.Cyan else Color.LightGray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Subscription Stats Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF16182B)),
            border = BorderStroke(1.dp, Color(0x1AFFFFFF))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Subscription Benefits: $selectedTier",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                val benefits = when (selectedTier) {
                    "Free" -> listOf("Basic chat speeds", "Standard 3.5 AI Core", "Offline game modes enabled")
                    "Pro" -> listOf("Priority model connections", "Uncapped API processing", "All offline reward multipliers active")
                    "Ultra" -> listOf("Advanced 3.1 Pro models enabled during deep searches", "Bonus time on everyday streaks", "Image & Video Studios fully unlocked")
                    else -> listOf("Dedicated enterprise workspace node access", "Highest priority cluster routing", "Lifetime budget rollover unlocked")
                }

                benefits.forEach { b ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = null, tint = Color.Green, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = b, color = Color.LightGray, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "ARCADE PERFORMANCE STATISTICS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Cyan,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Statistics list
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0D11)),
            border = BorderStroke(1.dp, Color(0xFF222222))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                StatRow(label = "Daily Check-in Streak", value = "${userState.dailyStreak} Days")
                Divider(color = Color(0x19FFFFFF), modifier = Modifier.padding(vertical = 10.dp))
                
                val minutesTotal = userState.totalEarnedSeconds / 60
                val secondsTotal = userState.totalEarnedSeconds % 60
                StatRow(label = "Total Earned AI Time", value = "${minutesTotal}m ${secondsTotal}s")
                Divider(color = Color(0x19FFFFFF), modifier = Modifier.padding(vertical = 10.dp))
                
                StatRow(label = "Platform Node Connection", value = "Online Secures")
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "GOOGLE CLOUD SYNCHRONIZATION",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Cyan,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Interactive authenticating cards
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0D11)),
            border = BorderStroke(1.dp, if (userState.isLoggedIn) Color.Cyan else Color(0xFF222222))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (userState.isLoggedIn) "Verified Google Cloud Account" else "Offline Local mode (Sync disabled)",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (userState.isLoggedIn) "All your chat histories, daily streak multipliers, and accumulated runtime and rollover variables are pinned securely to your email." else "Sign-in to link your offline rewards, streaking variables, and sandbox sessions permanently.",
                    color = Color.Gray,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                if (userState.isLoggedIn) {
                    Button(
                        onClick = { viewModel.simulateGoogleLogout() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Logout, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("DISCONNECT GOOGLE ACCOUNT", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                } else {
                    var emailInput by remember { mutableStateOf("fireblacky08@gmail.com") }
                    var nameInput by remember { mutableStateOf("fireblacky08") }

                    Column {
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            label = { Text("Google Account Email", color = Color.Gray, fontSize = 11.sp) },
                            textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Cyan,
                                unfocusedBorderColor = Color(0xFF333333)
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Display Name", color = Color.Gray, fontSize = 11.sp) },
                            textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Cyan,
                                unfocusedBorderColor = Color(0xFF333333)
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.simulateGoogleLogin(emailInput, nameInput, null) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Login, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("SIGN-IN WITH GOOGLE ACCOUNT", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "SYSTEM GUIDE INDUCTIONS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Cyan,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Button(
            onClick = { viewModel.resetOnboarding() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222222)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, Color(0xFF444444))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.RestartAlt, contentDescription = null, tint = Color.Cyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("REPLAY PLATFORM ONBOARDING FLOW", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.Gray, fontSize = 13.sp)
        Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}
