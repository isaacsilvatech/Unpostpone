package com.unpostpone.app.domain.usecase.statistics

import com.unpostpone.app.domain.repository.StatisticsRepository
import javax.inject.Inject

class IncrementBlockCountUseCase @Inject constructor(
    private val repository: StatisticsRepository
) {
    suspend operator fun invoke(date: String) {
        repository.ensureDateExists(date)
        repository.incrementBlockCount(date)
    }
}
