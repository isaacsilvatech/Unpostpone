package com.unpostpone.app.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Reserved for future app-level bindings: analytics, DataStore, remote config, etc. */
@Module
@InstallIn(SingletonComponent::class)
object AppModule
