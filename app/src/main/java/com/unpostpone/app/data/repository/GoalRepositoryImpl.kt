package com.unpostpone.app.data.repository

import com.unpostpone.app.data.datasource.GoalLocalDataSource
import com.unpostpone.app.data.local.entity.GoalEntity
import com.unpostpone.app.domain.model.Goal
import com.unpostpone.app.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GoalRepositoryImpl @Inject constructor(
    private val localDataSource: GoalLocalDataSource
) : GoalRepository {

    override fun getGoalsByDate(date: String): Flow<List<Goal>> =
        localDataSource.getGoalsByDate(date).map { it.map(GoalEntity::toDomain) }

    override fun getAllGoals(): Flow<List<Goal>> =
        localDataSource.getAllGoals().map { it.map(GoalEntity::toDomain) }

    override suspend fun addGoal(goal: Goal): Long =
        localDataSource.insertGoal(goal.toEntity())

    override suspend fun updateGoal(goal: Goal) =
        localDataSource.updateGoal(goal.toEntity())

    override suspend fun deleteGoal(goal: Goal) =
        localDataSource.deleteGoal(goal.toEntity())

    override suspend fun updateProgress(id: Long, progressMinutes: Int, isCompleted: Boolean) =
        localDataSource.updateProgress(id, progressMinutes, isCompleted)
}

private fun GoalEntity.toDomain() = Goal(
    id = id, name = name, targetMinutes = targetMinutes,
    progressMinutes = progressMinutes, isCompleted = isCompleted,
    createdAt = createdAt, date = date
)

private fun Goal.toEntity() = GoalEntity(
    id = id, name = name, targetMinutes = targetMinutes,
    progressMinutes = progressMinutes, isCompleted = isCompleted,
    createdAt = createdAt, date = date
)
