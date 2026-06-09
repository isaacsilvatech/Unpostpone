package com.unpostpone.app.data.repository

import android.content.SharedPreferences
import com.unpostpone.app.core.tempunlock.UnlockDuration
import com.unpostpone.app.domain.repository.UnlockDurationPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnlockDurationPreferencesImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) : UnlockDurationPreferences {

    private val _current = MutableStateFlow(readPersisted())
    override val current: StateFlow<Int> = _current.asStateFlow()

    override fun setDuration(minutes: Int) {
        val sanitized = minutes.coerceIn(
            UnlockDuration.MIN_MINUTES,
            UnlockDuration.MAX_MINUTES,
        )
        if (sanitized == _current.value) return
        sharedPreferences.edit()
            .putInt(KEY_DURATION_MINUTES, sanitized)
            .apply()
        _current.value = sanitized
    }

    private fun readPersisted(): Int {
        val raw = sharedPreferences.getInt(KEY_DURATION_MINUTES, UnlockDuration.DEFAULT_MINUTES)
        return raw.coerceIn(
            UnlockDuration.MIN_MINUTES,
            UnlockDuration.MAX_MINUTES,
        )
    }

    private companion object {
        const val KEY_DURATION_MINUTES = "unpostpone.unlock.duration.minutes"
    }
}
