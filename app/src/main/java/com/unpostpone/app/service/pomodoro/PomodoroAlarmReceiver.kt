package com.unpostpone.app.service.pomodoro

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.content.getSystemService
import com.unpostpone.app.domain.model.PomodoroSessionType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PomodoroAlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var notificationHelper: PomodoroNotificationHelper
    @Inject lateinit var ringtonePlayer: PomodoroRingtonePlayer
    @Inject lateinit var eventBus: PomodoroEventBus

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != PomodoroAlarmScheduler.ACTION_SESSION_END) return
        val ordinal = intent.getIntExtra(PomodoroAlarmScheduler.EXTRA_SESSION_TYPE, -1)
        if (ordinal !in PomodoroSessionType.values().indices) return
        val sessionType = PomodoroSessionType.values()[ordinal]

        val pending = goAsync()
        scope.launch {
            try {
                notificationHelper.ensureChannel()
                postCompletionNotification(context, sessionType)
                vibrate(context)
                ringtonePlayer.play()
                eventBus.emit(PomodoroAlarmEvent.SessionComplete(sessionType))
            } finally {
                pending.finish()
            }
        }
    }

    private fun postCompletionNotification(context: Context, sessionType: PomodoroSessionType) {
        val appContext = context.applicationContext
        val notification = notificationHelper.buildSessionCompleteNotification(sessionType)
        val manager = appContext.getSystemService<android.app.NotificationManager>() ?: return
        manager.notify(PomodoroNotificationHelper.POMODORO_NOTIFICATION_ID, notification)
    }

    private fun vibrate(context: Context) {
        val appContext = context.applicationContext
        val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            appContext.getSystemService<VibratorManager>()?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            appContext.getSystemService<Vibrator>()
        }
        vibrator ?: return
        val pattern = longArrayOf(0L, 400L, 200L, 400L)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, -1)
        }
    }
}
