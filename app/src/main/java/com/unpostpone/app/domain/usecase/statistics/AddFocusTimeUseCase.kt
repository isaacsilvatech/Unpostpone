package com.unpostpone.app.domain.usecase.statistics

import com.unpostpone.app.domain.repository.StatisticsRepository
import javax.inject.Inject

class AddFocusTimeUseCase @Inject constructor(
    private val repository: StatisticsRepository
) {
    suspend operator fun invoke(date: String, minutes: Int) {
        repository.ensureDateExists(date)
        repository.addFocusedMinutes(date, minutes)
    }
}
