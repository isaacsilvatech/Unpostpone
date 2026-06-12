package com.unpostpone.app.di

import android.content.Context
import com.unpostpone.app.service.pomodoro.PomodoroAlarmScheduler
import com.unpostpone.app.service.pomodoro.PomodoroNotificationHelper
import com.unpostpone.app.service.pomodoro.PomodoroRingtonePlayer
import com.unpostpone.app.service.pomodoro.PomodoroVibrator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PomodoroModule {

    @Provides
    @Singleton
    fun providePomodoroNotificationHelper(
        @ApplicationContext context: Context,
    ): PomodoroNotificationHelper = PomodoroNotificationHelper(context)

    @Provides
    @Singleton
    fun providePomodoroAlarmScheduler(
        @ApplicationContext context: Context,
        notificationHelper: PomodoroNotificationHelper,
    ): PomodoroAlarmScheduler = PomodoroAlarmScheduler(context, notificationHelper)

    @Provides
    @Singleton
    fun providePomodoroRingtonePlayer(
        @ApplicationContext context: Context,
    ): PomodoroRingtonePlayer = PomodoroRingtonePlayer(context)

    @Provides
    @Singleton
    fun providePomodoroVibrator(
        @ApplicationContext context: Context,
    ): PomodoroVibrator = PomodoroVibrator(context)
}
