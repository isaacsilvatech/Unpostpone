package com.unpostpone.app.service.pomodoro

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.unpostpone.app.MainActivity
import com.unpostpone.app.R
import com.unpostpone.app.domain.model.PomodoroSessionType
import com.unpostpone.app.presentation.pomodoro.PomodoroSessionCompleteActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PomodoroTimerService : Service() {

    @Inject lateinit var engine: PomodoroTimerEngine
    @Inject lateinit var notificationHelper: PomodoroNotificationHelper
    @Inject lateinit var alarmScheduler: PomodoroAlarmScheduler
    @Inject lateinit var ringtonePlayer: PomodoroRingtonePlayer
    @Inject lateinit var vibrator: PomodoroVibrator
    @Inject lateinit var sessionEndSignal: PomodoroSessionEndSignal

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var notifJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        notifJob = scope.launch {
            engine.state
                .combine(MutableStateFlow(Unit)) { state, _ -> state }
                .collect { state -> postNotification(state) }
        }
        scope.launch {
            engine.state.collect { state ->
                handleEngineState(state)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val duration = intent.getLongExtra(EXTRA_DURATION_MILLIS, 0L)
                val typeOrdinal = intent.getIntExtra(EXTRA_SESSION_TYPE, 0)
                val type = PomodoroSessionType.entries[typeOrdinal]
                notificationHelper.ensureChannel()
                val s = engine.state.value.copy(
                    status = PomodoroTimerEngine.Status.RUNNING,
                    sessionType = type,
                    totalMillis = duration,
                    remainingMillis = duration,
                )
                engine.start(duration, type)
                sessionEndSignal.reset()
                startForegroundCompat(s)
                alarmScheduler.scheduleSessionEnd(duration, type)
            }
            ACTION_PAUSE -> engine.pause()
            ACTION_RESUME -> {
                engine.resume()
                alarmScheduler.scheduleSessionEnd(
                    engine.state.value.remainingMillis.coerceAtLeast(0L),
                    engine.state.value.sessionType,
                )
            }
            ACTION_ADD_MINUTE -> {
                engine.addMinute()
                val s = engine.state.value
                if (s.status == PomodoroTimerEngine.Status.RUNNING) {
                    alarmScheduler.cancel()
                    val remaining = s.remainingMillis.coerceAtLeast(0L)
                    if (remaining > 0L) {
                        alarmScheduler.scheduleSessionEnd(remaining, s.sessionType)
                    }
                    if (s.remainingMillis > 0L) {
                        sessionEndSignal.reset()
                    }
                }
            }
            ACTION_STOP -> stopService()
        }
        return START_STICKY
    }

    private fun handleEngineState(state: PomodoroTimerEngine.State) {
        if (state.status != PomodoroTimerEngine.Status.RUNNING) return
        if (state.remainingMillis >= 0L) return
        if (!sessionEndSignal.tryFire()) return
        vibrator.vibrate()
        ringtonePlayer.stop()
        ringtonePlayer.play()
        launchSessionCompleteActivity()
    }

    private fun launchSessionCompleteActivity() {
        val intent = Intent(this, PomodoroSessionCompleteActivity::class.java).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
            )
        }
        runCatching { startActivity(intent) }
    }

    private fun stopService() {
        engine.stop()
        alarmScheduler.cancel()
        ringtonePlayer.stop()
        getSystemService(NotificationManager::class.java)
            ?.cancel(PomodoroNotificationHelper.POMODORO_OVERTIME_NOTIFICATION_ID)
        stopForegroundCompat()
        stopSelf()
    }

    private fun postNotification(state: PomodoroTimerEngine.State) {
        val manager = getSystemService(NotificationManager::class.java) ?: return
        val runningId = PomodoroNotificationHelper.POMODORO_RUNNING_NOTIFICATION_ID
        if (state.status == PomodoroTimerEngine.Status.IDLE && state.remainingMillis == 0L) {
            manager.cancel(runningId)
            return
        }
        if (state.remainingMillis < 0L) {
            manager.cancel(runningId)
            return
        }
        val notification = buildRunningNotification(state)
        manager.notify(runningId, notification)
    }

    private fun startForegroundCompat(state: PomodoroTimerEngine.State) {
        val notification = buildRunningNotification(state)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                PomodoroNotificationHelper.POMODORO_RUNNING_NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
            )
        } else {
            startForeground(PomodoroNotificationHelper.POMODORO_RUNNING_NOTIFICATION_ID, notification)
        }
    }

    private fun stopForegroundCompat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
    }

    private fun buildRunningNotification(s: PomodoroTimerEngine.State): Notification {
        val formattedTime = formatRemainingMillis(s.remainingMillis)
        val title = when (s.sessionType) {
            PomodoroSessionType.FOCUS -> getString(R.string.pomodoro_notification_ongoing_focus, formattedTime)
            PomodoroSessionType.BREAK -> getString(R.string.pomodoro_notification_ongoing_break, formattedTime)
        }

        val openIntent = Intent(this, MainActivity::class.java).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
            )
            putExtra(PomodoroNotificationHelper.EXTRA_OPEN_POMODORO, true)
        }
        val openPending = PendingIntent.getActivity(
            this,
            OPEN_REQUEST_CODE,
            openIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

        val builder = NotificationCompat.Builder(this, PomodoroNotificationHelper.POMODORO_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(getString(R.string.pomodoro_notification_ongoing_subtitle))
            .setSmallIcon(R.drawable.ic_pomodoro)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(openPending)

        when (s.status) {
            PomodoroTimerEngine.Status.RUNNING -> {
                val pausePending = PomodoroActionReceiver.pendingIntentFor(
                    this, ACTION_PAUSE, PAUSE_REQUEST_CODE,
                )
                builder.addAction(
                    R.drawable.ic_pomodoro,
                    getString(R.string.pomodoro_notification_pause),
                    pausePending,
                )
            }
            PomodoroTimerEngine.Status.PAUSED -> {
                val resumePending = PomodoroActionReceiver.pendingIntentFor(
                    this, ACTION_RESUME, RESUME_REQUEST_CODE,
                )
                builder.addAction(
                    R.drawable.ic_pomodoro,
                    getString(R.string.pomodoro_notification_resume),
                    resumePending,
                )
            }
            PomodoroTimerEngine.Status.IDLE -> Unit
        }

        val addMinutePending = PomodoroActionReceiver.pendingIntentFor(
            this, ACTION_ADD_MINUTE, ADD_MINUTE_REQUEST_CODE,
        )
        builder.addAction(
            R.drawable.ic_pomodoro,
            getString(R.string.pomodoro_notification_add_minute),
            addMinutePending,
        )

        return builder.build()
    }

    override fun onDestroy() {
        notifJob?.cancel()
        scope.coroutineContext[Job]?.cancel()
        super.onDestroy()
    }

    companion object {
        const val ACTION_START = "com.unpostpone.app.action.POMODORO_SERVICE_START"
        const val ACTION_PAUSE = "com.unpostpone.app.action.POMODORO_SERVICE_PAUSE"
        const val ACTION_RESUME = "com.unpostpone.app.action.POMODORO_SERVICE_RESUME"
        const val ACTION_ADD_MINUTE = "com.unpostpone.app.action.POMODORO_SERVICE_ADD_MINUTE"
        const val ACTION_STOP = "com.unpostpone.app.action.POMODORO_SERVICE_STOP"

        const val EXTRA_DURATION_MILLIS = "extra_pomodoro_duration_millis"
        const val EXTRA_SESSION_TYPE = "extra_pomodoro_session_type"

        private const val OPEN_REQUEST_CODE = 5001
        private const val PAUSE_REQUEST_CODE = 5002
        private const val RESUME_REQUEST_CODE = 5003
        private const val ADD_MINUTE_REQUEST_CODE = 5004
    }
}
