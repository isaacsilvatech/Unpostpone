package com.unpostpone.app.data.local.dao

import androidx.room.*
import com.unpostpone.app.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Query("SELECT * FROM goals WHERE date = :date ORDER BY createdAt ASC")
    fun getGoalsByDate(date: String): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals ORDER BY createdAt DESC")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE id = :id")
    suspend fun getGoalById(id: Long): GoalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity): Long

    @Update
    suspend fun updateGoal(goal: GoalEntity)

    @Delete
    suspend fun deleteGoal(goal: GoalEntity)

    @Query("UPDATE goals SET progressMinutes = :progress, isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateProgress(id: Long, progress: Int, isCompleted: Boolean)
}
