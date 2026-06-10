package com.unpostpone.app.service.pomodoro

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.unpostpone.app.domain.model.PomodoroSessionType

open class PomodoroAlarmScheduler(
    private val appContext: Context,
    private val notificationHelper: PomodoroNotificationHelper,
) {

    private val alarmManager: AlarmManager =
        appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    open fun scheduleSessionEnd(
        plannedDurationMillis: Long,
        sessionType: PomodoroSessionType,
    ) {
        notificationHelper.ensureChannel()
        val triggerAtMillis = System.currentTimeMillis() + plannedDurationMillis
        val pendingIntent = buildPendingIntent(sessionType)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            return
        }
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent,
        )
    }

    open fun cancel() {
        PomodoroSessionType.values().forEach { sessionType ->
            alarmManager.cancel(buildPendingIntent(sessionType))
        }
    }

    private fun buildPendingIntent(sessionType: PomodoroSessionType): PendingIntent {
        val intent = Intent(appContext, PomodoroAlarmReceiver::class.java).apply {
            action = ACTION_SESSION_END
            putExtra(EXTRA_SESSION_TYPE, sessionType.ordinal)
        }
        return PendingIntent.getBroadcast(
            appContext,
            sessionType.ordinal,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    companion object {
        const val ACTION_SESSION_END = "com.unpostpone.app.action.POMODORO_SESSION_END"
        const val EXTRA_SESSION_TYPE = "extra_pomodoro_session_type"
    }
}
