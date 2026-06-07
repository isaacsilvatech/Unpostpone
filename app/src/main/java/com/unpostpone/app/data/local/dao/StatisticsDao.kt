package com.unpostpone.app.data.local.dao

import androidx.room.*
import com.unpostpone.app.data.local.entity.StatisticsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StatisticsDao {

    @Query("SELECT * FROM statistics WHERE date = :date")
    fun getStatisticsByDate(date: String): Flow<StatisticsEntity?>

    @Query("SELECT * FROM statistics ORDER BY date DESC LIMIT 30")
    fun getRecentStatistics(): Flow<List<StatisticsEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStatistics(stats: StatisticsEntity)

    @Query("UPDATE statistics SET blockCount = blockCount + 1 WHERE date = :date")
    suspend fun incrementBlockCount(date: String)

    @Query("UPDATE statistics SET unlockAttempts = unlockAttempts + 1 WHERE date = :date")
    suspend fun incrementUnlockAttempts(date: String)

    @Query("UPDATE statistics SET focusedMinutes = focusedMinutes + :minutes WHERE date = :date")
    suspend fun addFocusedMinutes(date: String, minutes: Int)
}
