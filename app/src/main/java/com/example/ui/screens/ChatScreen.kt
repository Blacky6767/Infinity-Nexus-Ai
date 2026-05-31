package com.example.ui.screens

import android.graphics.Bitmap
import android.util.Base64
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.api.AIModel
import com.example.database.ChatMessage
import com.example.ui.MainViewModel
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: MainViewModel) {
    val messages by viewModel.currentMessages.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()
    val userState by viewModel.userState.collectAsState()

    // Mode States
    val compareMode by viewModel.chatCompareMode.collectAsState()
    val consensusMode by viewModel.chatConsensusMode.collectAsState()
    val debateMode by viewModel.chatDebateMode.collectAsState()
    val deepThinkingMode by viewModel.deepThinkingMode.collectAsState()

    var inputPhrase by remember { mutableStateOf("") }
    var showModelMenu by remember { mutableStateOf(false) }
    var mockAttachedFile by remember { mutableStateOf<String?>(null) } // null or file type

    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Auto-scroll on new message entry
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            lazyListState.animateScrollToItem(messages.size - 1)
        }
    }

    val scaffoldBg = Brush.verticalGradient(
        colors = listOf(Color(0xFFF6F8FA), Color(0xFFFFFFFF))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(scaffoldBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Action Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(BorderStroke(1.dp, Color(0xFFE2E8F0)))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Changing AI Model live selector button
                Box {
                    Button(
                        onClick = { showModelMenu = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = selectedModel.iconEmoji + " ", fontSize = 16.sp)
                            Text(
                                text = selectedModel.name,
                                color = Color(0xFF1E293B),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                Icons.Filled.ArrowDropDown,
                                contentDescription = null,
                                tint = Color(0xFF0F172A),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Dropdown for selecting model
                    DropdownMenu(
                        expanded = showModelMenu,
                        onDismissRequest = { showModelMenu = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        AIModel.MODELS.forEach { model ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = model.iconEmoji + " ", fontSize = 18.sp)
                                        Column {
                                            Text(text = model.name, color = Color(0xFF1E293B), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text(text = model.provider, color = Color(0xFF64748B), fontSize = 11.sp)
                                        }
                                    }
                                },
                                onClick = {
                                    viewModel.selectModel(model)
                                    showModelMenu = false
                                }
                            )
                        }
                    }
                }

                // Modes Config Row
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ModeIconButton(
                        icon = Icons.Filled.Compare,
                        active = compareMode,
                        tooltip = "Compare Mode",
                        onClick = { viewModel.chatCompareMode.value = !compareMode }
                    )
                    ModeIconButton(
                        icon = Icons.Filled.JoinInner,
                        active = consensusMode,
                        tooltip = "Consensus Mode",
                        onClick = { viewModel.chatConsensusMode.value = !consensusMode }
                    )
                    ModeIconButton(
                        icon = Icons.Filled.Forum,
                        active = debateMode,
                        tooltip = "Debate Mode",
                        onClick = { viewModel.chatDebateMode.value = !debateMode }
                    )
                    ModeIconButton(
                        icon = Icons.Filled.Psychology,
                        active = deepThinkingMode,
                        tooltip = "Thinking Mode",
                        onClick = { viewModel.deepThinkingMode.value = !deepThinkingMode }
                    )
                }
            }

            // Mode Headers Banner
            AnimatedVisibility(visible = compareMode || consensusMode || debateMode || deepThinkingMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F5F9))
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (compareMode) ModeActiveBadge("Compare")
                    if (consensusMode) ModeActiveBadge("Consensus")
                    if (debateMode) ModeActiveBadge("Debate")
                    if (deepThinkingMode) ModeActiveBadge("Deep Thought")
                }
            }

            // Messages Container List
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
            ) {
                if (messages.isEmpty()) {
                    item {
                        EmptyChatGreeting(selectedModel)
                    }
                } else {
                    items(messages) { message ->
                        ChatMessageItem(message, selectedModel)
                    }
                }

                if (isGenerating) {
                    item {
                        GeneratingLoaderItem(selectedModel)
                    }
                }
            }
        }

        // Floating Prompt Input Tray
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xFFF6F8FA), Color(0xFFF6F8FA))
                    )
                )
                .padding(16.dp)
        ) {
            // Attached mock indicator
            if (mockAttachedFile != null) {
                Row(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE0F2FE))
                        .border(1.dp, Color(0xFF38BDF8), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when(mockAttachedFile) {
                            "PDF" -> Icons.Filled.PictureAsPdf
                            "Image" -> Icons.Filled.Image
                            else -> Icons.Filled.AttachFile
                        },
                        contentDescription = null,
                        tint = Color(0xFF0369A1),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Attached: Mock_Nexus_File.$mockAttachedFile",
                        fontSize = 11.sp,
                        color = Color(0xFF0369A1),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Remove attachment",
                        tint = Color(0xFF0369A1),
                        modifier = Modifier
                            .size(14.dp)
                            .clickable { mockAttachedFile = null }
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFCBD5E1))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Attachment selector Menu Button
                    var showAttachmentMenu by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { showAttachmentMenu = true }) {
                            Icon(Icons.Filled.AddCircle, contentDescription = "Attach file", tint = Color(0xFF64748B))
                        }

                        DropdownMenu(
                            expanded = showAttachmentMenu,
                            onDismissRequest = { showAttachmentMenu = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Upload PDF Document", color = Color(0xFF1E293B), fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Filled.PictureAsPdf, contentDescription = null, tint = Color.Red) },
                                onClick = {
                                    mockAttachedFile = "PDF"
                                    showAttachmentMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Upload Local Photo", color = Color(0xFF1E293B), fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Filled.Image, contentDescription = null, tint = Color(0xFF0288D1)) },
                                onClick = {
                                    mockAttachedFile = "Image"
                                    showAttachmentMenu = false
                                }
                            )
                        }
                    }

                    TextField(
                        value = inputPhrase,
                        onValueChange = { inputPhrase = it },
                        placeholder = { Text("Ask Infinity Nexus...", color = Color(0xFF94A3B8), fontSize = 14.sp) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color(0xFF1E293B),
                            unfocusedTextColor = Color(0xFF1E293B),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = {
                            if (inputPhrase.trim().isNotEmpty()) {
                                viewModel.sendChatMessage(inputPhrase.trim())
                                inputPhrase = ""
                                mockAttachedFile = null
                                keyboardController?.hide()
                            }
                        })
                    )

                    IconButton(
                        onClick = {
                            if (inputPhrase.trim().isNotEmpty()) {
                                viewModel.sendChatMessage(inputPhrase.trim())
                                inputPhrase = ""
                                mockAttachedFile = null
                                keyboardController?.hide()
                            }
                        },
                        enabled = inputPhrase.trim().isNotEmpty()
                    ) {
                        Icon(
                            Icons.Filled.ArrowUpward,
                            contentDescription = "Send",
                            tint = if (inputPhrase.trim().isNotEmpty()) Color(0xFF0288D1) else Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModeIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    active: Boolean,
    tooltip: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(if (active) Color(0xFFE0F2FE) else Color.Transparent)
    ) {
        Icon(icon, contentDescription = tooltip, tint = if (active) Color(0xFF0288D1) else Color(0xFF64748B), modifier = Modifier.size(18.dp))
    }
}

