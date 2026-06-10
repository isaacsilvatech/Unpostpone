package com.unpostpone.app.di

import com.unpostpone.app.data.repository.BlockedAppRepositoryImpl
import com.unpostpone.app.data.repository.BlockingPreferencesImpl
import com.unpostpone.app.data.repository.GoalRepositoryImpl
import com.unpostpone.app.data.repository.PomodoroSessionRepositoryImpl
import com.unpostpone.app.data.repository.StatisticsRepositoryImpl
import com.unpostpone.app.domain.repository.BlockedAppRepository
import com.unpostpone.app.domain.repository.BlockingPreferences
import com.unpostpone.app.domain.repository.GoalRepository
import com.unpostpone.app.domain.repository.PomodoroSessionRepository
import com.unpostpone.app.domain.repository.StatisticsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindGoalRepository(impl: GoalRepositoryImpl): GoalRepository

    @Binds @Singleton
    abstract fun bindBlockedAppRepository(impl: BlockedAppRepositoryImpl): BlockedAppRepository

    @Binds @Singleton
    abstract fun bindStatisticsRepository(impl: StatisticsRepositoryImpl): StatisticsRepository

    @Binds @Singleton
    abstract fun bindPomodoroSessionRepository(impl: PomodoroSessionRepositoryImpl): PomodoroSessionRepository

    @Binds @Singleton
    abstract fun bindBlockingPreferences(impl: BlockingPreferencesImpl): BlockingPreferences
}
