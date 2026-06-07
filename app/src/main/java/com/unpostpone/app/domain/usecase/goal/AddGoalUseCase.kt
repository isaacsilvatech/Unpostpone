package com.unpostpone.app.domain.usecase.goal

import com.unpostpone.app.domain.model.Goal
import com.unpostpone.app.domain.repository.GoalRepository
import javax.inject.Inject

class AddGoalUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    suspend operator fun invoke(goal: Goal): Result<Long> = runCatching {
        require(goal.name.isNotBlank()) { "O nome da meta não pode estar vazio" }
        require(goal.targetMinutes > 0) { "O tempo alvo deve ser maior que zero" }
        repository.addGoal(goal)
    }
}
