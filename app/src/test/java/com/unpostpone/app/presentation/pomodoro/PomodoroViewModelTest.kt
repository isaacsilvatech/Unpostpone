package com.unpostpone.app.presentation.pomodoro

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.unpostpone.app.domain.model.PomodoroPreset
import com.unpostpone.app.domain.model.PomodoroSession
import com.unpostpone.app.domain.model.PomodoroSessionType
import com.unpostpone.app.domain.repository.PomodoroSessionRepository
import com.unpostpone.app.domain.usecase.pomodoro.RecordPomodoroSessionUseCase
import com.unpostpone.app.service.pomodoro.PomodoroAlarmEvent
import com.unpostpone.app.service.pomodoro.PomodoroAlarmScheduler
import com.unpostpone.app.service.pomodoro.PomodoroEventBus
import com.unpostpone.app.service.pomodoro.PomodoroNotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE, sdk = [33])
class PomodoroViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var context: Context

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        context = ApplicationProvider.getApplicationContext()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init — state is Idle, FOCUS, Classic preset, 25 minutes planned`() = runTest(testDispatcher) {
        val viewModel = buildViewModel()

        val state = viewModel.uiState.value
        assertEquals(TimerState.Idle, state.timerState)
        assertEquals(PomodoroSessionType.FOCUS, state.currentSessionType)
        assertEquals(PomodoroPreset.Classic, state.selectedPreset)
        assertEquals(25L * 60_000L, state.plannedDurationMillis)
        assertEquals(25L * 60_000L, state.remainingMillis)
        assertEquals(0, state.completedFocusCount)
    }

    @Test
    fun `onStart — transitions to Running and schedules an alarm for the planned duration`() =
        runTest(testDispatcher) {
            val scheduler = RecordingAlarmScheduler()
            val viewModel = buildViewModel(alarmScheduler = scheduler)

            viewModel.onStart()
            runCurrent()

            assertEquals(TimerState.Running, viewModel.uiState.value.timerState)
            assertEquals(1, scheduler.scheduled.size)
            assertEquals(25L * 60_000L, scheduler.scheduled.single().plannedDurationMillis)
            assertEquals(PomodoroSessionType.FOCUS, scheduler.scheduled.single().sessionType)
        }

    @Test
    fun `onPause — transitions to Paused and cancels the alarm`() = runTest(testDispatcher) {
        val scheduler = RecordingAlarmScheduler()
        val viewModel = buildViewModel(alarmScheduler = scheduler)

        viewModel.onStart()
        runCurrent()
        assertEquals(TimerState.Running, viewModel.uiState.value.timerState)

        viewModel.onPause()
        runCurrent()

        assertEquals(TimerState.Paused, viewModel.uiState.value.timerState)
        assertTrue("cancel() should be called on pause", scheduler.cancelled)
    }

    @Test
    fun `onReset — returns to Idle, planned and remaining equal the classic focus duration`() =
        runTest(testDispatcher) {
            val scheduler = RecordingAlarmScheduler()
            val viewModel = buildViewModel(alarmScheduler = scheduler)

            viewModel.onStart()
            runCurrent()
            viewModel.onReset()
            runCurrent()

            val state = viewModel.uiState.value
            assertEquals(TimerState.Idle, state.timerState)
            assertEquals(25L * 60_000L, state.remainingMillis)
            assertEquals(25L * 60_000L, state.plannedDurationMillis)
            assertEquals(false, state.showSessionCompleteDialog)
            assertTrue("cancel() should be called on reset", scheduler.cancelled)
        }

    @Test
    fun `onPresetSelected — updates plannedDurationMillis to match the new preset's focus length`() =
        runTest(testDispatcher) {
            val scheduler = RecordingAlarmScheduler()
            val viewModel = buildViewModel(alarmScheduler = scheduler)

            viewModel.onPresetSelected(PomodoroPreset.DeepWork)
            runCurrent()

            val state = viewModel.uiState.value
            assertEquals(PomodoroPreset.DeepWork, state.selectedPreset)
            assertEquals(50L * 60_000L, state.plannedDurationMillis)
            assertEquals(50L * 60_000L, state.remainingMillis)
            assertEquals(TimerState.Idle, state.timerState)
        }

    @Test
    fun `four completed focus sessions advance to LONG_BREAK as the next session type`() =
        runTest(testDispatcher) {
            val scheduler = RecordingAlarmScheduler()
            val viewModel = buildViewModel(alarmScheduler = scheduler)

            repeat(7) {
                viewModel.onSkipToNext()
                runCurrent()
            }

            val state = viewModel.uiState.value
            assertEquals(4, state.completedFocusCount)
            assertEquals(PomodoroSessionType.LONG_BREAK, state.currentSessionType)
            assertEquals(15L * 60_000L, state.plannedDurationMillis)
        }

    @Test
    fun `EventBus SessionComplete for the running focus records a completed session`() =
        runTest(testDispatcher) {
            val scheduler = RecordingAlarmScheduler()
            val bus = PomodoroEventBus()
            val repo = RecordingPomodoroSessionRepository()
            val viewModel = buildViewModel(
                alarmScheduler = scheduler,
                eventBus = bus,
                recordUseCase = RecordPomodoroSessionUseCase(repo),
            )

            viewModel.onStart()
            runCurrent()

            bus.emit(PomodoroAlarmEvent.SessionComplete(PomodoroSessionType.FOCUS))
            runCurrent()

            assertTrue(
                "recordSession should be called for the alarm-triggered completion",
                repo.recorded.isNotEmpty(),
            )
            val recorded = repo.recorded.single()
            assertEquals(PomodoroSessionType.FOCUS, recorded.type)
            assertEquals(true, recorded.completed)
            assertEquals(PomodoroPreset.Classic, recorded.preset)
        }

    private fun buildViewModel(
        alarmScheduler: PomodoroAlarmScheduler = RecordingAlarmScheduler(),
        eventBus: PomodoroEventBus = PomodoroEventBus(),
        recordUseCase: RecordPomodoroSessionUseCase =
            RecordPomodoroSessionUseCase(RecordingPomodoroSessionRepository()),
    ): PomodoroViewModel = PomodoroViewModel(
        context = context,
        alarmScheduler = alarmScheduler,
        eventBus = eventBus,
        recordPomodoroSession = recordUseCase,
    )

    private class RecordingAlarmScheduler(
        notificationHelper: PomodoroNotificationHelper =
            PomodoroNotificationHelper(ApplicationProvider.getApplicationContext()),
    ) : PomodoroAlarmScheduler(
        appContext = ApplicationProvider.getApplicationContext(),
        notificationHelper = notificationHelper,
    ) {
        data class ScheduledCall(
            val plannedDurationMillis: Long,
            val sessionType: PomodoroSessionType,
        )

        val scheduled: MutableList<ScheduledCall> = mutableListOf()
        var cancelled: Boolean = false

        override fun scheduleSessionEnd(
            plannedDurationMillis: Long,
            sessionType: PomodoroSessionType,
        ) {
            scheduled += ScheduledCall(plannedDurationMillis, sessionType)
        }

        override fun cancel() {
            cancelled = true
        }
    }

    private class RecordingPomodoroSessionRepository : PomodoroSessionRepository {
        val recorded: MutableList<PomodoroSession> = mutableListOf()
        private val state = MutableStateFlow<List<PomodoroSession>>(emptyList())

        override fun observeSessionsForDay(
            startOfDay: Long,
            endOfDay: Long,
        ): Flow<List<PomodoroSession>> = state.asStateFlow()

        override suspend fun recordSession(session: PomodoroSession): Long {
            recorded += session
            return recorded.size.toLong()
        }

        override suspend fun countCompletedFocusSessions(): Int = 0
    }
}
