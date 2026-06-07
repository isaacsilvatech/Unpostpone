package com.unpostpone.app.service.monitoring

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.unpostpone.app.domain.usecase.statistics.AddFocusTimeUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AppMonitoringService : Service() {

    @Inject lateinit var addFocusTimeUseCase: AddFocusTimeUseCase

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var focusStartTime = 0L

    companion object {
        const val CHANNEL_ID = "unpostpone_monitoring"
        const val NOTIFICATION_ID = 1001
        const val ACTION_START = "ACTION_START_MONITORING"
        const val ACTION_STOP = "ACTION_STOP_MONITORING"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startMonitoring()
            ACTION_STOP  -> stopMonitoring()
        }
        return START_STICKY
    }

    private fun startMonitoring() {
        focusStartTime = System.currentTimeMillis()
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Unpostpone ativo")
            .setContentText("Monitorando suas metas de foco")
            .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun stopMonitoring() {
        if (focusStartTime > 0) {
            val elapsedMinutes = ((System.currentTimeMillis() - focusStartTime) / 60_000).toInt()
            if (elapsedMinutes > 0) {
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                serviceScope.launch { addFocusTimeUseCase(today, elapsedMinutes) }
            }
        }
        stopSelf()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Monitoramento Unpostpone",
            NotificationManager.IMPORTANCE_LOW
        ).apply { description = "Mantém o monitoramento de foco ativo" }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
