package com.unpostpone.app.domain.usecase.blockedapp

import com.unpostpone.app.domain.model.BlockedApp
import com.unpostpone.app.domain.repository.BlockedAppRepository
import javax.inject.Inject

class AddBlockedAppUseCase @Inject constructor(
    private val repository: BlockedAppRepository
) {
    suspend operator fun invoke(app: BlockedApp) = repository.addBlockedApp(app)
}
