package com.unpostpone.app.domain.usecase.pomodoro

import com.unpostpone.app.domain.model.PomodoroSession
import com.unpostpone.app.domain.repository.PomodoroSessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePomodoroSessionsForDayUseCase @Inject constructor(
    private val repository: PomodoroSessionRepository,
) {
    operator fun invoke(startOfDay: Long, endOfDay: Long): Flow<List<PomodoroSession>> =
        repository.observeSessionsForDay(startOfDay, endOfDay)
}
