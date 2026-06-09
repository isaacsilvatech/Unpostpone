package com.unpostpone.app.presentation.blocker

import com.unpostpone.app.domain.model.Goal
import com.unpostpone.app.domain.model.Statistics
import com.unpostpone.app.domain.repository.GoalRepository
import com.unpostpone.app.domain.repository.StatisticsRepository
import com.unpostpone.app.domain.usecase.goal.GetGoalsUseCase
import com.unpostpone.app.domain.usecase.statistics.IncrementUnlockAttemptUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BlockerViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loadActiveGoals filters out completed goals`() = runTest(testDispatcher) {
        val active = goal(id = 1, name = "Write report", isCompleted = false)
        val completed = goal(id = 2, name = "Send email", isCompleted = true)
        val fakeRepo = FakeGoalRepository(initial = listOf(active, completed))

        val viewModel = BlockerViewModel(
            getGoalsUseCase = GetGoalsUseCase(fakeRepo),
            incrementUnlockAttemptUseCase = IncrementUnlockAttemptUseCase(FakeStatisticsRepository()),
        )
        runCurrent()

        assertEquals(listOf(active), viewModel.uiState.value.activeGoals)
    }

    @Test
    fun `init loadActiveGoals keeps all goals when none are completed`() = runTest(testDispatcher) {
        val g1 = goal(id = 1, name = "A", isCompleted = false)
        val g2 = goal(id = 2, name = "B", isCompleted = false)
        val fakeRepo = FakeGoalRepository(initial = listOf(g1, g2))

        val viewModel = BlockerViewModel(
            getGoalsUseCase = GetGoalsUseCase(fakeRepo),
            incrementUnlockAttemptUseCase = IncrementUnlockAttemptUseCase(FakeStatisticsRepository()),
        )
        runCurrent()

        assertEquals(listOf(g1, g2), viewModel.uiState.value.activeGoals)
    }

    @Test
    fun `requestTemporaryUnlock sets isTemporarilyUnlocked to true and starts countdown at 300`() =
        runTest(testDispatcher) {
            val fakeStats = FakeStatisticsRepository()
            val viewModel = BlockerViewModel(
                getGoalsUseCase = GetGoalsUseCase(FakeGoalRepository(initial = emptyList())),
                incrementUnlockAttemptUseCase = IncrementUnlockAttemptUseCase(fakeStats),
            )
            runCurrent()

            assertEquals(false, viewModel.uiState.value.isTemporarilyUnlocked)
            assertEquals(0, viewModel.uiState.value.unlockCountdown)
            assertEquals(0, fakeStats.unlockAttempts.size)

            viewModel.requestTemporaryUnlock()
            runCurrent()

            assertTrue(
                "incrementUnlockAttemptUseCase should be called at least once",
                fakeStats.unlockAttempts.isNotEmpty(),
            )
            assertEquals(true, viewModel.uiState.value.isTemporarilyUnlocked)
            assertEquals(300, viewModel.uiState.value.unlockCountdown)
        }

    @Test
    fun `requestTemporaryUnlock countdown decrements when virtual time advances`() =
        runTest(testDispatcher) {
            val viewModel = BlockerViewModel(
                getGoalsUseCase = GetGoalsUseCase(FakeGoalRepository(initial = emptyList())),
                incrementUnlockAttemptUseCase = IncrementUnlockAttemptUseCase(FakeStatisticsRepository()),
            )
            runCurrent()

            viewModel.requestTemporaryUnlock()
            runCurrent()
            assertEquals(300, viewModel.uiState.value.unlockCountdown)

            advanceTimeBy(5_000L)
            runCurrent()

            assertEquals(true, viewModel.uiState.value.isTemporarilyUnlocked)
            assertEquals(295, viewModel.uiState.value.unlockCountdown)
        }

    private fun goal(id: Long, name: String, isCompleted: Boolean) = Goal(
        id = id,
        name = name,
        targetMinutes = 60,
        progressMinutes = 0,
        isCompleted = isCompleted,
        date = "2026-06-08",
    )

    private class FakeGoalRepository(initial: List<Goal>) : GoalRepository {
        private val state = MutableStateFlow(initial)
        override fun getGoalsByDate(date: String): Flow<List<Goal>> = state.asStateFlow()
        override fun getAllGoals(): Flow<List<Goal>> = state.asStateFlow()
        override suspend fun addGoal(goal: Goal): Long = error("unused")
        override suspend fun updateGoal(goal: Goal) = error("unused")
        override suspend fun deleteGoal(goal: Goal) = error("unused")
        override suspend fun updateProgress(id: Long, progressMinutes: Int, isCompleted: Boolean) =
            error("unused")
    }

    private class FakeStatisticsRepository : StatisticsRepository {
        val unlockAttempts: MutableList<String> = mutableListOf()
        override fun getStatisticsByDate(date: String): Flow<Statistics?> = error("unused")
        override fun getRecentStatistics(): Flow<List<Statistics>> = error("unused")
        override suspend fun ensureDateExists(date: String) = Unit
        override suspend fun incrementBlockCount(date: String) = Unit
        override suspend fun incrementUnlockAttempts(date: String) {
            unlockAttempts += date
        }
        override suspend fun addFocusedMinutes(date: String, minutes: Int) = Unit
    }
}
