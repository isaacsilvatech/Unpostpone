package com.unpostpone.app.service.pomodoro

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.unpostpone.app.MainActivity
import com.unpostpone.app.R
import com.unpostpone.app.domain.model.PomodoroSessionType

class PomodoroNotificationHelper(
    private val appContext: Context,
) {

    fun ensureChannel() {
        val manager = appContext.getSystemService(NotificationManager::class.java) ?: return
        if (manager.getNotificationChannel(POMODORO_CHANNEL_ID) != null) return
        val channel = NotificationChannel(
            POMODORO_CHANNEL_ID,
            appContext.getString(R.string.pomodoro_channel_name),
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = appContext.getString(R.string.pomodoro_channel_description)
            enableVibration(true)
            setShowBadge(true)
        }
        manager.createNotificationChannel(channel)
    }

    fun buildSessionCompleteNotification(
        sessionType: PomodoroSessionType,
    ): Notification {
        val (title, body) = sessionCompleteContent(sessionType)
        val openIntent = Intent().apply {
            component = ComponentName(appContext, MainActivity::class.java)
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
            )
            putExtra(EXTRA_OPEN_POMODORO, true)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            appContext,
            POMODORO_OPEN_REQUEST_CODE,
            openIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val nextAction = NotificationCompat.Action.Builder(
            R.drawable.ic_pomodoro,
            appContext.getString(R.string.pomodoro_notification_action_next),
            contentPendingIntent,
        ).build()

        return NotificationCompat.Builder(appContext, POMODORO_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_pomodoro)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(contentPendingIntent)
            .addAction(nextAction)
            .build()
    }

    fun buildOngoingNotification(
        sessionType: PomodoroSessionType,
        remainingMillis: Long,
    ): Notification {
        val title = appContext.getString(R.string.pomodoro_notification_ongoing_title)
        val body = ongoingBody(sessionType, remainingMillis)
        val openIntent = Intent().apply {
            component = ComponentName(appContext, MainActivity::class.java)
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
            )
            putExtra(EXTRA_OPEN_POMODORO, true)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            appContext,
            POMODORO_OPEN_REQUEST_CODE,
            openIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

        return NotificationCompat.Builder(appContext, POMODORO_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_pomodoro)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(contentPendingIntent)
            .build()
    }

    private fun sessionCompleteContent(sessionType: PomodoroSessionType): Pair<String, String> {
        val title = appContext.getString(R.string.pomodoro_notification_complete_title)
        val bodyRes = when (sessionType) {
            PomodoroSessionType.FOCUS -> R.string.pomodoro_notification_complete_focus_body
            PomodoroSessionType.SHORT_BREAK,
            PomodoroSessionType.LONG_BREAK -> R.string.pomodoro_notification_complete_break_body
        }
        return title to appContext.getString(bodyRes)
    }

    private fun ongoingBody(sessionType: PomodoroSessionType, remainingMillis: Long): String {
        val baseRes = when (sessionType) {
            PomodoroSessionType.FOCUS -> R.string.pomodoro_notification_ongoing_body_focus
            PomodoroSessionType.SHORT_BREAK -> R.string.pomodoro_notification_ongoing_body_short_break
            PomodoroSessionType.LONG_BREAK -> R.string.pomodoro_notification_ongoing_body_long_break
        }
        val remainingMinutes = (remainingMillis / 60_000L).coerceAtLeast(0L)
        return "${appContext.getString(baseRes)} · ${remainingMinutes}m"
    }

    companion object {
        const val POMODORO_CHANNEL_ID = "pomodoro_channel"
        const val POMODORO_NOTIFICATION_ID = 1001
        const val POMODORO_RUNNING_NOTIFICATION_ID = 1002

        const val EXTRA_OPEN_POMODORO = "extra_open_pomodoro"

        private const val POMODORO_OPEN_REQUEST_CODE = 4001
    }
}
