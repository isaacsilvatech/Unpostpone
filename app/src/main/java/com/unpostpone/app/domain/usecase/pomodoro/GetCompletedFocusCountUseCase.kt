package com.unpostpone.app.domain.usecase.pomodoro

import com.unpostpone.app.domain.repository.PomodoroSessionRepository
import javax.inject.Inject

class GetCompletedFocusCountUseCase @Inject constructor(
    private val repository: PomodoroSessionRepository,
) {
    suspend operator fun invoke(): Int = repository.countCompletedFocusSessions()
}
