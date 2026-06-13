package com.unpostpone.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.unpostpone.app.domain.model.PomodoroPreset
import com.unpostpone.app.domain.model.PomodoroSession
import com.unpostpone.app.domain.model.PomodoroSessionType

@Entity(tableName = "pomodoro_sessions")
data class PomodoroSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val type: String,
    val presetName: String,
    val focusMinutes: Int,
    val breakMinutes: Int,
    val plannedDurationMillis: Long,
    val startedAtEpochMillis: Long,
    val endedAtEpochMillis: Long?,
    val completed: Boolean,
) {
    fun toDomain(): PomodoroSession = PomodoroSession(
        id = id.takeIf { it != 0L },
        type = PomodoroSessionType.valueOf(type),
        preset = PomodoroPreset(
            name = presetName,
            focusMinutes = focusMinutes,
            breakMinutes = breakMinutes,
        ),
        plannedDurationMillis = plannedDurationMillis,
        startedAtEpochMillis = startedAtEpochMillis,
        endedAtEpochMillis = endedAtEpochMillis,
        completed = completed,
    )

    companion object {
        fun fromDomain(session: PomodoroSession): PomodoroSessionEntity = PomodoroSessionEntity(
            id = session.id ?: 0L,
            type = session.type.name,
            presetName = session.preset.name,
            focusMinutes = session.preset.focusMinutes,
            breakMinutes = session.preset.breakMinutes,
            plannedDurationMillis = session.plannedDurationMillis,
            startedAtEpochMillis = session.startedAtEpochMillis,
            endedAtEpochMillis = session.endedAtEpochMillis,
            completed = session.completed,
        )
    }
}
