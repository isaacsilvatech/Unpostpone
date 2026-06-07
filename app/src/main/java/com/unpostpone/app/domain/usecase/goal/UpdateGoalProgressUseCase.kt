package com.unpostpone.app.domain.usecase.goal

import com.unpostpone.app.domain.repository.GoalRepository
import javax.inject.Inject

class UpdateGoalProgressUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    suspend operator fun invoke(id: Long, progressMinutes: Int, targetMinutes: Int) {
        val isCompleted = progressMinutes >= targetMinutes
        repository.updateProgress(id, progressMinutes.coerceAtMost(targetMinutes), isCompleted)
    }
}
