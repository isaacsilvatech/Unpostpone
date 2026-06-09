package com.unpostpone.app.domain.usecase.blockedapp

import com.unpostpone.app.domain.repository.BlockingPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveBlockingEnabledUseCase @Inject constructor(
    private val preferences: BlockingPreferences,
) {
    operator fun invoke(): Flow<Boolean> = preferences.isBlockingEnabled
}
