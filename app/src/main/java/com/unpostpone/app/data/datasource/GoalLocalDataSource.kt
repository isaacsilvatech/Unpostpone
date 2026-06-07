package com.unpostpone.app.data.datasource

import com.unpostpone.app.data.local.dao.GoalDao
import com.unpostpone.app.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GoalLocalDataSource @Inject constructor(
    private val goalDao: GoalDao
) {
    fun getGoalsByDate(date: String): Flow<List<GoalEntity>> = goalDao.getGoalsByDate(date)
    fun getAllGoals(): Flow<List<GoalEntity>> = goalDao.getAllGoals()
    suspend fun getGoalById(id: Long): GoalEntity? = goalDao.getGoalById(id)
    suspend fun insertGoal(goal: GoalEntity): Long = goalDao.insertGoal(goal)
    suspend fun updateGoal(goal: GoalEntity) = goalDao.updateGoal(goal)
    suspend fun deleteGoal(goal: GoalEntity) = goalDao.deleteGoal(goal)
    suspend fun updateProgress(id: Long, progress: Int, isCompleted: Boolean) =
        goalDao.updateProgress(id, progress, isCompleted)
}
