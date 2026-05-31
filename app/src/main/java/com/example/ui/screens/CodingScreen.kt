package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import kotlinx.coroutines.delay

data class Collaborator(
    val name: String,
    val email: String,
    val color: Color,
    val status: String,
    val isYou: Boolean = false
)

data class SharedFile(
    val name: String,
    val path: String,
    val initialContent: String
)

data class CommitLog(
    val id: String,
    val version: String,
    val author: String,
    val description: String,
    val timestamp: String,
    val editedContent: String
)

@Composable
fun CodingScreen(viewModel: MainViewModel) {
    val userState by viewModel.userState.collectAsState()

    // Pure black background matching requested theme
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
    ) {
        var activeTab by remember { mutableStateOf("workspace") } // workspace, collaboration, history

        val files = listOf(
            SharedFile("MainActivity.kt", "app/src/main/java/com/example/", "package com.example\n\nimport android.os.Bundle\n\nclass MainActivity : ComponentActivity() {\n    override fun onCreate(savedInstanceState: Bundle?) {\n        super.onCreate(savedInstanceState)\n        println(\"Hello Collaborator!\")\n    }\n}"),
            SharedFile("Entities.kt", "database/", "package com.example.database\n\n@Entity(tableName = \"user_states\")\ndata class UserState(\n    val dailyStreak: Int = 1,\n    val isLoggedIn: Boolean = true\n)"),
            SharedFile("RetrofitClient.kt", "api/", "package com.example.api\n\nimport retrofit2.Retrofit\n\nclass RetrofitClient {\n    val api = Retrofit.Builder().baseUrl(\"https://api.nexus.com\")\n}")
        )

        var selectedFileIndex by remember { mutableStateOf(0) }
        val currentFile = files[selectedFileIndex]

        var editorContent by remember(selectedFileIndex) { mutableStateOf(currentFile.initialContent) }

        // Live cursor simulator or presence indicators
        val collaborators = remember {
            listOf(
                Collaborator("You", userState.googleEmail ?: "fireblacky08@gmail.com", Color.Cyan, "Editing active", isYou = true),
                Collaborator("Alex_Dev", "alex.dev@nexus.io", Color(0xFF00FF88), "Coding...", false),
                Collaborator("Sophia_AI", "sophia.bot@nexus.io", Color(0xFFFFB300), "Reviewing", false),
                Collaborator("Marcus_Node", "marcus.db@nexus.io", Color.White, "Watching", false)
            )
        }

        // Commit and Version History Logs
        var commitHistory by remember {
            mutableStateOf(
                mutableListOf(
                    CommitLog("1", "v1.0.2", "Sophia_AI", "Refactored userState schemas in Entities.kt", "3 mins ago", "package com.example.database\n\n// Added fields"),
                    CommitLog("2", "v1.0.1", "Alex_Dev", "Configured network nodes and routing in MainActivity", "23 mins ago", "package com.example\n\n// Setup main route"),
                    CommitLog("3", "v1.0.0", userState.googleDisplayName ?: "fireblacky08", "Initial repository framework init", "2 hours ago", "package com.example\n\n// Fresh build")
                )
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // Header Console
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, Color(0xFF1A1A1A)))
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF00FF88)))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "NEXUS CO-COMPILER",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "Real-time Multi-agent Coding Workspace",
                        color = Color.Gray,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Screen Navigation tabs
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF121212))
                        .padding(2.dp)
                ) {
                    listOf("workspace", "collaboration", "history").forEach { tab ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (activeTab == tab) Color(0xFF222222) else Color.Transparent)
                                .clickable { activeTab = tab }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = tab.uppercase(),
                                color = if (activeTab == tab) Color.Cyan else Color.Gray,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Tabs implementation
            when (activeTab) {
                "workspace" -> {
                    // Shared Workspace Screen: Code editor, collaborator indicators, revision trigger
                    Row(modifier = Modifier.weight(1f)) {
                        // Left sidebar: Files explorer
                        Column(
                            modifier = Modifier
                                .width(120.dp)
                                .fillMaxHeight()
                                .border(BorderStroke(1.dp, Color(0xFF111111)))
                                .background(Color(0xFF050505))
                        ) {
                            Text(
                                text = " EXPLORER",
                                color = Color.Gray,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(10.dp)
                            )
                            files.forEachIndexed { idx, file ->
                                val isSelected = idx == selectedFileIndex
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(if (isSelected) Color(0xFF151515) else Color.Transparent)
                                        .clickable { selectedFileIndex = idx }
                                        .padding(horizontal = 12.dp, vertical = 10.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.InsertDriveFile,
                                            contentDescription = null,
                                            tint = if (isSelected) Color.Cyan else Color.Gray,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = file.name,
                                            color = if (isSelected) Color.White else Color.Gray,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.weight(1f))
                            
                            // Live peer counters
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF00FF88)))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("4 ONLINE", color = Color.Gray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                            }
                        }

                        // Right section: Editor terminal content
                        Column(modifier = Modifier.weight(1f)) {
                            // File title banner
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF0E0E0E))
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${currentFile.path}${currentFile.name}*",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = Color.Cyan
                                )

                                // Live editors list tags
                                Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
                                    collaborators.forEach { buddy ->
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(buddy.color)
                                                .border(2.dp, Color.Black, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = buddy.name.take(1),
                                                color = Color.Black,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            // Interactive Editor Text field
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .background(Color.Black)
                                    .padding(12.dp)
                            ) {
                                BasicTextField(
                                    value = editorContent,
                                    onValueChange = { editorContent = it },
                                    textStyle = TextStyle(
                                        color = Color(0xFFDCDCDC),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp
                                    ),
                                    modifier = Modifier.fillMaxSize()
                                )

                                // Simulated compiler highlight code suggestions
                                Text(
                                    text = "Ready to Sync & Commit",
                                    color = Color.DarkGray,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp)
                                )
                            }

                            // Control tray: Local Commit of Shared Documents
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(BorderStroke(1.dp, Color(0xFF111111)))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                var commitMessage by remember { mutableStateOf("") }

                                BasicTextField(
                                    value = commitMessage,
                                    onValueChange = { commitMessage = it },
                                    textStyle = TextStyle(color = Color.White, fontSize = 12.sp, fontFamily = FontFamily.Monospace),
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(BorderStroke(1.dp, Color(0xFF222222)), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    decorationBox = { innerTextField ->
                                        if (commitMessage.isEmpty()) {
                                            Text("Commit message...", color = Color.DarkGray, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                                        }
                                        innerTextField()
                                    }
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Button(
                                    onClick = {
                                        if (commitMessage.isNotEmpty()) {
                                            val nextVersion = "v1.0.${commitHistory.size + 1}"
                                            commitHistory.add(
                                                0,
                                                CommitLog(
                                                    id = (commitHistory.size + 1).toString(),
                                                    version = nextVersion,
                                                    author = userState.googleDisplayName ?: "fireblacky08",
                                                    description = commitMessage,
                                                    timestamp = "Just now",
                                                    editedContent = editorContent
                                                )
                                            )
                                            commitMessage = ""
                                            viewModel.dismissNotification()
                                        }
                                    },
                                    enabled = commitMessage.isNotEmpty(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF88), disabledContainerColor = Color(0xFF121212)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("COMMIT", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }
                    }
                }

                "collaboration" -> {
                    // Shared Session User list, status presence indicators
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                "ACTIVE CELL COLLABORATORS",
                                color = Color.Cyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 2.sp
                            )
                            Text(
                                "Users currently connected to this workspace document model session node.",
                                color = Color.Gray,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                            )
                        }

                        items(collaborators) { buddy ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF050505)),
                                border = BorderStroke(1.dp, Color(0xFF151515)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Avatar
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(buddy.color.copy(alpha = 0.15f))
                                            .border(1.dp, buddy.color, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = buddy.name.take(1),
                                            color = buddy.color,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = buddy.name,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                            if (buddy.isYou) {
                                                Box(
                                                    modifier = Modifier
                                                        .padding(start = 8.dp)
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(Color.Cyan.copy(alpha = 0.15f))
                                                        .border(1.dp, Color.Cyan, RoundedCornerShape(4.dp))
                                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                                ) {
                                                    Text("YOU", color = Color.Cyan, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                        Text(
                                            text = buddy.email,
                                            color = Color.Gray,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(if (buddy.status.contains("active")) Color(0xFF00FF88) else Color(0xFFFFB300))
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = buddy.status,
                                            color = if (buddy.status.contains("active")) Color(0xFF00FF88) else Color.LightGray,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                "history" -> {
                    // Version control / activity log logs. User can rollback compiler logic!
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                "VERSION REVISION CONTROL",
                                color = Color.Cyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 2.sp
                            )
                            Text(
                                "Activity logs detailing committing histories. Revert workspace codes to early nodes instantly.",
                                color = Color.Gray,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                            )
                        }

                        items(commitHistory) { log ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF050505)),
                                border = BorderStroke(1.dp, Color(0xFF151515)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(Color(0xFF222222))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = log.version,
                                                    color = Color.Cyan,
                                                    fontSize = 11.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = "by ${log.author}",
                                                color = Color.LightGray,
                                                fontSize = 12.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }

                                        Text(
                                            text = log.timestamp,
                                            color = Color.DarkGray,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = log.description,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp,
                                        fontFamily = FontFamily.Monospace
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Button(
                                            onClick = {
                                                editorContent = log.editedContent
                                                activeTab = "workspace"
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222222)),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                "ROLLBACK CODE & SYNC",
                                                color = Color.Cyan,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
