package com.unpostpone.app.domain.usecase.blockedapp

import com.unpostpone.app.domain.repository.BlockedAppRepository
import javax.inject.Inject

class IsAppBlockedUseCase @Inject constructor(
    private val repository: BlockedAppRepository
) {
    suspend operator fun invoke(packageName: String): Boolean =
        repository.isAppBlocked(packageName)
}
