package com.unpostpone.app.service.pomodoro

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.getSystemService
import com.unpostpone.app.domain.model.PomodoroSessionType
import com.unpostpone.app.presentation.pomodoro.PomodoroSessionCompleteActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PomodoroAlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var engine: PomodoroTimerEngine
    @Inject lateinit var notificationHelper: PomodoroNotificationHelper
    @Inject lateinit var ringtonePlayer: PomodoroRingtonePlayer
    @Inject lateinit var vibrator: PomodoroVibrator
    @Inject lateinit var sessionEndSignal: PomodoroSessionEndSignal

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != PomodoroAlarmScheduler.ACTION_SESSION_END) return
        val ordinal = intent.getIntExtra(PomodoroAlarmScheduler.EXTRA_SESSION_TYPE, -1)
        if (ordinal !in PomodoroSessionType.values().indices) return
        val sessionType = PomodoroSessionType.values()[ordinal]

        val current = engine.state.value
        if (current.status != PomodoroTimerEngine.Status.RUNNING) return
        if (current.sessionType != sessionType) return
        if (!sessionEndSignal.tryFire()) return

        val pending = goAsync()
        scope.launch {
            try {
                notificationHelper.ensureChannel()
                postOvertimeHeadsUp(context, current)
                vibrator.vibrate()
                ringtonePlayer.stop()
                ringtonePlayer.play()
                launchSessionCompleteActivity(context)
            } finally {
                pending.finish()
            }
        }
    }

    private fun postOvertimeHeadsUp(
        context: Context,
        state: PomodoroTimerEngine.State,
    ) {
        val appContext = context.applicationContext
        val notification = notificationHelper.buildOvertimeNotification(state)
        val manager = appContext.getSystemService<android.app.NotificationManager>() ?: return
        manager.notify(
            PomodoroNotificationHelper.POMODORO_OVERTIME_NOTIFICATION_ID,
            notification,
        )
    }

    private fun launchSessionCompleteActivity(context: Context) {
        val appContext = context.applicationContext
        val intent = Intent(appContext, PomodoroSessionCompleteActivity::class.java).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
            )
        }
        runCatching { appContext.startActivity(intent) }
    }
}
