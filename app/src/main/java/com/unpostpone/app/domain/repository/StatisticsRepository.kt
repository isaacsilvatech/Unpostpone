package com.unpostpone.app.domain.repository

import com.unpostpone.app.domain.model.Statistics
import kotlinx.coroutines.flow.Flow

interface StatisticsRepository {
    fun getStatisticsByDate(date: String): Flow<Statistics?>
    fun getRecentStatistics(): Flow<List<Statistics>>
    suspend fun ensureDateExists(date: String)
    suspend fun incrementBlockCount(date: String)
    suspend fun incrementUnlockAttempts(date: String)
    suspend fun addFocusedMinutes(date: String, minutes: Int)
}
