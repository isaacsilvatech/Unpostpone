package com.unpostpone.app.data.repository

import com.unpostpone.app.data.datasource.StatisticsLocalDataSource
import com.unpostpone.app.data.local.entity.StatisticsEntity
import com.unpostpone.app.domain.model.Statistics
import com.unpostpone.app.domain.repository.StatisticsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StatisticsRepositoryImpl @Inject constructor(
    private val localDataSource: StatisticsLocalDataSource
) : StatisticsRepository {

    override fun getStatisticsByDate(date: String): Flow<Statistics?> =
        localDataSource.getStatisticsByDate(date).map { it?.toDomain() }

    override fun getRecentStatistics(): Flow<List<Statistics>> =
        localDataSource.getRecentStatistics().map { it.map(StatisticsEntity::toDomain) }

    override suspend fun ensureDateExists(date: String) =
        localDataSource.ensureDateExists(date)

    override suspend fun incrementBlockCount(date: String) =
        localDataSource.incrementBlockCount(date)

    override suspend fun incrementUnlockAttempts(date: String) =
        localDataSource.incrementUnlockAttempts(date)

    override suspend fun addFocusedMinutes(date: String, minutes: Int) =
        localDataSource.addFocusedMinutes(date, minutes)
}

private fun StatisticsEntity.toDomain() = Statistics(
    date = date, focusedMinutes = focusedMinutes,
    blockCount = blockCount, unlockAttempts = unlockAttempts
)
