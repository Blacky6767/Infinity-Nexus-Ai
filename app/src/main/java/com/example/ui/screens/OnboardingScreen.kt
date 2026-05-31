package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel

@Composable
fun OnboardingScreen(viewModel: MainViewModel) {
    var step by remember { mutableStateOf(0) }
    val totalSteps = 4

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF04060C), Color(0xFF000000))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(24.dp)
    ) {
        // Skip Button Top Right
        Text(
            text = "Skip Intro",
            color = Color.Cyan,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .clickable { viewModel.completeOnboarding() }
                .border(BorderStroke(1.dp, Color.Cyan), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Crossfade(targetState = step, label = "step_crossfade") { currentStep ->
                when (currentStep) {
                    0 -> OnboardingStepView(
                        title = "Infinity Nexus-AI Brain",
                        description = "Harness the power of multi-model Omni Processing. Simultaneously query Gemini, GPT, Claude, DeepSeek and Grok connected live directly to raw SQLite data flows.",
                        icon = Icons.Filled.Psychology,
                        accentColor = Color.Cyan,
                        badge = "MULTI-AI NODE"
                    )
                    1 -> OnboardingStepView(
                        title = "Collaborative Coding Engine",
                        description = "Step into a synchronized, high-contrast pure black workspace terminal. Live cursor indicators and presence logs let team cells compile and commit code collaboratively with full version control histories.",
                        icon = Icons.Filled.Code,
                        accentColor = Color(0xFF00FF88), // Neon green
                        badge = "SYNCHRONIZED TERMINAL"
                    )
                    2 -> OnboardingStepView(
                        title = "Intelligent Deep Research",
                        description = "Compile extensive analytical dossiers using parallel internet crawlers. Build detailed citations, model comparisons, consensus boards, and cognitive debates with custom search parameters.",
                        icon = Icons.Filled.Language,
                        accentColor = Color(0xFFFFB300), // Amber
                        badge = "SEARCH DOSSIERS"
                    )
                    3 -> OnboardingStepView(
                        title = "Quantum Game Rollovers",
                        description = "Rest and regain active parameters in our neon Arcade. Play addictive local games for at least 60s to unlock 5-10 minutes of bonus AI chat time. Any unused time carries over to the next day automatically!",
                        icon = Icons.Filled.SportsEsports,
                        accentColor = Color(0xFFFF5252), // Retro orange-red
                        badge = "ARCADE TIME TOKENS"
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Step Progress Dot Indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                for (i in 0 until totalSteps) {
                    Box(
                        modifier = Modifier
                            .size(if (step == i) 24.dp else 8.dp, 8.dp)
                            .clip(CircleShape)
                            .background(if (step == i) Color.Cyan else Color.DarkGray)
                            .animateContentSize()
                    )
                }
            }

            // Progression buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Prev button
                TextButton(
                    onClick = { if (step > 0) step-- },
                    enabled = step > 0
                ) {
                    Text(
                        text = "BACK",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (step > 0) Color.LightGray else Color.DarkGray
                    )
                }

                // Next / Finish Button
                Button(
                    onClick = {
                        if (step < totalSteps - 1) {
                            step++
                        } else {
                            viewModel.completeOnboarding()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = if (step < totalSteps - 1) "CONTINUE" else "ACTIVATE NEXUS",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingStepView(
    title: String,
    description: String,
    icon: ImageVector,
    accentColor: Color,
    badge: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Graphic Box Frame
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0xFF0F111E))
                .border(2.dp, accentColor, RoundedCornerShape(32.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Pulsing Background Glow effect
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val pulseScale by infiniteTransition.animateFloat(
                initialValue = 0.85f,
                targetValue = 1.15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = EaseInOutSine),
                    repeatMode = RepeatMode.Reverse
                ), label = "pulse_scale"
            )

            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.1f * pulseScale))
                    .border(1.dp, accentColor.copy(alpha = 0.25f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Feature category tag
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(accentColor.copy(alpha = 0.15f))
                .border(1.dp, accentColor, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = badge,
                color = accentColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Large title text
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Context description body
        Text(
            text = description,
            fontSize = 14.sp,
            color = Color.LightGray,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}
