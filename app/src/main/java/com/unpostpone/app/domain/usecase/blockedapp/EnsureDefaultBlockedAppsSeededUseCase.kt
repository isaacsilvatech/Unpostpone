package com.unpostpone.app.domain.usecase.blockedapp

import com.unpostpone.app.core.util.Constants
import com.unpostpone.app.domain.model.BlockedApp
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class EnsureDefaultBlockedAppsSeededUseCase @Inject constructor(
    private val getBlockedApps: GetBlockedAppsUseCase,
    private val addBlockedApp: AddBlockedAppUseCase,
) {
    suspend operator fun invoke() {
        val current = getBlockedApps().first()
        val known = current.map { it.packageName }.toSet()
        val missing = Constants.DEFAULT_BLOCKED_APPS.filter { it.packageName !in known }
        missing.forEach { def ->
            addBlockedApp(
                BlockedApp(
                    packageName = def.packageName,
                    displayName = def.displayName,
                    isEnabled = true,
                )
            )
        }
    }
}
