package com.unpostpone.app.domain.model

enum class PomodoroSessionType(
    val defaultDurationMinutes: Int,
) {
    FOCUS(25),
    BREAK(5),
}
