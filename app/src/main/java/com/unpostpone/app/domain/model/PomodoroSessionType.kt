package com.unpostpone.app.domain.model

enum class PomodoroSessionType(
    val defaultDurationMinutes: Int,
) {
    FOCUS(25),
    SHORT_BREAK(5),
    LONG_BREAK(15),
}
