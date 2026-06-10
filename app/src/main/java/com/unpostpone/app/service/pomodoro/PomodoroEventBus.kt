package com.unpostpone.app.service.pomodoro

import com.unpostpone.app.domain.model.PomodoroSessionType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class PomodoroAlarmEvent {
    data class SessionComplete(val sessionType: PomodoroSessionType) : PomodoroAlarmEvent()
}

open class PomodoroEventBus {

    private val _sessionComplete = MutableSharedFlow<PomodoroAlarmEvent>(
        replay = 0,
        extraBufferCapacity = 4,
    )

    val sessionComplete: SharedFlow<PomodoroAlarmEvent> = _sessionComplete.asSharedFlow()

    open suspend fun emit(event: PomodoroAlarmEvent) {
        _sessionComplete.emit(event)
    }
}
