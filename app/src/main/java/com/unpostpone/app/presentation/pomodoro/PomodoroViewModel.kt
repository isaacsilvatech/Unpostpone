package com.unpostpone.app.presentation.pomodoro

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unpostpone.app.domain.model.PomodoroPreset
import com.unpostpone.app.domain.model.PomodoroSession
import com.unpostpone.app.domain.model.PomodoroSessionType
import com.unpostpone.app.domain.usecase.pomodoro.RecordPomodoroSessionUseCase
import com.unpostpone.app.service.pomodoro.PomodoroAlarmEvent
import com.unpostpone.app.service.pomodoro.PomodoroAlarmScheduler
import com.unpostpone.app.service.pomodoro.PomodoroEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PomodoroViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val alarmScheduler: PomodoroAlarmScheduler,
    private val eventBus: PomodoroEventBus,
    private val recordPomodoroSession: RecordPomodoroSessionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PomodoroUiState())
    val uiState: StateFlow<PomodoroUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var eventCollectionJob: Job? = null

    private var sessionStartedAtEpochMillis: Long = 0L

    init {
        eventCollectionJob = viewModelScope.launch {
            eventBus.sessionComplete.collect { event ->
                when (event) {
                    is PomodoroAlarmEvent.SessionComplete -> handleAlarmSessionComplete(event.sessionType)
                }
            }
        }
    }

    private fun durationFor(type: PomodoroSessionType, preset: PomodoroPreset): Long {
        val minutes = when (type) {
            PomodoroSessionType.FOCUS -> preset.focusMinutes
            PomodoroSessionType.SHORT_BREAK -> preset.shortBreakMinutes
            PomodoroSessionType.LONG_BREAK -> preset.longBreakMinutes
        }
        return minutes * 60_000L
    }

    fun onStart() {
        if (_uiState.value.timerState == TimerState.Running) return
        sessionStartedAtEpochMillis = System.currentTimeMillis()
        val state = _uiState.value
        _uiState.update {
            it.copy(
                timerState = TimerState.Running,
                showSessionCompleteDialog = false,
            )
        }
        alarmScheduler.scheduleSessionEnd(
            plannedDurationMillis = state.plannedDurationMillis,
            sessionType = state.currentSessionType,
        )
        launchTimer()
    }

    fun onPause() {
        if (_uiState.value.timerState != TimerState.Running) return
        timerJob?.cancel()
        timerJob = null
        alarmScheduler.cancel()
        _uiState.update { it.copy(timerState = TimerState.Paused) }
    }

    fun onResume() {
        if (_uiState.value.timerState != TimerState.Paused) return
        val now = System.currentTimeMillis()
        val elapsedBeforePause = _uiState.value.plannedDurationMillis - _uiState.value.remainingMillis
        sessionStartedAtEpochMillis = now - elapsedBeforePause
        val state = _uiState.value
        _uiState.update { it.copy(timerState = TimerState.Running) }
        alarmScheduler.scheduleSessionEnd(
            plannedDurationMillis = state.remainingMillis,
            sessionType = state.currentSessionType,
        )
        launchTimer()
    }

    fun onReset() {
        timerJob?.cancel()
        timerJob = null
        alarmScheduler.cancel()
        _uiState.update {
            it.copy(
                timerState = TimerState.Idle,
                remainingMillis = durationFor(it.currentSessionType, it.selectedPreset),
                plannedDurationMillis = durationFor(it.currentSessionType, it.selectedPreset),
                showSessionCompleteDialog = false,
            )
        }
    }

    fun onPresetSelected(preset: PomodoroPreset) {
        timerJob?.cancel()
        timerJob = null
        alarmScheduler.cancel()
        _uiState.update {
            it.copy(
                selectedPreset = preset,
                timerState = TimerState.Idle,
                remainingMillis = durationFor(it.currentSessionType, preset),
                plannedDurationMillis = durationFor(it.currentSessionType, preset),
                showSessionCompleteDialog = false,
            )
        }
    }

    fun onSkipToNext() {
        advanceToNextSession()
    }

    fun onDismissCompleteDialog() {
        _uiState.update { it.copy(showSessionCompleteDialog = false) }
    }

    fun onEvent(event: PomodoroEvent) {
        when (event) {
            PomodoroEvent.Start -> onStart()
            PomodoroEvent.Pause -> onPause()
            PomodoroEvent.Resume -> onResume()
            PomodoroEvent.Reset -> onReset()
            PomodoroEvent.DismissCompleteDialog -> onDismissCompleteDialog()
            PomodoroEvent.SkipToNext -> onSkipToNext()
            is PomodoroEvent.PresetSelected -> onPresetSelected(event.preset)
        }
    }

    private fun launchTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                val now = System.currentTimeMillis()
                val elapsed = now - sessionStartedAtEpochMillis
                val planned = _uiState.value.plannedDurationMillis
                val remaining = (planned - elapsed).coerceAtLeast(0L)
                _uiState.update { it.copy(remainingMillis = remaining) }
                if (remaining <= 0L) {
                    handleSessionComplete()
                    break
                }
                delay(250L)
            }
        }
    }

    private fun handleSessionComplete() {
        timerJob = null
        recordSessionInBackground(completed = true)
        val state = _uiState.value
        val nextFocusCount = if (state.currentSessionType == PomodoroSessionType.FOCUS)
            state.completedFocusCount + 1
        else state.completedFocusCount
        advanceToNextSessionInternal(nextFocusCount)
        _uiState.update {
            it.copy(
                timerState = TimerState.Finished,
                showSessionCompleteDialog = true,
            )
        }
    }

    private fun handleAlarmSessionComplete(sessionType: PomodoroSessionType) {
        if (_uiState.value.timerState == TimerState.Idle) return
        recordSessionInBackground(completed = true, forcedType = sessionType)
    }

    private fun recordSessionInBackground(
        completed: Boolean,
        forcedType: PomodoroSessionType? = null,
    ) {
        val snapshot = _uiState.value
        val type = forcedType ?: snapshot.currentSessionType
        val session = PomodoroSession(
            id = null,
            type = type,
            preset = snapshot.selectedPreset,
            plannedDurationMillis = snapshot.plannedDurationMillis,
            startedAtEpochMillis = sessionStartedAtEpochMillis,
            endedAtEpochMillis = System.currentTimeMillis(),
            completed = completed,
        )
        viewModelScope.launch {
            runCatching { recordPomodoroSession(session) }
        }
    }

    private fun advanceToNextSession() {
        val state = _uiState.value
        timerJob?.cancel()
        timerJob = null
        alarmScheduler.cancel()
        val nextFocusCount = if (state.currentSessionType == PomodoroSessionType.FOCUS)
            state.completedFocusCount + 1
        else state.completedFocusCount
        advanceToNextSessionInternal(nextFocusCount)
        _uiState.update { it.copy(timerState = TimerState.Idle, showSessionCompleteDialog = false) }
    }

    private fun advanceToNextSessionInternal(focusCountAfterThisOne: Int) {
        val state = _uiState.value
        val nextType: PomodoroSessionType = when (state.currentSessionType) {
            PomodoroSessionType.FOCUS ->
                if (focusCountAfterThisOne > 0 && focusCountAfterThisOne % state.selectedPreset.cyclesBeforeLongBreak == 0)
                    PomodoroSessionType.LONG_BREAK
                else
                    PomodoroSessionType.SHORT_BREAK
            PomodoroSessionType.SHORT_BREAK, PomodoroSessionType.LONG_BREAK -> PomodoroSessionType.FOCUS
        }
        val nextDuration = durationFor(nextType, state.selectedPreset)
        _uiState.update {
            it.copy(
                currentSessionType = nextType,
                completedFocusCount = focusCountAfterThisOne,
                remainingMillis = nextDuration,
                plannedDurationMillis = nextDuration,
            )
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        timerJob = null
        eventCollectionJob?.cancel()
        eventCollectionJob = null
        alarmScheduler.cancel()
        super.onCleared()
    }
}
