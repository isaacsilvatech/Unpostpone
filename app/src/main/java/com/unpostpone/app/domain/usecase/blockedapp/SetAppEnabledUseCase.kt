package com.unpostpone.app.domain.usecase.blockedapp

import com.unpostpone.app.domain.repository.BlockedAppRepository
import javax.inject.Inject

/**
 * Flips the [isEnabled] flag on a row in the blocked-apps table without
 * touching the rest of the row. Used by the Settings screen to toggle a
 * known app on or off without re-inserting it (which would reset `addedAt`).
 *
 * The row must already exist — the ViewModel is responsible for seeding
 * the default list on first run. Calling this on an unknown package
 * silently does nothing (Room's UPDATE matches 0 rows).
 */
class SetAppEnabledUseCase @Inject constructor(
    private val repository: BlockedAppRepository
) {
    suspend operator fun invoke(packageName: String, isEnabled: Boolean) =
        repository.setAppEnabled(packageName, isEnabled)
}
