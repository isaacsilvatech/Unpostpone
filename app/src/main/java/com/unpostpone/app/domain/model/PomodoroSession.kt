package com.unpostpone.app.domain.model

data class PomodoroSession(
    val id: Long?,
    val type: PomodoroSessionType,
    val preset: PomodoroPreset,
    val plannedDurationMillis: Long,
    val startedAtEpochMillis: Long,
    val endedAtEpochMillis: Long?,
    val completed: Boolean,
)
