package com.unpostpone.app.presentation.pomodoro

import com.unpostpone.app.domain.model.PomodoroPreset
import com.unpostpone.app.domain.model.PomodoroSessionType
import com.unpostpone.app.service.pomodoro.formatRemainingMillis

sealed interface TimerState {
    data object Idle : TimerState
    data object Running : TimerState
    data object Paused : TimerState
    data object Finished : TimerState
}

data class PomodoroUiState(
    val currentSessionType: PomodoroSessionType = PomodoroSessionType.FOCUS,
    val selectedPreset: PomodoroPreset = PomodoroPreset.Classic,
    val timerState: TimerState = TimerState.Idle,
    val remainingMillis: Long = PomodoroPreset.Classic.focusMinutes * 60_000L,
    val plannedDurationMillis: Long = PomodoroPreset.Classic.focusMinutes * 60_000L,
    val completedFocusCount: Int = 0,
    val availablePresets: List<PomodoroPreset> = PomodoroPreset.All,
    val inOvertime: Boolean = false,
) {
    val progress: Float
        get() {
            if (plannedDurationMillis <= 0L) return 0f
            val raw = 1f - (remainingMillis.toFloat() / plannedDurationMillis.toFloat())
            return raw.coerceIn(0f, 1f)
        }

    val isSessionActive: Boolean
        get() = (timerState == TimerState.Running || timerState == TimerState.Paused) && !inOvertime

    val formattedRemaining: String
        get() = formatRemainingMillis(remainingMillis)
}
