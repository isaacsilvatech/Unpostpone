package com.unpostpone.app.di

import android.content.Context
import android.content.SharedPreferences
import com.unpostpone.app.data.repository.OnboardingPreferencesImpl
import com.unpostpone.app.data.repository.ThemePreferencesImpl
import com.unpostpone.app.domain.repository.OnboardingPreferences
import com.unpostpone.app.domain.repository.ThemePreferences
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val PREFS_FILE = "unpostpone.prefs"

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context,
    ): SharedPreferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModuleBinds {

    @Binds
    @Singleton
    abstract fun bindOnboardingPreferences(
        impl: OnboardingPreferencesImpl,
    ): OnboardingPreferences

    @Binds
    @Singleton
    abstract fun bindThemePreferences(
        impl: ThemePreferencesImpl,
    ): ThemePreferences
}
