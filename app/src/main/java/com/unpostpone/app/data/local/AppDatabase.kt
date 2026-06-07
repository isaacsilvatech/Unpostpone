package com.unpostpone.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.unpostpone.app.data.local.dao.BlockedAppDao
import com.unpostpone.app.data.local.dao.GoalDao
import com.unpostpone.app.data.local.dao.StatisticsDao
import com.unpostpone.app.data.local.entity.BlockedAppEntity
import com.unpostpone.app.data.local.entity.GoalEntity
import com.unpostpone.app.data.local.entity.StatisticsEntity

@Database(
    entities = [GoalEntity::class, BlockedAppEntity::class, StatisticsEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun goalDao(): GoalDao
    abstract fun blockedAppDao(): BlockedAppDao
    abstract fun statisticsDao(): StatisticsDao
}
