package com.unpostpone.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.unpostpone.app.data.local.entity.PomodoroSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PomodoroSessionDao {

    @Insert
    suspend fun insert(entity: PomodoroSessionEntity): Long

    @Update
    suspend fun update(entity: PomodoroSessionEntity)

    @Query(
        "SELECT * FROM pomodoro_sessions " +
            "WHERE startedAtEpochMillis >= :startOfDay " +
            "AND startedAtEpochMillis < :endOfDay " +
            "ORDER BY startedAtEpochMillis DESC"
    )
    fun observeForRange(startOfDay: Long, endOfDay: Long): Flow<List<PomodoroSessionEntity>>

    @Query("SELECT COUNT(*) FROM pomodoro_sessions WHERE type = :typeName AND completed = 1")
    suspend fun countCompletedByType(typeName: String): Int
}