@Composable
fun ModeActiveBadge(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFFE0F2FE))
            .border(1.dp, Color(0xFFBAE6FD), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(text = text.uppercase(), color = Color(0xFF0369A1), fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun EmptyChatGreeting(model: AIModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1F5F9))
                .border(2.dp, Color(0xFFCBD5E1), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = model.iconEmoji, fontSize = 32.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Active Node: ${model.name}",
            color = Color(0xFF1E293B),
            fontWeight = FontWeight.Black,
            fontSize = 18.sp
        )

        Text(
            text = model.description,
            color = Color(0xFF64748B),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(start = 32.dp, end = 32.dp, top = 6.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Try suggesting prompts below to start your private sandboxed logical intelligence flow.",
            color = Color(0xFF94A3B8),
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 48.dp)
        )
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage, activeModel: AIModel) {
    val isUser = message.role == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = activeModel.iconEmoji, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
        }

        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            modifier = Modifier.weight(1f, fill = false)
        ) {
            // Model/Role Label
            if (!isUser) {
                Text(
                    text = activeModel.name,
                    color = Color(0xFF0F172A),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            // Message Bubble Card
            Card(
                shape = RoundedCornerShape(
                    topStart = if (isUser) 16.dp else 4.dp,
                    topEnd = if (isUser) 4.dp else 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                ),
                colors = CardDefaults.cardColors(containerColor = if (isUser) Color(0xFFE0F2FE) else Color(0xFFF1F5F9)),
                border = BorderStroke(1.dp, if (isUser) Color(0xFFBAE6FD) else Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Thinking expansion block
                    if (message.thinkingProcess != null) {
                        var expandThinking by remember { mutableStateOf(false) }
                        Column(
                            modifier = Modifier
                                .padding(bottom = 10.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFEF3C7))
                                .padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expandThinking = !expandThinking },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Troubleshoot, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Deep Reasoning Pathway", color = Color(0xFFB45309), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Icon(
                                    imageVector = if (expandThinking) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                    contentDescription = null,
                                    tint = Color(0xFFB45309),
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            if (expandThinking) {
                                Divider(color = Color(0x2BD97706), modifier = Modifier.padding(vertical = 6.dp))
                                Text(
                                    text = message.thinkingProcess,
                                    color = Color(0xFF78350F),
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    Text(
                        text = message.text,
                        color = if (isUser) Color(0xFF0369A1) else Color(0xFF1E293B),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun GeneratingLoaderItem(model: AIModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFFE2E8F0)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = model.iconEmoji, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.width(10.dp))

        Card(
            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1D30))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    color = Color.Cyan,
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Syncing with Overlord node...",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        }
    }
}
