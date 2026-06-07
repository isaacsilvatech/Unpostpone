package com.unpostpone.app.domain.repository

import com.unpostpone.app.domain.model.Goal
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    fun getGoalsByDate(date: String): Flow<List<Goal>>
    fun getAllGoals(): Flow<List<Goal>>
    suspend fun addGoal(goal: Goal): Long
    suspend fun updateGoal(goal: Goal)
    suspend fun deleteGoal(goal: Goal)
    suspend fun updateProgress(id: Long, progressMinutes: Int, isCompleted: Boolean)
}
