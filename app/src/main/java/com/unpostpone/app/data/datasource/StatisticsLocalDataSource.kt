package com.unpostpone.app.data.datasource

import com.unpostpone.app.data.local.dao.StatisticsDao
import com.unpostpone.app.data.local.entity.StatisticsEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StatisticsLocalDataSource @Inject constructor(
    private val statisticsDao: StatisticsDao
) {
    fun getStatisticsByDate(date: String): Flow<StatisticsEntity?> =
        statisticsDao.getStatisticsByDate(date)

    fun getRecentStatistics(): Flow<List<StatisticsEntity>> =
        statisticsDao.getRecentStatistics()

    suspend fun ensureDateExists(date: String) =
        statisticsDao.insertStatistics(StatisticsEntity(date = date))

    suspend fun incrementBlockCount(date: String) =
        statisticsDao.incrementBlockCount(date)

    suspend fun incrementUnlockAttempts(date: String) =
        statisticsDao.incrementUnlockAttempts(date)

    suspend fun addFocusedMinutes(date: String, minutes: Int) =
        statisticsDao.addFocusedMinutes(date, minutes)
}
