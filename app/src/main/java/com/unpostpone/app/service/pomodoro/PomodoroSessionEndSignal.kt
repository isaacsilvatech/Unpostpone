package com.unpostpone.app.service.pomodoro

import java.util.concurrent.atomic.AtomicLong

@javax.inject.Singleton
class PomodoroSessionEndSignal @javax.inject.Inject constructor() {

    private val lastFiredAtMillis = AtomicLong(0L)

    fun tryFire(): Boolean {
        val now = System.currentTimeMillis()
        val previous = lastFiredAtMillis.get()
        if (now - previous < WINDOW_MILLIS) {
            return false
        }
        return lastFiredAtMillis.compareAndSet(previous, now)
    }

    fun reset() {
        lastFiredAtMillis.set(0L)
    }

    private companion object {
        // Absorbs the alarm-vs-engine race; short enough to re-fire after `+1:00`.
        const val WINDOW_MILLIS = 10_000L
    }
}
