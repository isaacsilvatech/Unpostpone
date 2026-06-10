package com.unpostpone.app.domain.model

data class PomodoroPreset(
    val name: String,
    val focusMinutes: Int,
    val shortBreakMinutes: Int,
    val longBreakMinutes: Int,
    val cyclesBeforeLongBreak: Int,
) {
    companion object {
        val Classic = PomodoroPreset(
            name = "classic",
            focusMinutes = 25,
            shortBreakMinutes = 5,
            longBreakMinutes = 15,
            cyclesBeforeLongBreak = 4,
        )
        val DeepWork = PomodoroPreset(
            name = "deep_work",
            focusMinutes = 50,
            shortBreakMinutes = 10,
            longBreakMinutes = 20,
            cyclesBeforeLongBreak = 4,
        )
        val Extended = PomodoroPreset(
            name = "extended",
            focusMinutes = 90,
            shortBreakMinutes = 15,
            longBreakMinutes = 30,
            cyclesBeforeLongBreak = 4,
        )

        val All: List<PomodoroPreset> = listOf(Classic, DeepWork, Extended)
    }
}
