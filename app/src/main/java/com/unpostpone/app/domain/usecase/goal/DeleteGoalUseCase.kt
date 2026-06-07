package com.unpostpone.app.domain.usecase.goal

import com.unpostpone.app.domain.model.Goal
import com.unpostpone.app.domain.repository.GoalRepository
import javax.inject.Inject

class DeleteGoalUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    suspend operator fun invoke(goal: Goal) = repository.deleteGoal(goal)
}
