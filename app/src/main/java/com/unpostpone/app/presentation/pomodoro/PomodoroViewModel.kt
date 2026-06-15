package com.unpostpone.app.presentation.pomodoro

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unpostpone.app.domain.model.PomodoroPreset
import com.unpostpone.app.domain.model.PomodoroSession
import com.unpostpone.app.domain.model.PomodoroSessionType
import com.unpostpone.app.domain.usecase.pomodoro.RecordPomodoroSessionUseCase
import com.unpostpone.app.service.pomodoro.PomodoroAlarmScheduler
import com.unpostpone.app.service.pomodoro.PomodoroTimerEngine
import com.unpostpone.app.service.pomodoro.PomodoroTimerService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
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
    private val recordPomodoroSession: RecordPomodoroSessionUseCase,
    private val timerEngine: PomodoroTimerEngine,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PomodoroUiState())
    val uiState: StateFlow<PomodoroUiState> = _uiState.asStateFlow()

    private var engineObserverJob: Job? = null

    private var sessionStartedAtEpochMillis: Long = 0L
    private var lastSeenSessionType: PomodoroSessionType = PomodoroSessionType.FOCUS
    private var lastSeenPreset: PomodoroPreset = PomodoroPreset.Classic

    private var overtimeRecorded: Boolean = false
    private var wasInOvertime: Boolean = false

    init {
        engineObserverJob = viewModelScope.launch {
            timerEngine.state.collect { state ->
                syncUiFromEngine(state)
            }
        }
    }

    private fun syncUiFromEngine(state: PomodoroTimerEngine.State) {
        val totalMillis = state.totalMillis
        val remainingMillis = state.remainingMillis
        val inOvertime = state.status == PomodoroTimerEngine.Status.RUNNING && remainingMillis < 0L
        val isEngineIdle = state.status == PomodoroTimerEngine.Status.IDLE
        val justLeftOvertimeToIdle = wasInOvertime && isEngineIdle
        val timerState = when {
            justLeftOvertimeToIdle -> TimerState.Idle
            state.status == PomodoroTimerEngine.Status.IDLE -> {
                if (remainingMillis == 0L) TimerState.Idle
                else TimerState.Running
            }
            state.status == PomodoroTimerEngine.Status.RUNNING -> {
                if (remainingMillis == 0L || inOvertime) TimerState.Finished
                else TimerState.Running
            }
            state.status == PomodoroTimerEngine.Status.PAUSED -> TimerState.Paused
            else -> TimerState.Idle
        }
        val preset = _uiState.value.selectedPreset
        _uiState.update { current ->
            if (justLeftOvertimeToIdle) {
                val nextType = when (state.sessionType) {
                    PomodoroSessionType.FOCUS -> PomodoroSessionType.BREAK
                    PomodoroSessionType.BREAK -> PomodoroSessionType.FOCUS
                }
                val nextDuration = durationFor(nextType, preset)
                val nextFocusCount = if (state.sessionType == PomodoroSessionType.FOCUS)
                    current.completedFocusCount + 1
                else current.completedFocusCount
                current.copy(
                    currentSessionType = nextType,
                    completedFocusCount = nextFocusCount,
                    plannedDurationMillis = nextDuration,
                    remainingMillis = nextDuration,
                    timerState = TimerState.Idle,
                    inOvertime = false,
                )
            } else {
                val planned = if (totalMillis > 0L) totalMillis else current.plannedDurationMillis
                current.copy(
                    currentSessionType = state.sessionType,
                    plannedDurationMillis = planned,
                    remainingMillis = if (isEngineIdle) {
                        if (current.remainingMillis == 0L) planned else current.remainingMillis
                    } else {
                        remainingMillis
                    },
                    timerState = timerState,
                    inOvertime = inOvertime,
                )
            }
        }
        wasInOvertime = inOvertime
        if (inOvertime && !overtimeRecorded) {
            overtimeRecorded = true
            handleSessionCompleteOvertime(state.sessionType)
        }
        if (justLeftOvertimeToIdle) {
            overtimeRecorded = false
        }
    }

    private fun durationFor(type: PomodoroSessionType, preset: PomodoroPreset): Long {
        val minutes = when (type) {
            PomodoroSessionType.FOCUS -> preset.focusMinutes
            PomodoroSessionType.BREAK -> preset.breakMinutes
        }
        return minutes * 60_000L
    }

    fun onStart() {
        if (_uiState.value.timerState == TimerState.Running) return
        val state = _uiState.value
        val sessionType = state.selectedPreset.sessionTypeFor(state.plannedDurationMillis)
        sessionStartedAtEpochMillis = System.currentTimeMillis()
        lastSeenSessionType = sessionType
        lastSeenPreset = state.selectedPreset
        overtimeRecorded = false

        startService(state.plannedDurationMillis, sessionType)
    }

    fun onPause() {
        if (_uiState.value.timerState != TimerState.Running) return
        sendServiceAction(PomodoroTimerService.ACTION_PAUSE)
    }

    fun onResume() {
        if (_uiState.value.timerState != TimerState.Paused) return
        sendServiceAction(PomodoroTimerService.ACTION_RESUME)
    }

    fun onReset() {
        sendServiceAction(PomodoroTimerService.ACTION_STOP)
        val s = _uiState.value
        val newDuration = durationFor(s.currentSessionType, s.selectedPreset)
        overtimeRecorded = false
        _uiState.update {
            it.copy(
                timerState = TimerState.Idle,
                remainingMillis = newDuration,
                plannedDurationMillis = newDuration,
            )
        }
    }

    fun onPresetSelected(preset: PomodoroPreset) {
        sendServiceAction(PomodoroTimerService.ACTION_STOP)
        overtimeRecorded = false
        _uiState.update {
            it.copy(
                selectedPreset = preset,
                timerState = TimerState.Idle,
                remainingMillis = durationFor(it.currentSessionType, preset),
                plannedDurationMillis = durationFor(it.currentSessionType, preset),
            )
        }
    }

    fun onSkipToNext() {
        advanceToNextSession()
    }

    fun onEvent(event: PomodoroEvent) {
        when (event) {
            PomodoroEvent.Start -> onStart()
            PomodoroEvent.Pause -> onPause()
            PomodoroEvent.Resume -> onResume()
            PomodoroEvent.Reset -> onReset()
            PomodoroEvent.SkipToNext -> onSkipToNext()
            is PomodoroEvent.PresetSelected -> onPresetSelected(event.preset)
        }
    }

    private fun startService(durationMillis: Long, type: PomodoroSessionType) {
        val preset = _uiState.value.selectedPreset
        val intent = Intent(context, PomodoroTimerService::class.java).apply {
            action = PomodoroTimerService.ACTION_START
            putExtra(PomodoroTimerService.EXTRA_DURATION_MILLIS, durationMillis)
            putExtra(PomodoroTimerService.EXTRA_SESSION_TYPE, type.ordinal)
            putExtra(PomodoroTimerService.EXTRA_FOCUS_MINUTES, preset.focusMinutes)
            putExtra(PomodoroTimerService.EXTRA_BREAK_MINUTES, preset.breakMinutes)
        }
        context.startForegroundService(intent)
        alarmScheduler.scheduleSessionEnd(durationMillis, type)
    }

    private fun sendServiceAction(action: String) {
        val intent = Intent(context, PomodoroTimerService::class.java).apply {
            this.action = action
        }
        context.startService(intent)
    }

    private fun handleSessionCompleteOvertime(sessionType: PomodoroSessionType) {
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
        val nextType: PomodoroSessionType = when (state.currentSessionType) {
            PomodoroSessionType.FOCUS -> PomodoroSessionType.BREAK
            PomodoroSessionType.BREAK -> PomodoroSessionType.FOCUS
        }
        val nextDuration = durationFor(nextType, state.selectedPreset)
        val nextFocusCount = if (state.currentSessionType == PomodoroSessionType.FOCUS)
            state.completedFocusCount + 1
            else state.completedFocusCount
        advanceToNextSessionInternal(nextFocusCount)
        _uiState.update { it.copy(timerState = TimerState.Idle) }
        startService(nextDuration, nextType)
    }

    private fun advanceToNextSessionInternal(focusCountAfterThisOne: Int) {
        val state = _uiState.value
        val nextType: PomodoroSessionType = when (state.currentSessionType) {
            PomodoroSessionType.FOCUS -> PomodoroSessionType.BREAK
            PomodoroSessionType.BREAK -> PomodoroSessionType.FOCUS
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
        engineObserverJob?.cancel()
        engineObserverJob = null
        super.onCleared()
    }
}
