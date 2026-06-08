package com.unpostpone.app

import android.app.Application
import com.unpostpone.app.service.monitoring.BlockingController
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class UnpostponeApplication : Application() {

    /**
     * Bridges the persisted blocking flag to the [com.unpostpone.app.service.monitoring.AppMonitoringService]
     * foreground service. Started eagerly in [onCreate] so the service
     * mirrors whatever the last persisted value was, even on a cold start
     * where no UI has been shown yet.
     */
    @Inject lateinit var blockingController: BlockingController

    override fun onCreate() {
        super.onCreate()
        blockingController.start()
    }
}
