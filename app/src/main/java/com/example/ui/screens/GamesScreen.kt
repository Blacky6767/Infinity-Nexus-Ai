package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import kotlinx.coroutines.delay
import java.util.UUID

@Composable
fun GamesScreen(viewModel: MainViewModel) {
    val userState by viewModel.userState.collectAsState()
    var activeGame by remember { mutableStateOf<String?>(null) } // null, "memory", "tictactoe", "starcatcher"

    val darkGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF000000), Color(0xFF0D0E15))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkGradient)
    ) {
        if (activeGame == null) {
            // ARCADE DASHBOARD
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Title
                Text(
                    text = "INFINITE ARCADE",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Cyan,
                    letterSpacing = 4.sp,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                Text(
                    text = "Play games to earn free AI Chat Time. Addictive gameplay with daily reward streaks!",
                    fontSize = 14.sp,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Time Remaining Status Glass Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x15FFFFFF)),
                    border = BorderStroke(1.dp, Color(0x3000FFFF))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Your Chat Budget",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            val totalSecs = userState.chatTimeRemainingSeconds
                            val hours = totalSecs / 3600
                            val minutes = (totalSecs % 3600) / 60
                            val seconds = totalSecs % 60
                            Text(
                                text = String.format("%02d:%02d:%02d", hours, minutes, seconds),
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF00E676))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Bolt, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "Streak: ${userState.dailyStreak}d",
                                    color = Color.Black,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "Choose a Game to Play",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 16.dp)
                )

                // Games List (Scrollable column)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    GameSelectionCard(
                        title = "Memory Matrix",
                        subtitle = "Train your focus by matching digital quantum symbol clusters. Addicting brain exercise.",
                        difficulty = "Medium",
                        timeTag = "+5-15 mins Chat Time",
                        icon = Icons.Filled.Memory,
                        color = Color(0xFF81D4FA),
                        onClick = { activeGame = "memory" }
                    )

                    GameSelectionCard(
                        title = "Tic-Tac-Toe Overlord",
                        subtitle = "Compete against an active, adaptive local AI. Match your tactical capability.",
                        difficulty = "Variable",
                        timeTag = "+5-15 mins Chat Time",
                        icon = Icons.Filled.SmartButton,
                        color = Color(0xFFA5D6A7),
                        onClick = { activeGame = "tictactoe" }
                    )

                    GameSelectionCard(
                        title = "Cosmic Star Catcher",
                        subtitle = "Retro arcade action. Shift left or right, capture cosmic particles, avoid meteors!",
                        difficulty = "Hard / Fast",
                        timeTag = "+5-15 mins Chat Time",
                        icon = Icons.Filled.SportsEsports,
                        color = Color(0xFFFFCC80),
                        onClick = { activeGame = "starcatcher" }
                    )

                    GameSelectionCard(
                        title = "Neon Speed Clicker",
                        subtitle = "Reflex core speed trainer. How fast can you tap the fluctuating neon nexus inside 15 seconds?",
                        difficulty = "Easy / Fast",
                        timeTag = "+5-12 mins Chat Time",
                        icon = Icons.Filled.TouchApp,
                        color = Color(0xFFE040FB),
                        onClick = { activeGame = "clicker" }
                    )

                    GameSelectionCard(
                        title = "Reflex Matrix Trigger",
                        subtitle = "Neural reaction speed test. Tap target matrix cells as they strobe active. Don't slip!",
                        difficulty = "Extreme",
                        timeTag = "+5-15 mins Chat Time",
                        icon = Icons.Filled.Grid3x3,
                        color = Color(0xFF00E676),
                        onClick = { activeGame = "reflex" }
                    )

                    GameSelectionCard(
                        title = "Lucky Dice Journey",
                        subtitle = "Roll dual custom quantum core cubes. Match lucky runs and secure point multiplier streaks.",
                        difficulty = "Easy / Luck",
                        timeTag = "+5-10 mins Chat Time",
                        icon = Icons.Filled.Casino,
                        color = Color(0xFFFF5252),
                        onClick = { activeGame = "diceroll" }
                    )

                    GameSelectionCard(
                        title = "Hidden Code Cipher",
                        subtitle = "Analytical connect puzzle. Crack the random 3-digit connection sequence in 6 tries.",
                        difficulty = "Hard",
                        timeTag = "+5-15 mins Chat Time",
                        icon = Icons.Filled.Lock,
                        color = Color(0xFFFFD700),
                        onClick = { activeGame = "cipher" }
                    )

                    GameSelectionCard(
                        title = "Chromatic Speed Match",
                        subtitle = "Cognitive conflict training. Confirm if the literal color string matches actual font rendering.",
                        difficulty = "Hard / Fast",
                        timeTag = "+5-12 mins Chat Time",
                        icon = Icons.Filled.Palette,
                        color = Color(0xFF1DE9B6),
                        onClick = { activeGame = "colormatch" }
                    )

                    GameSelectionCard(
                        title = "Quantum 2048 Sandbox",
                        subtitle = "Futuristic 3x3 slide-and-merge playground. Combine identical energy nodes to form maximum counts.",
                        difficulty = "Medium",
                        timeTag = "+5-15 mins Chat Time",
                        icon = Icons.Filled.ViewQuilt,
                        color = Color(0xFF2979FF),
                        onClick = { activeGame = "twentyfortyeight" }
                    )

                    GameSelectionCard(
                        title = "Code Typing Champion",
                        subtitle = "Speed typing benchmark. Type the neon syntactical lines before the clock breaches zero.",
                        difficulty = "Hard",
                        timeTag = "+5-15 mins Chat Time",
                        icon = Icons.Filled.Keyboard,
                        color = Color(0xFFFF9100),
                        onClick = { activeGame = "typing" }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "*Notice: Play must last at least 60 seconds to earn chat benefits. Play more than 1 min to claim reward.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        } else {
            // ACTIVE GAME CONTAINER WITH BACK BUTTON & TIMER TRACKING
            var gameTimerSeconds by remember { mutableStateOf(0L) }
            
            // Increment gameplay duration active state
            LaunchedEffect(activeGame) {
                gameTimerSeconds = 0
                while (activeGame != null) {
                    delay(1000)
                    gameTimerSeconds++
                }
            }

            Column(modifier = Modifier.fillMaxSize()) {
                // Top Play Status Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1F2235))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        // Notify VM of finished game session play time prior to exiting!
                        if (activeGame != null) {
                            val name = when (activeGame) {
                                "memory" -> "Memory Matrix"
                                "tictactoe" -> "Tic-Tac-Toe Overlord"
                                "starcatcher" -> "Cosmic Star Catcher"
                                "clicker" -> "Neon Speed Clicker"
                                "reflex" -> "Reflex Matrix Trigger"
                                "diceroll" -> "Lucky Dice Quest"
                                "cipher" -> "Hidden Connection Cipher"
                                "colormatch" -> "Chromatic Speed Match"
                                "twentyfortyeight" -> "Quantum 2048 Sandbox"
                                "typing" -> "Type Champion Sprint"
                                else -> "Arcade Game"
                            }
                            viewModel.recordGameFinished(name, gameTimerSeconds)
                        }
                        activeGame = null
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Exit Game", tint = Color.White)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = when (activeGame) {
                                "memory" -> "MEMORY MATRIX"
                                "tictactoe" -> "TIC-TAC-TOE AI"
                                "starcatcher" -> "COSMIC SPACE"
                                "clicker" -> "SPEED CLICKER"
                                "reflex" -> "REFLEX MATRIX"
                                "diceroll" -> "DICE ROLLER QUEST"
                                "cipher" -> "CODE CIPHER"
                                "colormatch" -> "SPEED COLOR MATCH"
                                "twentyfortyeight" -> "QUANTUM 2048 MINI"
                                "typing" -> "TYPING CHAMPION"
                                else -> "OFFLINE ARCADE"
                            },
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Playtimer: ${gameTimerSeconds / 60}m ${gameTimerSeconds % 60}s",
                            color = if (gameTimerSeconds >= 60) Color(0xFF00FF88) else Color(0xFFFF5252),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            val name = when (activeGame) {
                                "memory" -> "Memory Matrix"
                                "tictactoe" -> "Tic-Tac-Toe Overlord"
                                "starcatcher" -> "Cosmic Star Catcher"
                                "clicker" -> "Neon Speed Clicker"
                                "reflex" -> "Reflex Matrix Trigger"
                                "diceroll" -> "Lucky Dice Quest"
                                "cipher" -> "Hidden Connection Cipher"
                                "colormatch" -> "Chromatic Speed Match"
                                "twentyfortyeight" -> "Quantum 2048 Sandbox"
                                "typing" -> "Type Champion Sprint"
                                else -> "Arcade Game"
                            }
                            viewModel.recordGameFinished(name, gameTimerSeconds)
                            activeGame = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (gameTimerSeconds >= 60) Color(0xFF00FF88) else Color(0xFF424242)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (gameTimerSeconds >= 60) "Claim Time" else "No Reward Yet",
                            fontSize = 11.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Render active game sub-compositions
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    when (activeGame) {
                        "memory" -> MemoryMatrixGame()
                        "tictactoe" -> TicTacToeOverlordGame()
                        "starcatcher" -> CosmicStarCatcherGame()
                        "clicker" -> CosmicClickerGame()
                        "reflex" -> ReflexMatrixGame()
                        "diceroll" -> DiceQuestGame()
                        "cipher" -> CodeCipherGame()
                        "colormatch" -> ColorMatchGame()
                        "twentyfortyeight" -> Quantum2048Game()
                        "typing" -> TypeChampionGame()
                    }
                }
            }
        }
    }
}

