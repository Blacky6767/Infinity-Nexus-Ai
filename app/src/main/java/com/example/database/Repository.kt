package com.example.database

import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NexusRepository(
    private val chatDao: ChatDao,
    private val userStateDao: UserStateDao
) {
    val allSessions: Flow<List<ChatSession>> = chatDao.getAllSessionsFlow()
    
    fun getMessagesForSession(sessionId: String): Flow<List<ChatMessage>> {
        return chatDao.getMessagesForSessionFlow(sessionId)
    }

    suspend fun getMessagesForSessionList(sessionId: String): List<ChatMessage> {
        return chatDao.getMessagesForSession(sessionId)
    }

    suspend fun getSessionById(sessionId: String): ChatSession? {
        return chatDao.getSessionById(sessionId)
    }

    suspend fun insertSession(session: ChatSession) {
        chatDao.insertSession(session)
    }

    suspend fun updateSession(session: ChatSession) {
        chatDao.updateSession(session)
    }

    suspend fun insertMessage(message: ChatMessage) {
        chatDao.insertMessage(message)
        // Also update lastActive of the session
        chatDao.getSessionById(message.sessionId)?.let { session ->
            chatDao.insertSession(session.copy(lastActive = System.currentTimeMillis()))
        }
    }

    suspend fun deleteSession(sessionId: String) {
        chatDao.deleteSession(sessionId)
        chatDao.deleteMessagesForSession(sessionId)
    }

    // --- User State & Time Limits & Streaks ---
    val userStateFlow: Flow<UserState?> = userStateDao.getUserStateFlow()

    suspend fun getUserState(): UserState {
        var state = userStateDao.getUserState()
        if (state == null) {
            state = UserState(
                userId = "current_user",
                dailyStreak = 1,
                lastUpdateDate = getTodayDateString(),
                chatTimeRemainingSeconds = 7200, // 2 hours
                totalEarnedSeconds = 0,
                lastDailyRewardCollectedDate = ""
            )
            userStateDao.insertOrUpdateState(state)
        }
        return state
    }

    suspend fun saveUserState(state: UserState) {
        userStateDao.insertOrUpdateState(state)
    }

    // This handles check-in, streak update, and daily 2 hours rollover
    suspend fun checkDailyRolloverAndStreak(): UserState {
        val lastState = getUserState()
        val today = getTodayDateString()
        
        if (lastState.lastUpdateDate != today) {
            // New day! Let's update streak and roll over remaining time!
            val updatedStreak = when {
                lastState.lastUpdateDate == getYesterdayDateString() -> lastState.dailyStreak + 1
                lastState.lastUpdateDate.isEmpty() -> 1
                else -> 1 // Streak broken
            }
            
            // "if user left some time add that time at next day" -> Rollover!
            // Add 2 hours (7200) to whatever remains
            val rolledOverTime = lastState.chatTimeRemainingSeconds + 7200
            
            val newState = lastState.copy(
                dailyStreak = updatedStreak,
                lastUpdateDate = today,
                chatTimeRemainingSeconds = rolledOverTime
            )
            userStateDao.insertOrUpdateState(newState)
            return newState
        }
        return lastState
    }

    suspend fun consumeChatTime(seconds: Long) {
        val state = getUserState()
        val remaining = maxOf(0L, state.chatTimeRemainingSeconds - seconds)
        userStateDao.insertOrUpdateState(state.copy(chatTimeRemainingSeconds = remaining))
    }

    suspend fun awardBonusTime(seconds: Long) {
        val state = getUserState()
        userStateDao.insertOrUpdateState(
            state.copy(
                chatTimeRemainingSeconds = state.chatTimeRemainingSeconds + seconds,
                totalEarnedSeconds = state.totalEarnedSeconds + seconds
            )
        )
    }

    suspend fun collectDailyReward(): Int {
        val state = getUserState()
        val today = getTodayDateString()
        if (state.lastDailyRewardCollectedDate != today) {
            // Give 30 minutes of chat time (1800 seconds) as daily reward
            val bonusSeconds = 1800L
            userStateDao.insertOrUpdateState(
                state.copy(
                    lastDailyRewardCollectedDate = today,
                    chatTimeRemainingSeconds = state.chatTimeRemainingSeconds + bonusSeconds,
                    totalEarnedSeconds = state.totalEarnedSeconds + bonusSeconds
                )
            )
            return 30 // minutes awarded
        }
        return 0 // already collected
    }

    fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getYesterdayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val yesterday = Date(System.currentTimeMillis() - 86400000L)
        return sdf.format(yesterday)
    }
}
