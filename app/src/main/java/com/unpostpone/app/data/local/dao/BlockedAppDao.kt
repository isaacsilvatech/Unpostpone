package com.unpostpone.app.data.local.dao

import androidx.room.*
import com.unpostpone.app.data.local.entity.BlockedAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedAppDao {

    @Query("SELECT * FROM blocked_apps ORDER BY displayName ASC")
    fun getAllBlockedApps(): Flow<List<BlockedAppEntity>>

    @Query("SELECT * FROM blocked_apps WHERE isEnabled = 1")
    fun getEnabledBlockedApps(): Flow<List<BlockedAppEntity>>

    @Query("SELECT COUNT(*) > 0 FROM blocked_apps WHERE packageName = :packageName AND isEnabled = 1")
    suspend fun isAppBlocked(packageName: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedApp(app: BlockedAppEntity)

    @Delete
    suspend fun deleteBlockedApp(app: BlockedAppEntity)

    @Query("UPDATE blocked_apps SET isEnabled = :isEnabled WHERE packageName = :packageName")
    suspend fun setAppEnabled(packageName: String, isEnabled: Boolean)
}
