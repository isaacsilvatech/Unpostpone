package com.unpostpone.app.service.pomodoro

import com.unpostpone.app.domain.model.PomodoroSessionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PomodoroTimerEngine @Inject constructor() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var tickJob: Job? = null

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    private var focusMinutes: Int = 25
    private var breakMinutes: Int = 5

    fun start(durationMillis: Long, type: PomodoroSessionType) {
        val now = System.currentTimeMillis()
        _state.value = State(
            status = Status.RUNNING,
            sessionType = type,
            totalMillis = durationMillis,
            remainingMillis = durationMillis,
            runningStartedAtEpochMillis = now,
            pausedAccumulatedMillis = 0L,
            pauseStartedAtEpochMillis = 0L,
        )
        scheduleTickLoop()
    }

    fun configurePreset(focusMinutes: Int, breakMinutes: Int) {
        this.focusMinutes = focusMinutes
        this.breakMinutes = breakMinutes
    }

    fun nextSession(): Pair<PomodoroSessionType, Long> {
        val current = _state.value
        val nextType = when (current.sessionType) {
            PomodoroSessionType.FOCUS -> PomodoroSessionType.BREAK
            PomodoroSessionType.BREAK -> PomodoroSessionType.FOCUS
        }
        val minutes = when (nextType) {
            PomodoroSessionType.FOCUS -> focusMinutes
            PomodoroSessionType.BREAK -> breakMinutes
        }
        return nextType to (minutes * 60_000L)
    }

    fun pause() {
        if (_state.value.status != Status.RUNNING) return
        tickJob?.cancel()
        val now = System.currentTimeMillis()
        val s = _state.value
        val currentRemaining = computeRemainingMillis(s, now)
        _state.value = s.copy(
            status = Status.PAUSED,
            remainingMillis = currentRemaining,
            pauseStartedAtEpochMillis = now,
        )
    }

    fun resume() {
        if (_state.value.status != Status.PAUSED) return
        val now = System.currentTimeMillis()
        val s = _state.value
        val pauseDuration = if (s.pauseStartedAtEpochMillis > 0L)
            now - s.pauseStartedAtEpochMillis
        else 0L
        _state.value = s.copy(
            status = Status.RUNNING,
            runningStartedAtEpochMillis = now,
            pausedAccumulatedMillis = s.pausedAccumulatedMillis + pauseDuration,
            pauseStartedAtEpochMillis = 0L,
        )
        scheduleTickLoop()
    }

    fun addMinute() {
        val s = _state.value
        if (s.status == Status.IDLE) return
        val now = System.currentTimeMillis()
        val currentRemaining = computeRemainingMillis(s, now)
        _state.value = s.copy(
            totalMillis = s.totalMillis + 60_000L,
            remainingMillis = currentRemaining + 60_000L,
            runningStartedAtEpochMillis = now,
            pauseStartedAtEpochMillis = 0L,
        )
    }

    fun stop() {
        tickJob?.cancel()
        _state.value = State()
    }

    fun acknowledgeComplete() {
        tickJob?.cancel()
        _state.value = State()
    }

    private fun scheduleTickLoop() {
        tickJob?.cancel()
        tickJob = scope.launch {
            while (true) {
                delay(1_000L)
                val s = _state.value
                if (s.status != Status.RUNNING) break
                val now = System.currentTimeMillis()
                _state.value = s.copy(remainingMillis = computeRemainingMillis(s, now))
            }
        }
    }

    fun syncFromWallClock() {
        val s = _state.value
        if (s.status != Status.RUNNING) return
        val now = System.currentTimeMillis()
        _state.value = s.copy(remainingMillis = computeRemainingMillis(s, now))
    }

    private fun computeRemainingMillis(s: State, now: Long): Long {
        if (s.runningStartedAtEpochMillis == 0L) return s.remainingMillis
        val elapsed = now - s.runningStartedAtEpochMillis - s.pausedAccumulatedMillis
        return s.totalMillis - elapsed
    }

    enum class Status { IDLE, RUNNING, PAUSED }

    data class State(
        val status: Status = Status.IDLE,
        val sessionType: PomodoroSessionType = PomodoroSessionType.FOCUS,
        val totalMillis: Long = 0L,
        val remainingMillis: Long = 0L,
        val runningStartedAtEpochMillis: Long = 0L,
        val pausedAccumulatedMillis: Long = 0L,
        val pauseStartedAtEpochMillis: Long = 0L,
    )
}

fun formatRemainingMillis(remainingMillis: Long): String {
    val sign = if (remainingMillis < 0L) "−" else ""
    val absMillis = kotlin.math.abs(remainingMillis)
    val totalSeconds = absMillis / 1_000L
    val minutes = totalSeconds / 60L
    val seconds = totalSeconds % 60L
    return "$sign%02d:%02d".format(minutes, seconds)
}
