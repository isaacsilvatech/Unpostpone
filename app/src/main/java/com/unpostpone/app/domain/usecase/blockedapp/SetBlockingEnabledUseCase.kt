package com.unpostpone.app.domain.usecase.blockedapp

import com.unpostpone.app.domain.repository.BlockingPreferences
import javax.inject.Inject

/**
 * Persists the global "is blocking enabled" flag via [BlockingPreferences].
 * Pure delegation; the [BlockingController] and the dashboard toggle both
 * call this to flip the state.
 */
class SetBlockingEnabledUseCase @Inject constructor(
    private val preferences: BlockingPreferences,
) {
    suspend operator fun invoke(enabled: Boolean) = preferences.setBlockingEnabled(enabled)
}
