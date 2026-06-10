package com.unpostpone.app.domain.repository

import com.unpostpone.app.domain.model.PomodoroSession
import kotlinx.coroutines.flow.Flow

interface PomodoroSessionRepository {
    fun observeSessionsForDay(startOfDay: Long, endOfDay: Long): Flow<List<PomodoroSession>>
    suspend fun recordSession(session: PomodoroSession): Long
    suspend fun countCompletedFocusSessions(): Int
}
