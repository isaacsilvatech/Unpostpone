package com.unpostpone.app.service.pomodoro

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.unpostpone.app.MainActivity

class PomodoroActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_OPEN -> {
                val openIntent = Intent(context, MainActivity::class.java).apply {
                    addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                    )
                    putExtra(PomodoroNotificationHelper.EXTRA_OPEN_POMODORO, true)
                }
                context.startActivity(openIntent)
            }
            ACTION_PAUSE, ACTION_RESUME, ACTION_ADD_MINUTE, ACTION_STOP -> {
                val serviceIntent = Intent(context, PomodoroTimerService::class.java).apply {
                    action = intent.action
                }
                context.startService(serviceIntent)
            }
        }
    }

    companion object {
        const val ACTION_PAUSE = PomodoroTimerService.ACTION_PAUSE
        const val ACTION_RESUME = PomodoroTimerService.ACTION_RESUME
        const val ACTION_ADD_MINUTE = PomodoroTimerService.ACTION_ADD_MINUTE
        const val ACTION_OPEN = "com.unpostpone.app.action.POMODORO_OPEN"
        const val ACTION_STOP = PomodoroTimerService.ACTION_STOP

        fun pendingIntentFor(
            context: Context,
            action: String,
            requestCode: Int,
        ): PendingIntent {
            val intent = Intent(context, PomodoroActionReceiver::class.java).apply {
                this.action = action
            }
            return PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
        }
    }
}
