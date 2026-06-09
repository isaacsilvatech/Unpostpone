package com.unpostpone.app.service.monitoring

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.unpostpone.app.domain.usecase.blockedapp.ObserveBlockingEnabledUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlockingController     @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val observeBlockingEnabled: ObserveBlockingEnabledUseCase,
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var job: Job? = null

    fun start() {
        if (job?.isActive == true) return
        job = scope.launch {
            observeBlockingEnabled()
                .distinctUntilChanged()
                .collect { enabled ->
                    if (enabled) sendStart() else sendStop()
                }
        }
    }

    private fun sendStart() {
        val intent = Intent(context, AppMonitoringService::class.java)
            .setAction(AppMonitoringService.ACTION_START)
        ContextCompat.startForegroundService(context, intent)
    }

    private fun sendStop() {
        val intent = Intent(context, AppMonitoringService::class.java)
            .setAction(AppMonitoringService.ACTION_STOP)
        context.stopService(intent)
    }
}
