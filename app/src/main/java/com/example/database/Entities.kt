package com.example.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_sessions")
data class ChatSession(
    @PrimaryKey val sessionId: String,
    val title: String,
    val category: String, // "General", "Coding", "Research", "Image", "Video", "Voice"
    val lastActive: Long = System.currentTimeMillis(),
    val pinned: Boolean = false,
    val model: String = "gemini-3.5-flash"
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey val messageId: String,
    val sessionId: String,
    val role: String, // "user" or "model"
    val text: String,
    val thinkingProcess: String? = null,
    val citationsJson: String? = null, // JSON list of search results / citations
    val imageUrl: String? = null, // for image/vision capability
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_states")
data class UserState(
    @PrimaryKey val userId: String = "current_user",
    val dailyStreak: Int = 1,
    val lastUpdateDate: String = "", // e.g., "2026-05-31"
    val chatTimeRemainingSeconds: Long = 7200, // 2 hours initial
    val totalEarnedSeconds: Long = 0,
    val lastDailyRewardCollectedDate: String = "",
    val isOnboardingCompleted: Boolean = false,
    val googleEmail: String? = null,
    val googleDisplayName: String? = null,
    val googlePhotoUrl: String? = null,
    val isLoggedIn: Boolean = false
)
