package com.unpostpone.app.domain.usecase.blockedapp

import com.unpostpone.app.domain.model.BlockedApp
import com.unpostpone.app.domain.repository.BlockedAppRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBlockedAppsUseCase @Inject constructor(
    private val repository: BlockedAppRepository
) {
    operator fun invoke(): Flow<List<BlockedApp>> = repository.getAllBlockedApps()
}
