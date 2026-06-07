package com.unpostpone.app.domain.usecase.goal

import com.unpostpone.app.domain.model.Goal
import com.unpostpone.app.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGoalsUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    operator fun invoke(date: String): Flow<List<Goal>> = repository.getGoalsByDate(date)
}
