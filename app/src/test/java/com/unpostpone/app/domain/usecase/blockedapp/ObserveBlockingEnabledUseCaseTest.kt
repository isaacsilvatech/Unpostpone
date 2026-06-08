package com.unpostpone.app.domain.usecase.blockedapp

import com.unpostpone.app.domain.repository.BlockingPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

/**
 * Unit tests for [ObserveBlockingEnabledUseCase]. Pure JUnit4, no
 * Robolectric, no coroutines-test — relies on the underlying
 * [BlockingPreferences] fake to drive the flow values.
 */
class ObserveBlockingEnabledUseCaseTest {

    @Test
    fun `invoke — returns the same flow instance as the repository`() {
        val fake = FakeBlockingPreferences(initial = false)
        val useCase = ObserveBlockingEnabledUseCase(fake)

        // The use case is a pure delegation; the returned flow IS the
        // repository's flow (asStateFlow returns the same instance).
        assertSame(fake.isBlockingEnabled, useCase())
    }

    @Test
    fun `invoke — emits the current value of the repository on first()`() = runBlocking {
        val fake = FakeBlockingPreferences(initial = false)
        val useCase = ObserveBlockingEnabledUseCase(fake)

        assertEquals(false, useCase().first())
    }

    @Test
    fun `invoke — reflects subsequent updates from the repository`() = runBlocking {
        val fake = FakeBlockingPreferences(initial = false)
        val useCase = ObserveBlockingEnabledUseCase(fake)

        fake.setBlockingEnabled(true)
        assertEquals(true, useCase().first())

        fake.setBlockingEnabled(false)
        assertEquals(false, useCase().first())
    }

    @Test
    fun `invoke — captures every emission as the repository state changes`() = runBlocking {
        val fake = FakeBlockingPreferences(initial = false)
        val useCase = ObserveBlockingEnabledUseCase(fake)

        // Collect up to 3 emissions on a background dispatcher; drive the
        // updates from the runBlocking coroutine. take(3) bounds the
        // collection so the test doesn't hang on the hot flow.
        val emissions = mutableListOf<Boolean>()
        val job = launch(Dispatchers.Unconfined) {
            useCase().take(3).toList(emissions)
        }
        fake.setBlockingEnabled(true)
        fake.setBlockingEnabled(false)
        job.join()

        // Hot replay-1 flow: the first emission is the initial value, then
        // whatever the fake set afterwards.
        assertEquals(listOf(false, true, false), emissions)
    }

    // ── Test helpers ────────────────────────────────────────────────────

    private class FakeBlockingPreferences(initial: Boolean) : BlockingPreferences {
        private val state = MutableStateFlow(initial)
        override val isBlockingEnabled: Flow<Boolean> = state.asStateFlow()
        override suspend fun setBlockingEnabled(enabled: Boolean) {
            state.value = enabled
        }
    }
}
