package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import coil.compose.AsyncImage
import java.net.URLEncoder
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

@Composable
fun StudioScreen(viewModel: MainViewModel) {
    var selectedTab by remember { mutableStateOf("Image") } // Image, Video, Voice
    val bgGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF04060E), Color(0xFF0C0E1E), Color(0xFF070914))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(16.dp))

            // Tab Selector Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF16182B))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Image", "Video", "Voice").forEach { tab ->
                    val isActive = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isActive) Color(0x3300FFFF) else Color.Transparent)
                            .clickable { selectedTab = tab }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$tab Studio",
                            color = if (isActive) Color.Cyan else Color.LightGray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Tab Body Displays
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (selectedTab) {
                    "Image" -> ImageStudioTab()
                    "Video" -> VideoStudioTab()
                    "Voice" -> VoiceStudioTab()
                }
            }
        }
    }
}

// ---------------------------------------------
// TAB 1: IMAGE STUDIO
// ---------------------------------------------
@Composable
fun ImageStudioTab() {
    var prompt by remember { mutableStateOf("") }
    var selectedStyle by remember { mutableStateOf("Cyberpunk Neon") }
    var compiling by remember { mutableStateOf(false) }
    var renderSeed by remember { mutableStateOf(1) }
    var showOutputArt by remember { mutableStateOf(false) }
    var generatedImageUrl by remember { mutableStateOf("") }

    val coroutine = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF16182B)),
            border = BorderStroke(1.dp, Color(0x1AFFFFFF))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Creative Art Prompt", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    placeholder = { Text("E.g., Quantum cathedral of glowing cosmic string particles...", color = Color.Gray, fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF0F111E),
                        unfocusedContainerColor = Color(0xFF0F111E),
                        focusedIndicatorColor = Color.Cyan
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Render Format / Style", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Cyberpunk Neon", "Futuristic Glass", "Unreal 3D").forEach { style ->
                        val isSel = selectedStyle == style
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) Color(0xFF1E2E4E) else Color(0xFF0F111E))
                                .clickable { selectedStyle = style }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = style, color = if (isSel) Color.Cyan else Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (prompt.isNotEmpty()) {
                            compiling = true
                            showOutputArt = false
                            coroutine.launch {
                                delay(2000) // simulation duration of neural pass
                                val seed = (1..999999).random()
                                renderSeed = seed
                                val encodedPrompt = URLEncoder.encode("$prompt, in $selectedStyle Art Style, highly detailed, masterpieces, 8k, Unreal Engine 5 render", "UTF-8")
                                generatedImageUrl = "https://image.pollinations.ai/p/$encodedPrompt?width=1024&height=1024&seed=$seed&nologo=true"
                                compiling = false
                                showOutputArt = true
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan),
                    enabled = !compiling && prompt.isNotEmpty()
                ) {
                    if (compiling) {
                        CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Quantum Synthesizing...", color = Color.Black, fontWeight = FontWeight.Bold)
                    } else {
                        Text(text = "Generate Masterpiece", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // High Quality AI Generated Masterpiece Output Card using Coil AsyncImage!
        if (showOutputArt) {
            Text(
                text = "GENERATED MASTERPIECE",
                color = Color.Yellow,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .padding(vertical = 12.dp)
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF07080F)),
                border = BorderStroke(2.dp, Color.Cyan)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    var imageLoading by remember { mutableStateOf(true) }

                    AsyncImage(
                        model = generatedImageUrl,
                        contentDescription = "AI Generated Art Piece",
                        modifier = Modifier.fillMaxSize(),
                        onLoading = { imageLoading = true },
                        onSuccess = { imageLoading = false },
                        onError = { imageLoading = false }
                    )

                    if (imageLoading) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            CircularProgressIndicator(color = Color.Cyan, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Constructing neural weights...",
                                color = Color.Cyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Loading diffusion parameters in sandbox",
                                color = Color.Gray,
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(48.dp))
    }
}

// ---------------------------------------------
// TAB 2: VIDEO STUDIO
// ---------------------------------------------
@Composable
fun VideoStudioTab() {
    var description by remember { mutableStateOf("") }
    var processing by remember { mutableStateOf(false) }
    var progressVal by remember { mutableStateOf(0f) }
    var showDone by remember { mutableStateOf(false) }

    val coroutine = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF16182B))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "AI Video Generation Prompt", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Describe sequence movement (e.g., Starship taking off)...", color = Color.Gray, fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF0F111E),
                        unfocusedContainerColor = Color(0xFF0F111E)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (description.isNotEmpty()) {
                            processing = true
                            progressVal = 0f
                            showDone = false
                            coroutine.launch {
                                repeat(100) {
                                    delay(40)
                                    progressVal += 0.01f
                                }
                                processing = false
                                showDone = true
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan),
                    enabled = !processing && description.isNotEmpty()
                ) {
                    Text(text = "Initialize Veo Video Render", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (processing) {
            Text(text = "Synthesizing Motion Nodes: ${(progressVal * 100).toInt()}%", color = Color.Cyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progressVal },
                color = Color.Cyan,
                trackColor = Color.DarkGray,
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape)
            )
        }

        if (showDone) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .border(1.dp, Color.Green, RoundedCornerShape(12.dp))
                    .background(Color(0xFF0C1D14))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.VideoFile, contentDescription = null, tint = Color.Green, modifier = Modifier.size(48.dp))
                    Text(text = "QUANTUM VIDEO RENDERED", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(top = 8.dp))
                    Text(text = "Completed successfully. 4K frame array initialized.", color = Color.LightGray, fontSize = 12.sp, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

// ---------------------------------------------
// TAB 3: VOICE STUDIO
// ---------------------------------------------
@Composable
fun VoiceStudioTab() {
    var isLiveActive by remember { mutableStateOf(false) }
    var soundWavePhase by remember { mutableStateOf(0f) }
    var vocalTranslationText by remember { mutableStateOf("Ready to receive voice command. Press mic.") }

    var animIndex by remember { mutableStateOf(0) }
    val coroutine = rememberCoroutineScope()

    LaunchedEffect(isLiveActive) {
        soundWavePhase = 0f
        while (isLiveActive) {
            delay(50)
            soundWavePhase += 0.3f
            animIndex++
            if (animIndex % 20 == 0) {
                vocalTranslationText = listOf(
                    "Computing contextual alignment...",
                    "Searching workspace repositories...",
                    "Overlord synchronizer sequence checked.",
                    "Active secure stream established."
                ).random()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "REAL-TIME VOICE SYNC",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp
        )
        Text(
            text = "Initiate interactive double-path conversation streams.",
            color = Color.Gray,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
        )

        // Waveform Oscillations box
        Box(
            modifier = Modifier
                .width(280.dp)
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, if (isLiveActive) Color.Cyan else Color.DarkGray, RoundedCornerShape(16.dp))
                .background(Color(0xFF090A10))
        ) {
            if (isLiveActive) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val pathWidthFactor = 5f
                    
                    val points = mutableListOf<Offset>()
                    for (x in 0 until w.toInt() step 5) {
                        val factor = x.toFloat() / w
                        val waveAmp = h * 0.25f * sin(factor * 12f + soundWavePhase) * (1f - factor) * factor * 4f
                        points.add(Offset(x.toFloat(), h / 2f + waveAmp))
                    }
                    
                    for (i in 0 until points.size - 1) {
                        drawLine(
                            color = Color.Cyan,
                            start = points[i],
                            end = points[i+1],
                            strokeWidth = 3f
                        )
                    }
                }
            } else {
                Text(
                    text = "MIC CLOSED",
                    color = Color.DarkGray,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Vocal Translation display
        Text(
            text = vocalTranslationText,
            color = if (isLiveActive) Color.Cyan else Color.Gray,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 40.dp)
        )

        // Pulsing Microphone Trigger Button
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(if (isLiveActive) Color.Red else Color.Cyan)
                .clickable {
                    isLiveActive = !isLiveActive
                    if (!isLiveActive) {
                        vocalTranslationText = "Voice sync channel terminated."
                    } else {
                        vocalTranslationText = "Listening..."
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isLiveActive) Icons.Filled.MicOff else Icons.Filled.Mic,
                contentDescription = "Trigger voice sync",
                tint = Color.Black,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
