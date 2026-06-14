package com.unpostpone.app.service.pomodoro

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.unpostpone.app.domain.model.PomodoroSessionType
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PomodoroAlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var engine: PomodoroTimerEngine

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != PomodoroAlarmScheduler.ACTION_SESSION_END) return
        val ordinal = intent.getIntExtra(PomodoroAlarmScheduler.EXTRA_SESSION_TYPE, -1)
        if (ordinal !in PomodoroSessionType.values().indices) return
        val sessionType = PomodoroSessionType.values()[ordinal]

        val current = engine.state.value
        if (current.status != PomodoroTimerEngine.Status.RUNNING) return
        if (current.sessionType != sessionType) return

        engine.syncFromWallClock()

        val serviceIntent = Intent(context, PomodoroTimerService::class.java).apply {
            action = PomodoroTimerService.ACTION_CHECK_OVERTIME
        }
        runCatching {
            context.startForegroundService(serviceIntent)
        }
    }
}
