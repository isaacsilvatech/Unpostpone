package com.unpostpone.app.domain.usecase.blockedapp

import com.unpostpone.app.domain.repository.BlockingPreferences
import javax.inject.Inject

class SetBlockingEnabledUseCase @Inject constructor(
    private val preferences: BlockingPreferences,
) {
    suspend operator fun invoke(enabled: Boolean) = preferences.setBlockingEnabled(enabled)
}
