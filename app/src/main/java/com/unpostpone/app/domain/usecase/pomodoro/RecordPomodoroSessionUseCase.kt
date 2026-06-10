package com.unpostpone.app.domain.usecase.pomodoro

import com.unpostpone.app.domain.model.PomodoroSession
import com.unpostpone.app.domain.repository.PomodoroSessionRepository
import javax.inject.Inject

class RecordPomodoroSessionUseCase @Inject constructor(
    private val repository: PomodoroSessionRepository,
) {
    suspend operator fun invoke(session: PomodoroSession): Long = repository.recordSession(session)
}