@Composable
fun GameSelectionCard(
    title: String,
    subtitle: String,
    difficulty: String,
    timeTag: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x1F223500 + 0x10FFFFFF)),
        border = BorderStroke(1.dp, Color(0x1FFFFFFF))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.2f))
                    .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0x30FFFFFF))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = difficulty, color = Color.LightGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                Text(text = subtitle, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Timelapse, contentDescription = null, tint = Color.Cyan, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = timeTag, color = Color.Cyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ============================================
// GAME 1: MEMORY MATRIX (4x4 BRAIN PAIRS MATCH)
// ============================================
@Composable
fun MemoryMatrixGame() {
    val iconsList = listOf(
        Icons.Filled.Favorite, Icons.Filled.Favorite,
        Icons.Filled.Star, Icons.Filled.Star,
        Icons.Filled.ThumbUp, Icons.Filled.ThumbUp,
        Icons.Filled.WbCloudy, Icons.Filled.WbCloudy,
        Icons.Filled.Anchor, Icons.Filled.Anchor,
        Icons.Filled.Lightbulb, Icons.Filled.Lightbulb,
        Icons.Filled.Settings, Icons.Filled.Settings,
        Icons.Filled.DirectionsCar, Icons.Filled.DirectionsCar
    )

    var cards by remember { mutableStateOf(iconsList.shuffled().map { MemoryCard(it) }) }
    var selectedIds by remember { mutableStateOf<List<Int>>(emptyList()) }
    var movesCount by remember { mutableStateOf(0) }
    var matchesFound by remember { mutableStateOf(0) }
    var victoryTriggered by remember { mutableStateOf(false) }

    fun handleCardClick(index: Int) {
        if (victoryTriggered || cards[index].isFaceUp || cards[index].isMatched || selectedIds.size >= 2) return

        // Flip card up
        cards = cards.toMutableList().apply {
            this[index] = this[index].copy(isFaceUp = true)
        }
        val currentSelected = selectedIds + index
        selectedIds = currentSelected

        if (currentSelected.size == 2) {
            movesCount++
            val firstIdx = currentSelected[0]
            val secondIdx = currentSelected[1]
            if (cards[firstIdx].icon == cards[secondIdx].icon) {
                // Match found!
                cards = cards.toMutableList().apply {
                    this[firstIdx] = this[firstIdx].copy(isMatched = true)
                    this[secondIdx] = this[secondIdx].copy(isMatched = true)
                }
                matchesFound++
                selectedIds = emptyList()
                if (matchesFound == 8) {
                    victoryTriggered = true
                }
            } else {
                // Wait and Flip back down
                // We use coroutine scope simulation in game thread
            }
        }
    }

    // Effect to flip cards back down if they didn't match after delay
    LaunchedEffect(selectedIds) {
        if (selectedIds.size == 2) {
            delay(1000)
            val firstIdx = selectedIds[0]
            val secondIdx = selectedIds[1]
            if (!cards[firstIdx].isMatched) {
                cards = cards.toMutableList().apply {
                    this[firstIdx] = this[firstIdx].copy(isFaceUp = false)
                    this[secondIdx] = this[secondIdx].copy(isFaceUp = false)
                }
            }
            selectedIds = emptyList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Tracker Statistics
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0x35000000))) {
                Text(
                    text = "Moves: $movesCount",
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontWeight = FontWeight.Bold
                )
            }
            Card(colors = CardDefaults.cardColors(containerColor = Color(0x35000000))) {
                Text(
                    text = "Matches: $matchesFound / 8",
                    color = Color.Green,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (victoryTriggered) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .border(1.dp, Color.Cyan, RoundedCornerShape(12.dp))
                    .background(Color(0xD0151828))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color.Green, modifier = Modifier.size(56.dp))
                    Text(text = "MATRIX MERGED!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(top = 12.dp))
                    Text(text = "Perfect memory synchronization achieved in $movesCount moves.", color = Color.LightGray, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 4.dp))
                    Button(
                        onClick = {
                            cards = iconsList.shuffled().map { MemoryCard(it) }
                            movesCount = 0
                            matchesFound = 0
                            victoryTriggered = false
                            selectedIds = emptyList()
                        },
                        modifier = Modifier.padding(top = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan)
                    ) {
                        Text(text = "Reset Quantum Matrix", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.aspectRatio(1f)
            ) {
                items(cards.size) { index ->
                    val card = cards[index]
                    val isFlipped = card.isFaceUp || card.isMatched

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isFlipped) Color(0xFF0F2B5C) else Color(0xFF1E2235))
                            .border(
                                1.dp,
                                if (isFlipped) Color.Cyan else Color(0xFF323955),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { handleCardClick(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isFlipped) {
                            Icon(card.icon, contentDescription = null, tint = if (card.isMatched) Color.Green else Color.Cyan, modifier = Modifier.size(28.dp))
                        } else {
                            Text(text = "?", color = Color.DarkGray, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

data class MemoryCard(
    val icon: ImageVector,
    val isFaceUp: Boolean = false,
    val isMatched: Boolean = false
)

// ============================================
// GAME 2: TIC-TAC-TOE AI OVERLORD (WITH CHAT TIME)
// ============================================
@Composable
fun TicTacToeOverlordGame() {
    var board by remember { mutableStateOf(List(9) { "" }) } // "", "X", "O"
    var isUserTurn by remember { mutableStateOf(true) }
    var gameStatusText by remember { mutableStateOf("Your Turn. Match against Overlord!") }
    var userScore by remember { mutableStateOf(0) }
    var aiScore by remember { mutableStateOf(0) }
    var drawScore by remember { mutableStateOf(0) }

    fun checkWinner(b: List<String>): String? {
        val windPatterns = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // horizontal
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // vertical
            listOf(0, 4, 8), listOf(2, 4, 6) // diagonal
        )
        for (pattern in windPatterns) {
            if (b[pattern[0]] != "" && b[pattern[0]] == b[pattern[1]] && b[pattern[1]] == b[pattern[2]]) {
                return b[pattern[0]]
            }
        }
        if (!b.contains("")) return "Draw"
        return null
    }

    fun makeAIMove() {
        // Look for winning move first
        // If none, take center, else random empty spot
        val emptyIndices = board.indices.filter { board[it] == "" }
        if (emptyIndices.isEmpty()) return

        // 1. Can AI Win?
        for (idx in emptyIndices) {
            val tempBoard = board.toMutableList()
            tempBoard[idx] = "O"
            if (checkWinner(tempBoard) == "O") {
                board = board.toMutableList().apply { this[idx] = "O" }
                return
            }
        }

        // 2. Can User Win? Block them.
        for (idx in emptyIndices) {
            val tempBoard = board.toMutableList()
            tempBoard[idx] = "X"
            if (checkWinner(tempBoard) == "X") {
                board = board.toMutableList().apply { this[idx] = "O" }
                return
            }
        }

        // 3. Take center if available
        if (board[4] == "") {
            board = board.toMutableList().apply { this[4] = "O" }
            return
        }

        // 4. Take random
        val randomIdx = emptyIndices.random()
        board = board.toMutableList().apply { this[randomIdx] = "O" }
    }

    LaunchedEffect(isUserTurn) {
        if (!isUserTurn) {
            delay(600) // think duration
            makeAIMove()
            val win = checkWinner(board)
            if (win != null) {
                if (win == "O") {
                    aiScore++
                    gameStatusText = "AI Overlord Won! Try again."
                } else if (win == "Draw") {
                    drawScore++
                    gameStatusText = "Draw! A tight mental match."
                }
                isUserTurn = true
            } else {
                gameStatusText = "Your turn."
                isUserTurn = true
            }
        }
    }

    fun handleCellClick(index: Int) {
        if (!isUserTurn || board[index] != "" || checkWinner(board) != null) return

        board = board.toMutableList().apply { this[index] = "X" }
        val win = checkWinner(board)
        if (win != null) {
            if (win == "X") {
                userScore++
                gameStatusText = "You Defeated AI Overlord! Epic achievement!"
            } else if (win == "Draw") {
                drawScore++
                gameStatusText = "Quantum Match Draw!"
            }
        } else {
            gameStatusText = "AI Overlord is computing pathway..."
            isUserTurn = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Score Board
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "USER (X)", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                Text(text = "$userScore", fontSize = 24.sp, color = Color.Cyan, fontWeight = FontWeight.Black)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "DRAWS", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                Text(text = "$drawScore", fontSize = 24.sp, color = Color.White, fontWeight = FontWeight.Black)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "OVERLORD (O)", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                Text(text = "$aiScore", fontSize = 24.sp, color = Color.Yellow, fontWeight = FontWeight.Black)
            }
        }

        // Live status
        Text(
            text = gameStatusText,
            color = if (gameStatusText.contains("Won")) Color.Green else Color.LightGray,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Grid
        Box(
            modifier = Modifier
                .width(280.dp)
                .aspectRatio(1f)
                .background(Color(0xFF131524), RoundedCornerShape(12.dp))
                .border(2.dp, Color(0xFF22263F), RoundedCornerShape(12.dp))
                .padding(8.dp)
        ) {
            Column {
                for (row in 0..2) {
                    Row(modifier = Modifier.weight(1f)) {
                        for (col in 0..2) {
                            val index = row * 3 + col
                            val cell = board[index]
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize()
                                    .padding(6.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1B1E34))
                                    .border(
                                        1.dp,
                                        if (cell == "X") Color.Cyan else if (cell == "O") Color.Yellow else Color(
                                            0x1CFFFFFF
                                        ),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { handleCellClick(index) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cell,
                                    color = if (cell == "X") Color.Cyan else Color.Yellow,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                board = List(9) { "" }
                isUserTurn = true
                gameStatusText = "Grid refreshed. Match Overlord!"
            },
            modifier = Modifier.padding(top = 28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan)
        ) {
            Text(text = "Reroll Next Match", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

// ============================================
// GAME 3: COSMIC STAR CATCHER (RETRO ARCADE CONTROLLER ON CANVAS)
// ============================================
data class DustStar(
    val id: String = UUID.randomUUID().toString(),
    var x: Float,
    var y: Float,
    val speed: Float,
    val isMeteor: Boolean
)

@Composable
fun CosmicStarCatcherGame() {
    var playerX by remember { mutableStateOf(0.5f) } // 0.0f (left) to 1.0f (right)
    var stars by remember { mutableStateOf(emptyList<DustStar>()) }
    var score by remember { mutableStateOf(0) }
    var health by remember { mutableStateOf(3) } // 3 Hearts
    var isGameOver by remember { mutableStateOf(false) }

    // Star generation routine
    LaunchedEffect(isGameOver) {
        if (!isGameOver) {
            stars = emptyList()
            score = 0
            health = 3
            while (!isGameOver) {
                delay(1200)
                val newX = (0..100).random().toFloat() / 100f
                val isMeteor = (1..100).random() < 30 // 30% chance for red meteor
                stars = stars + DustStar(x = newX, y = -10f, speed = (4..8).random().toFloat(), isMeteor = isMeteor)
            }
        }
    }

    // Active frame movement routine
    LaunchedEffect(isGameOver) {
        while (!isGameOver) {
            delay(30) // ~30 FPS
            val updatedStars = mutableListOf<DustStar>()
            for (star in stars) {
                // fall
                star.y += star.speed
                
                // check collision
                if (star.y >= 540f && star.y < 580f) {
                    val distanceX = kotlin.math.abs(star.x - playerX)
                    if (distanceX < 0.15f) {
                        // collision!
                        if (star.isMeteor) {
                            health--
                            if (health <= 0) {
                                isGameOver = true
                            }
                        } else {
                            score += 10
                        }
                        continue // remove star
                    }
                }

                // If out of bounds
                if (star.y > 600f) {
                    continue // remove star
                }
                updatedStars.add(star)
            }
            stars = updatedStars
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top stats container
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "COSMIC SCORE: $score",
                color = Color.Yellow,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp
            )

            Row {
                repeat(3) { index ->
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = if (index < health) Color.Red else Color.DarkGray,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(horizontal = 2.dp)
                    )
                }
            }
        }

        if (isGameOver) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .border(1.dp, Color.Red, RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E1118))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.ReportGmailerrorred, contentDescription = null, tint = Color.Red, modifier = Modifier.size(64.dp))
                    Text(text = "ATMOSPHERE CRITICAL", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = "Final Particle Yield: $score points", color = Color.LightGray, modifier = Modifier.padding(top = 8.dp))
                    Button(
                        onClick = { isGameOver = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow),
                        modifier = Modifier.padding(top = 20.dp)
                    ) {
                        Text(text = "Re-enter Atmosphere", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // ARCADE SCREEN CANVAS
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF07080F))
                    .border(2.dp, Color(0xFF191C36), RoundedCornerShape(8.dp))
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val sensitivity = dragAmount.x / size.width.toFloat()
                            playerX = (playerX + sensitivity).coerceIn(0.05f, 0.95f)
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    
                    // Draw stars
                    for (star in stars) {
                        val mappedX = star.x * canvasWidth
                        val mappedY = star.y * (canvasHeight / 600f)
                        drawCircle(
                            color = if (star.isMeteor) Color.Red else Color.Cyan,
                            radius = if (star.isMeteor) 12f else 8f,
                            center = Offset(mappedX, mappedY)
                        )
                    }

                    // Draw Player Ship / Nest
                    val mappedPlayerX = playerX * canvasWidth
                    val shipWidth = 80f
                    val shipHeight = 20f
                    val topOffset = canvasHeight - 40f
                    
                    drawRoundRect(
                        color = Color.Yellow,
                        topLeft = Offset(mappedPlayerX - (shipWidth / 2f), topOffset),
                        size = androidx.compose.ui.geometry.Size(shipWidth, shipHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
                    )
                }
            }

            // Controllers Panel at Bottom
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { playerX = (playerX - 0.15f).coerceIn(0.05f, 0.95f) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF191C36)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Shift Left", tint = Color.Cyan)
                }

                Button(
                    onClick = { playerX = (playerX + 0.15f).coerceIn(0.05f, 0.95f) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF191C36)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                ) {
                    Icon(Icons.Filled.ArrowForward, contentDescription = "Shift Right", tint = Color.Cyan)
                }
            }
        }
    }
}

// ============================================
// GAME 4: NEON CORE CLICKER
// ============================================
@Composable
fun CosmicClickerGame() {
    var clicks by remember { mutableStateOf(0) }
    var timerRunning by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableStateOf(15) }
    var coreScale by remember { mutableStateOf(1f) }
    val coroutine = rememberCoroutineScope()

    LaunchedEffect(timerRunning) {
        if (timerRunning) {
            timeLeft = 15
            clicks = 0
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            timerRunning = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("NEON CORE CLICKER", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (timerRunning) "Time Remaining: ${timeLeft}s" else "Game Finished/Idle",
            color = Color.Yellow,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(24.dp))

        val animateScale by animateFloatAsState(
            targetValue = coreScale,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )

        Box(
            modifier = Modifier
                .size(160.dp)
                .graphicsLayer(scaleX = animateScale, scaleY = animateScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = if (timerRunning) listOf(Color(0xFFE040FB), Color(0xFF6A1B9A)) else listOf(Color(0xFF424242), Color(0xFF212121))
                    )
                )
                .border(4.dp, Color(0xFFE040FB), CircleShape)
                .clickable {
                    if (!timerRunning) {
                        timerRunning = true
                    } else {
                        clicks++
                        coroutine.launch {
                            coreScale = 1.3f
                            delay(50)
                            coreScale = 1.0f
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.Bolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                Text(
                    text = if (timerRunning) "$clicks" else "TAP CORE",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Total Score: ${clicks * 10} Quantum Points",
            color = Color.Cyan,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = when {
                clicks == 0 -> "Tap the core to begin! Fast clicks build score!"
                clicks < 30 -> "Rating: Neural Initiate"
                clicks < 55 -> "Rating: Synapse Speedster"
                else -> "Rating: Cyber Tapper Overlord!"
            },
            color = Color.LightGray,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

// ============================================
// GAME 5: REFLEX MATRIX TRIGGER
// ============================================
@Composable
fun ReflexMatrixGame() {
    var score by remember { mutableStateOf(0) }
    var lives by remember { mutableStateOf(3) }
    var activeCell by remember { mutableStateOf((0..8).random()) }
    var isGameOver by remember { mutableStateOf(false) }
    var timeLimit by remember { mutableStateOf(1000L) }
    var interactionCount by remember { mutableStateOf(0) }

    LaunchedEffect(interactionCount, isGameOver) {
        if (!isGameOver) {
            activeCell = (0..8).random()
            timeLimit = maxOf(450L, 1000L - (score * 20L))
            val currentInteraction = interactionCount
            delay(timeLimit)
            if (interactionCount == currentInteraction && !isGameOver) {
                lives--
                if (lives <= 0) {
                    isGameOver = true
                } else {
                    interactionCount++
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("REFLEX TRIGGER", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Lives: ${"❤️ ".repeat(lives)}", color = Color(0xFFFF5252), fontSize = 14.sp)
            Text("Score: $score", color = Color.Green, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isGameOver) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0x33FF5252)),
                border = BorderStroke(1.dp, Color(0xFFFF5252))
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("MATRIX SHUTDOWN", color = Color(0xFFFF5252), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Your reflex speed collapsed.", color = Color.LightGray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            score = 0
                            lives = 3
                            isGameOver = false
                            interactionCount++
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF88))
                    ) {
                        Text("Reboot Cell Map", color = Color.Black)
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.size(240.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0B10)),
                border = BorderStroke(1.dp, Color(0x1FFFFFFF))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (row in 0 until 3) {
                        Row(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (col in 0 until 3) {
                                val cellIndex = row * 3 + col
                                val isActive = activeCell == cellIndex

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isActive) Color(0xFF00FF88) else Color(0xFF1E212D))
                                        .clickable {
                                            if (isActive) {
                                                score += 10
                                                interactionCount++
                                            } else {
                                                lives--
                                                if (lives <= 0) {
                                                    isGameOver = true
                                                    activeCell = -1
                                                } else {
                                                    interactionCount++
                                                }
                                            }
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================
// GAME 6: LUCKY DICE QUEST
// ============================================
@Composable
fun DiceQuestGame() {
    var dieOne by remember { mutableStateOf(1) }
    var dieTwo by remember { mutableStateOf(1) }
    var score by remember { mutableStateOf(0) }
    var rollsLeft by remember { mutableStateOf(10) }
    var message by remember { mutableStateOf("Roll the Dice Core nodes to acquire multipliers!") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("DICE QUEST ENGINE", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(dieOne, dieTwo).forEach { diceValue ->
                Card(
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2235)),
                    border = BorderStroke(2.dp, Color.White)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("$diceValue", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = message, color = Color.Yellow, fontSize = 14.sp, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Rolls Remaining: $rollsLeft", color = Color.LightGray, fontSize = 12.sp)
            Text("Active Score: $score", color = Color.Cyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (rollsLeft > 0) {
                    dieOne = (1..6).random()
                    dieTwo = (1..6).random()
                    rollsLeft--

                    val sum = dieOne + dieTwo
                    when {
                        dieOne == dieTwo -> {
                            score += sum * 5
                            message = "CRITICAL STRIKE! Double $dieOne! Added ${sum * 5} pts!"
                        }
                        sum == 7 || sum == 11 -> {
                            score += sum * 2
                            message = "LUCKY CHANNELS! Sum = $sum! Added ${sum * 2} pts!"
                        }
                        else -> {
                            score += sum
                            message = "Standard connection. Added $sum pts."
                        }
                    }
                } else {
                    message = "Energy exhausted. Quest Score: $score!"
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan)
        ) {
            Text(text = if (rollsLeft > 0) "Trigger Twin Cubes" else "Quest Completed", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        if (rollsLeft == 0) {
            Button(
                onClick = {
                    dieOne = 1
                    dieTwo = 1
                    rollsLeft = 10
                    score = 0
                    message = "Quest reset. Roll the Dice Core nodes!"
                },
                modifier = Modifier.padding(top = 12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("Start New Dice Journey")
            }
        }
    }
}

// ============================================
// GAME 7: HIDDEN CODE CIPHER
// ============================================
@Composable
fun CodeCipherGame() {
    var secretCode by remember { mutableStateOf("") }
    var digit1 by remember { mutableStateOf(0) }
    var digit2 by remember { mutableStateOf(0) }
    var digit3 by remember { mutableStateOf(0) }
    var attempts by remember { mutableStateOf(6) }
    var statusMessage by remember { mutableStateOf("Attempt to crack the 3-digit neural connection cipher.") }
    var cracked by remember { mutableStateOf(false) }

    LaunchedEffect(cracked) {
        if (secretCode.isEmpty()) {
            secretCode = "${(0..9).random()}${(0..9).random()}${(0..9).random()}"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("CONNECTION CIPHER", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(
                digit1 to { v: Int -> digit1 = v },
                digit2 to { v: Int -> digit2 = v },
                digit3 to { v: Int -> digit3 = v }
            ).forEach { (value, setter) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { setter((value + 1) % 10) }) {
                        Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Add", tint = Color.Cyan)
                    }
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(Color(0xFF131525), RoundedCornerShape(8.dp))
                            .border(1.dp, Color.Cyan, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("$value", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = { setter((value + 9) % 10) }) {
                        Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Subtract", tint = Color.Cyan)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = statusMessage, color = if (cracked) Color.Green else Color.Yellow, fontSize = 13.sp, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Remaining Tries: $attempts", color = Color.LightGray, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(20.dp))

        if (!cracked && attempts > 0) {
            Button(
                onClick = {
                    val guess = "$digit1$digit2$digit3"
                    if (guess == secretCode) {
                        cracked = true
                        statusMessage = "CIPHER CRACKED! Connection fully decrypted."
                    } else {
                        attempts--
                        val guessVal = guess.toInt()
                        val secretVal = secretCode.toInt()
                        val comparison = if (guessVal > secretVal) "Too High" else "Too Low"
                        statusMessage = "Rejected. Guess ($guess) is $comparison."
                        if (attempts == 0) {
                            statusMessage = "CIPHER BLOCKED. Access Key was: $secretCode."
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan)
            ) {
                Text("Transmit Access Key", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        } else {
            Button(
                onClick = {
                    secretCode = "${(0..9).random()}${(0..9).random()}${(0..9).random()}"
                    attempts = 6
                    digit1 = 0
                    digit2 = 0
                    digit3 = 0
                    cracked = false
                    statusMessage = "Cipher reset. Crack the 3-digit cipher."
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("Regenerate Connection Cipher", color = Color.Black)
            }
        }
    }
}

// ============================================
// GAME 8: CHROMATIC SPEED MATCH
// ============================================
@Composable
fun ColorMatchGame() {
    val wordList = listOf("RED", "GREEN", "BLUE", "YELLOW")
    val colorList = listOf(Color(0xFFFF5252), Color(0xFF00FF88), Color(0xFF2979FF), Color(0xFFFFD700))

    var activeWordIndex by remember { mutableStateOf((0..3).random()) }
    var activeColorIndex by remember { mutableStateOf((0..3).random()) }
    var score by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(30) }
    var isPlaying by remember { mutableStateOf(false) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            timeLeft = 30
            score = 0
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            isPlaying = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("STROOP SPEED MATRIX", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        if (!isPlaying) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131525)),
                border = BorderStroke(1.dp, Color(0x33FFFFFF))
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Test Cognitive Speed", color = Color.LightGray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Instruction: Choose MATCH if the text COLOR matches the literal word, otherwise choose MISMATCH!", color = Color.Gray, fontSize = 11.sp, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { isPlaying = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan)
                    ) {
                        Text("Initiate Speed Match", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
            if (score > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Last Score: $score", color = Color.Yellow, fontSize = 14.sp)
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Sec: ${timeLeft}s", color = Color.Yellow, fontSize = 13.sp)
                Text("Score: $score", color = Color.Green, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color(0xFF0F101A), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = wordList[activeWordIndex],
                    color = colorList[activeColorIndex],
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val checkAnswer = { answerIsMatch: Boolean ->
                    val actualMatch = activeWordIndex == activeColorIndex
                    if (answerIsMatch == actualMatch) {
                        score += 10
                    } else {
                        score = maxOf(0, score - 5)
                    }
                    activeWordIndex = (0..3).random()
                    activeColorIndex = (0..3).random()
                }

                Button(
                    onClick = { checkAnswer(true) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))
                ) {
                    Text("MATCH", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { checkAnswer(false) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                ) {
                    Text("MISMATCH", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ============================================
// GAME 9: QUANTUM 2048 SANDBOX
// ============================================
@Composable
fun Quantum2048Game() {
    var board by remember { mutableStateOf(listOf(2, 0, 0, 0, 2, 0, 0, 0, 0)) }
    var score by remember { mutableStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }

    val colorsMap = mapOf(
        0 to Color(0xFF1E212D),
        2 to Color(0xFF2979FF),
        4 to Color(0xFF00FF88),
        8 to Color(0xFFFFCC80),
        16 to Color(0xFFFF9100),
        32 to Color(0xFFE040FB),
        64 to Color(0xFFFF5252),
        128 to Color(0xFFFFD700)
    )

    fun spawnTile(currentBoard: List<Int>): List<Int> {
        val nextBoard = currentBoard.toMutableList()
        val emptyIndices = nextBoard.indices.filter { nextBoard[it] == 0 }
        if (emptyIndices.isNotEmpty()) {
            val pickIndex = emptyIndices.random()
            nextBoard[pickIndex] = if ((1..100).random() < 80) 2 else 4
        }
        return nextBoard
    }

    fun slideRow(r: List<Int>): List<Int> {
        val nonZeros = r.filter { it != 0 }
        val mergedList = mutableListOf<Int>()
        var i = 0
        while (i < nonZeros.size) {
            if (i < nonZeros.size - 1 && nonZeros[i] == nonZeros[i + 1]) {
                val value = nonZeros[i] * 2
                mergedList.add(value)
                score += value
                i += 2
            } else {
                mergedList.add(nonZeros[i])
                i++
            }
        }
        while (mergedList.size < 3) {
            mergedList.add(0)
        }
        return mergedList
    }

    fun makeMove(dir: String) {
        val nextList = board.toMutableList()
        val original = board.toList()

        when (dir) {
            "LEFT" -> {
                for (r in 0 until 3) {
                    val slice = listOf(board[r * 3], board[r * 3 + 1], board[r * 3 + 2])
                    val result = slideRow(slice)
                    nextList[r * 3] = result[0]
                    nextList[r * 3 + 1] = result[1]
                    nextList[r * 3 + 2] = result[2]
                }
            }
            "RIGHT" -> {
                for (r in 0 until 3) {
                    val slice = listOf(board[r * 3 + 2], board[r * 3 + 1], board[r * 3])
                    val result = slideRow(slice)
                    nextList[r * 3 + 2] = result[0]
                    nextList[r * 3 + 1] = result[1]
                    nextList[r * 3] = result[2]
                }
            }
            "UP" -> {
                for (c in 0 until 3) {
                    val slice = listOf(board[c], board[c + 3], board[c + 6])
                    val result = slideRow(slice)
                    nextList[c] = result[0]
                    nextList[c + 3] = result[1]
                    nextList[c + 6] = result[2]
                }
            }
            "DOWN" -> {
                for (c in 0 until 3) {
                    val slice = listOf(board[c + 6], board[c + 3], board[c])
                    val result = slideRow(slice)
                    nextList[c + 6] = result[0]
                    nextList[c + 3] = result[1]
                    nextList[c] = result[2]
                }
            }
        }

        if (nextList.toList() != original) {
            val spawned = spawnTile(nextList)
            board = spawned
            if (!spawned.contains(0)) {
                isGameOver = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("QUANTUM 2048 SANDBOX", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Power Nodes", color = Color.LightGray, fontSize = 12.sp)
            Text("Score: $score", color = Color.Cyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.size(240.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F101A)),
            border = BorderStroke(2.dp, Color(0xFF2979FF))
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.SpaceEvenly) {
                for (row in 0 until 3) {
                    Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                        for (col in 0 until 3) {
                            val value = board[row * 3 + col]
                            val bgColor = colorsMap[value] ?: Color(0xFF1E212D)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(bgColor),
                                contentAlignment = Alignment.Center
                            ) {
                                if (value > 0) {
                                    Text(
                                        text = "$value",
                                        color = if (value >= 16) Color.Black else Color.White,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Slide Gestures controllers
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row {
                IconButton(onClick = { makeMove("UP") }) {
                    Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Slide Up", tint = Color.Cyan, modifier = Modifier.size(36.dp))
                }
            }
            Row {
                IconButton(onClick = { makeMove("LEFT") }) {
                    Icon(Icons.Filled.KeyboardArrowLeft, contentDescription = "Slide Left", tint = Color.Cyan, modifier = Modifier.size(36.dp))
                }
                Spacer(modifier = Modifier.width(36.dp))
                IconButton(onClick = { makeMove("RIGHT") }) {
                    Icon(Icons.Filled.KeyboardArrowRight, contentDescription = "Slide Right", tint = Color.Cyan, modifier = Modifier.size(36.dp))
                }
            }
            Row {
                IconButton(onClick = { makeMove("DOWN") }) {
                    Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Slide Down", tint = Color.Cyan, modifier = Modifier.size(36.dp))
                }
            }
        }

        if (isGameOver) {
            Button(
                onClick = {
                    board = listOf(2, 0, 0, 0, 2, 0, 0, 0, 0)
                    score = 0
                    isGameOver = false
                },
                modifier = Modifier.padding(top = 12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)
            ) {
                Text("Re-align Power Nodes", color = Color.Black)
            }
        }
    }
}

// ============================================
// GAME 10: CODE TYPING CHAMPION
// ============================================
@Composable
fun TypeChampionGame() {
    val phrases = listOf(
        "val connection = GeminiClient.createNeuralLink()",
        "fun executeQuantumLoop(seed: Long): Flow<State>",
        "suspend fun emitActivePulse(nodes: List<MatrixNode>)",
        "class StudioManager(val sandbox: CybernetSandbox)"
    )

    var targetPhraseIndex by remember { mutableStateOf(0) }
    var userInput by remember { mutableStateOf("") }
    var score by remember { mutableStateOf(0) }
    var streak by remember { mutableStateOf(0) }
    var message by remember { mutableStateOf("Type the functional snippet exactly to boost streak!") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("CODE SPELLING SPRINT", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F101A)),
            border = BorderStroke(1.dp, Color(0x33FFFFFF))
        ) {
            Text(
                text = phrases[targetPhraseIndex],
                color = Color.Yellow,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = userInput,
            onValueChange = { inputVal ->
                userInput = inputVal
                val target = phrases[targetPhraseIndex]
                if (inputVal == target) {
                    score += 20 + streak * 5
                    streak++
                    userInput = ""
                    targetPhraseIndex = (phrases.indices).filter { it != targetPhraseIndex }.random()
                    message = "CONNECTION SECURED! WPM Match active!"
                } else if (target.startsWith(inputVal)) {
                    message = "Active streaming matched..."
                } else {
                    message = "Syntax mismatch detected! Realign keys."
                    streak = 0
                }
            },
            placeholder = { Text("Begin writing snippet here...", color = Color.Gray, fontSize = 12.sp) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF131525),
                unfocusedContainerColor = Color(0xFF131525),
                focusedIndicatorColor = Color.Cyan
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = message, color = if (streak > 0) Color.Green else Color.LightGray, fontSize = 13.sp, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Multiplier Streak: x$streak", color = Color.Yellow, fontSize = 12.sp)
            Text("Active Score: $score", color = Color.Cyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
