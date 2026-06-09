package com.unpostpone.app.core.tempunlock

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TemporaryUnlockManager @Inject constructor() {

    data class State(
        val packageName: String? = null,
        val displayName: String? = null,
        val endsAt: Long = 0L,
    ) {
        val isActive: Boolean
            get() = packageName != null && System.currentTimeMillis() < endsAt

        val remainingSeconds: Int
            get() = if (!isActive) 0
            else ((endsAt - System.currentTimeMillis() + 999L) / 1000L).toInt()
    }

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    fun start(packageName: String, displayName: String, durationSeconds: Int) {
        val endsAt = System.currentTimeMillis() + durationSeconds * 1000L
        _state.value = State(packageName, displayName, endsAt)
    }

    fun clear() {
        _state.value = State()
    }

    fun isActive(packageName: String): Boolean {
        val s = _state.value
        return s.packageName == packageName && System.currentTimeMillis() < s.endsAt
    }

    companion object {
        const val DEFAULT_DURATION_SECONDS = 5 * 60
    }
}
