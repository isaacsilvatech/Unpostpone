package com.unpostpone.app.domain.usecase.blockedapp

import com.unpostpone.app.domain.model.BlockedApp
import com.unpostpone.app.domain.repository.BlockedAppRepository
import javax.inject.Inject

class RemoveBlockedAppUseCase @Inject constructor(
    private val repository: BlockedAppRepository
) {
    suspend operator fun invoke(app: BlockedApp) = repository.removeBlockedApp(app)
}
