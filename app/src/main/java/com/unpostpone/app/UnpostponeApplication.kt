package com.unpostpone.app

import android.app.Application
import com.unpostpone.app.service.monitoring.BlockingController
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class UnpostponeApplication : Application() {

    @Inject lateinit var blockingController: BlockingController

    override fun onCreate() {
        super.onCreate()
        blockingController.start()
    }
}
