package com.unpostpone.app.domain.usecase.statistics

import com.unpostpone.app.domain.model.Statistics
import com.unpostpone.app.domain.repository.StatisticsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStatisticsUseCase @Inject constructor(
    private val repository: StatisticsRepository
) {
    operator fun invoke(): Flow<List<Statistics>> = repository.getRecentStatistics()
    fun forDate(date: String): Flow<Statistics?> = repository.getStatisticsByDate(date)
}
