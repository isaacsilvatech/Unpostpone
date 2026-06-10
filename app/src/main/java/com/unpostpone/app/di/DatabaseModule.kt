package com.unpostpone.app.di

import android.content.Context
import androidx.room.Room
import com.unpostpone.app.core.util.Constants
import com.unpostpone.app.data.local.AppDatabase
import com.unpostpone.app.data.local.dao.BlockedAppDao
import com.unpostpone.app.data.local.dao.GoalDao
import com.unpostpone.app.data.local.dao.PomodoroSessionDao
import com.unpostpone.app.data.local.dao.StatisticsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, Constants.DATABASE_NAME)
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()

    @Provides
    fun provideGoalDao(db: AppDatabase): GoalDao = db.goalDao()

    @Provides
    fun provideBlockedAppDao(db: AppDatabase): BlockedAppDao = db.blockedAppDao()

    @Provides
    fun provideStatisticsDao(db: AppDatabase): StatisticsDao = db.statisticsDao()

    @Provides
    fun providePomodoroSessionDao(db: AppDatabase): PomodoroSessionDao = db.pomodoroSessionDao()
}
