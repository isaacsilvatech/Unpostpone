package com.unpostpone.app.di

import com.unpostpone.app.core.locale.LanguageManager
import com.unpostpone.app.data.locale.LanguageManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocaleModule {

    @Binds
    @Singleton
    abstract fun bindLanguageManager(
        impl: LanguageManagerImpl,
    ): LanguageManager
}
