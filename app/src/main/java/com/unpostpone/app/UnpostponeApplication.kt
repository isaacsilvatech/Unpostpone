package com.unpostpone.app

import android.app.Application
import com.unpostpone.app.domain.usecase.blockedapp.EnsureDefaultBlockedAppsSeededUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class UnpostponeApplication : Application() {

    @Inject lateinit var ensureDefaultBlockedAppsSeeded: EnsureDefaultBlockedAppsSeededUseCase

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        appScope.launch { ensureDefaultBlockedAppsSeeded() }
    }
}
