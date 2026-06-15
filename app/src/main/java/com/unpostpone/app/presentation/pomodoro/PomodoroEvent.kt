package com.unpostpone.app.presentation.pomodoro

import com.unpostpone.app.domain.model.PomodoroPreset

sealed interface PomodoroEvent {
    data object Start : PomodoroEvent
    data object Pause : PomodoroEvent
    data object Resume : PomodoroEvent
    data object Reset : PomodoroEvent
    data class PresetSelected(val preset: PomodoroPreset) : PomodoroEvent
    data object SkipToNext : PomodoroEvent
}
