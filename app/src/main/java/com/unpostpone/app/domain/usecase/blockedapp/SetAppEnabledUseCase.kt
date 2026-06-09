package com.unpostpone.app.domain.usecase.blockedapp

import com.unpostpone.app.domain.repository.BlockedAppRepository
import javax.inject.Inject

class SetAppEnabledUseCase @Inject constructor(
    private val repository: BlockedAppRepository
) {
    suspend operator fun invoke(packageName: String, isEnabled: Boolean) =
        repository.setAppEnabled(packageName, isEnabled)
}
