package com.unpostpone.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.unpostpone.app.data.local.dao.BlockedAppDao
import com.unpostpone.app.data.local.dao.GoalDao
import com.unpostpone.app.data.local.dao.PomodoroSessionDao
import com.unpostpone.app.data.local.dao.StatisticsDao
import com.unpostpone.app.data.local.entity.BlockedAppEntity
import com.unpostpone.app.data.local.entity.GoalEntity
import com.unpostpone.app.data.local.entity.PomodoroSessionEntity
import com.unpostpone.app.data.local.entity.StatisticsEntity

@Database(
    entities = [
        GoalEntity::class,
        BlockedAppEntity::class,
        StatisticsEntity::class,
        PomodoroSessionEntity::class,
    ],
    version = 3,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun goalDao(): GoalDao
    abstract fun blockedAppDao(): BlockedAppDao
    abstract fun statisticsDao(): StatisticsDao
    abstract fun pomodoroSessionDao(): PomodoroSessionDao

    companion object {
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `pomodoro_sessions` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`type` TEXT NOT NULL, " +
                        "`presetName` TEXT NOT NULL, " +
                        "`focusMinutes` INTEGER NOT NULL, " +
                        "`shortBreakMinutes` INTEGER NOT NULL, " +
                        "`longBreakMinutes` INTEGER NOT NULL, " +
                        "`cyclesBeforeLongBreak` INTEGER NOT NULL, " +
                        "`plannedDurationMillis` INTEGER NOT NULL, " +
                        "`startedAtEpochMillis` INTEGER NOT NULL, " +
                        "`endedAtEpochMillis` INTEGER, " +
                        "`completed` INTEGER NOT NULL" +
                        ")"
                )
            }
        }

        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `pomodoro_sessions_new` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`type` TEXT NOT NULL, " +
                        "`presetName` TEXT NOT NULL, " +
                        "`focusMinutes` INTEGER NOT NULL, " +
                        "`breakMinutes` INTEGER NOT NULL, " +
                        "`plannedDurationMillis` INTEGER NOT NULL, " +
                        "`startedAtEpochMillis` INTEGER NOT NULL, " +
                        "`endedAtEpochMillis` INTEGER, " +
                        "`completed` INTEGER NOT NULL" +
                        ")"
                )
                db.execSQL(
                    "INSERT INTO `pomodoro_sessions_new` (" +
                        "`id`, `type`, `presetName`, `focusMinutes`, " +
                        "`breakMinutes`, " +
                        "`plannedDurationMillis`, `startedAtEpochMillis`, " +
                        "`endedAtEpochMillis`, `completed`" +
                        ") SELECT " +
                        "`id`, `type`, `presetName`, `focusMinutes`, " +
                        "`shortBreakMinutes`, " +
                        "`plannedDurationMillis`, `startedAtEpochMillis`, " +
                        "`endedAtEpochMillis`, `completed` " +
                        "FROM `pomodoro_sessions`"
                )
                db.execSQL("DROP TABLE `pomodoro_sessions`")
                db.execSQL("ALTER TABLE `pomodoro_sessions_new` RENAME TO `pomodoro_sessions`")
            }
        }
    }
}
