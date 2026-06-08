package com.unpostpone.app.domain.usecase.blockedapp

import com.unpostpone.app.domain.repository.BlockingPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Streams the global "is blocking enabled" flag from [BlockingPreferences].
 * Pure delegation; exists so the presentation / service layers depend on a
 * use case rather than the raw repository.
 */
class ObserveBlockingEnabledUseCase @Inject constructor(
    private val preferences: BlockingPreferences,
) {
    operator fun invoke(): Flow<Boolean> = preferences.isBlockingEnabled
}
