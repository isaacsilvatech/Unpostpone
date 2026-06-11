package com.unpostpone.app

import android.app.Application
import com.unpostpone.app.core.util.AccessibilityServiceUtils
import com.unpostpone.app.domain.repository.BlockingPreferences
import com.unpostpone.app.domain.usecase.blockedapp.EnsureDefaultBlockedAppsSeededUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class UnpostponeApplication : Application() {

    @Inject lateinit var blockingPreferences: BlockingPreferences
    @Inject lateinit var ensureDefaultBlockedAppsSeeded: EnsureDefaultBlockedAppsSeededUseCase

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        appScope.launch {
            if (AccessibilityServiceUtils.isUnpostponeEnabled(this@UnpostponeApplication) &&
                !blockingPreferences.isBlockingEnabled.first()
            ) {
                blockingPreferences.setBlockingEnabled(true)
            }
            ensureDefaultBlockedAppsSeeded()
        }
    }
}
