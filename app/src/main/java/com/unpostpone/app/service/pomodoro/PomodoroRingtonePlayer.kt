package com.unpostpone.app.service.pomodoro

import android.content.Context
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.Looper

class PomodoroRingtonePlayer(
    private val appContext: Context,
) {

    private val mainHandler = Handler(Looper.getMainLooper())
    private var currentRingtone: Ringtone? = null
    private var loopRunnable: Runnable? = null

    fun play() {
        if (currentRingtone?.isPlaying == true) return

        val ringtone = RingtoneManager.getRingtone(
            appContext,
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
        ) ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ringtone.audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        }
        if (!ringtone.isPlaying) {
            ringtone.play()
        }
        currentRingtone = ringtone

        val runnable = object : Runnable {
            override fun run() {
                val active = currentRingtone ?: return
                if (active.isPlaying) {
                    mainHandler.postDelayed(this, LOOP_INTERVAL_MILLIS)
                } else {
                    active.play()
                    mainHandler.postDelayed(this, LOOP_INTERVAL_MILLIS)
                }
            }
        }
        loopRunnable = runnable
        mainHandler.postDelayed(runnable, LOOP_INTERVAL_MILLIS)
    }

    fun stop() {
        loopRunnable?.let { mainHandler.removeCallbacks(it) }
        loopRunnable = null
        currentRingtone?.let { ringtone ->
            if (ringtone.isPlaying) {
                ringtone.stop()
            }
        }
        currentRingtone = null
    }

    private companion object {
        const val LOOP_INTERVAL_MILLIS = 2_000L
    }
}
