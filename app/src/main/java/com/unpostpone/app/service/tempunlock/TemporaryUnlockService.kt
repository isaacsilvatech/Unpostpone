package com.unpostpone.app.service.tempunlock

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.unpostpone.app.MainActivity
import com.unpostpone.app.R
import com.unpostpone.app.core.tempunlock.TemporaryUnlockManager
import com.unpostpone.app.service.accessibility.UnpostponeAccessibilityService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TemporaryUnlockService : Service() {

    @Inject lateinit var manager: TemporaryUnlockManager

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var currentPackage: String? = null
    private var currentDisplayName: String? = null
    private var tickerJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> handleStart(intent)
            ACTION_STOP -> handleStop()
        }
        return START_NOT_STICKY
    }

    private fun handleStart(intent: Intent) {
        val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: return stopSelf()
        val displayName = intent.getStringExtra(EXTRA_DISPLAY_NAME) ?: packageName
        val duration = intent.getIntExtra(
            EXTRA_DURATION_SECONDS,
            TemporaryUnlockManager.DEFAULT_DURATION_SECONDS,
        )

        currentPackage = packageName
        currentDisplayName = displayName

        manager.start(packageName, displayName, duration)

        val initial = buildNotification(displayName, duration)
        startForegroundCompat(initial)

        tickerJob?.cancel()
        tickerJob = scope.launch {
            while (true) {
                val remaining = manager.state.value.remainingSeconds
                if (remaining <= 0) {
                    onUnlockExpired(packageName)
                    return@launch
                }
                refreshNotification(remaining)
                delay(1_000L)
            }
        }
    }

    private fun handleStop() {
        tickerJob?.cancel()
        tickerJob = null
        manager.clear()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        stopSelf()
    }

    private fun onUnlockExpired(packageName: String) {
        manager.clear()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        stopSelf()
    }

    private fun startForegroundCompat(notification: Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun refreshNotification(remainingSeconds: Int) {
        val name = currentDisplayName ?: currentPackage ?: return
        val notification = buildNotification(name, remainingSeconds)
        getSystemService(NotificationManager::class.java)
            .notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(displayName: String, remainingSeconds: Int): Notification {
        val minutes = remainingSeconds / 60
        val seconds = remainingSeconds % 60
        val timeStr = "%d:%02d".format(minutes, seconds)

        val contentIntent = currentPackage?.let { pkg ->
            val openIntent = Intent().apply {
                component = ComponentName(this@TemporaryUnlockService, MainActivity::class.java)
                addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
                )
                putExtra(UnpostponeAccessibilityService.EXTRA_BLOCKED_PACKAGE, pkg)
                putExtra(MainActivity.EXTRA_FROM_UNLOCK_NOTIFICATION, true)
            }
            PendingIntent.getActivity(
                this,
                0,
                openIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.blocker_unlock_notification_title, displayName))
            .setContentText(getString(R.string.blocker_unlock_notification_body, timeStr))
            .setSmallIcon(R.drawable.ic_lock_open)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.blocker_unlock_channel_name),
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = getString(R.string.blocker_unlock_channel_description)
            setShowBadge(false)
        }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    companion object {
        const val CHANNEL_ID = "unpostpone_temporary_unlock"
        const val NOTIFICATION_ID = 1002
        const val ACTION_START = "ACTION_START_TEMP_UNLOCK"
        const val ACTION_STOP = "ACTION_STOP_TEMP_UNLOCK"
        const val EXTRA_PACKAGE_NAME = "extra_temp_unlock_package"
        const val EXTRA_DISPLAY_NAME = "extra_temp_unlock_display_name"
        const val EXTRA_DURATION_SECONDS = "extra_temp_unlock_duration_seconds"
    }
}
