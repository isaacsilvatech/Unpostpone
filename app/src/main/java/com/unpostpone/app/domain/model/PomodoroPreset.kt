package com.unpostpone.app.domain.model

data class PomodoroPreset(
    val name: String,
    val focusMinutes: Int,
    val breakMinutes: Int,
) {
    fun sessionTypeFor(plannedDurationMillis: Long): PomodoroSessionType {
        val minutes = (plannedDurationMillis / 60_000L).toInt()
        return when (minutes) {
            focusMinutes -> PomodoroSessionType.FOCUS
            breakMinutes -> PomodoroSessionType.BREAK
            else -> PomodoroSessionType.FOCUS
        }
    }

    companion object {
        val Classic = PomodoroPreset(
            name = "classic",
            focusMinutes = 25,
            breakMinutes = 5,
        )
        val DeepWork = PomodoroPreset(
            name = "deep_work",
            focusMinutes = 50,
            breakMinutes = 10,
        )
        val Extended = PomodoroPreset(
            name = "extended",
            focusMinutes = 90,
            breakMinutes = 15,
        )

        val All: List<PomodoroPreset> = listOf(Classic, DeepWork, Extended)
    }
}
