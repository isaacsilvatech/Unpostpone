package com.unpostpone.app.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Global "is blocking enabled" flag. Survives process death and is the
 * single switch that gates the [com.unpostpone.app.service.accessibility.UnpostponeAccessibilityService]
 * blocker and starts/stops the [com.unpostpone.app.service.monitoring.AppMonitoringService].
 *
 * Implementations are expected to hydrate the value from persistent storage
 * (e.g. SharedPreferences) on init and emit updates whenever the underlying
 * store changes — even from outside the class — so all observers stay in sync.
 */
interface BlockingPreferences {
    /** Hot, replay-1 flow with the current persisted value. Initial value is `false`. */
    val isBlockingEnabled: Flow<Boolean>

    /** Persist the new value. Emits a new value on [isBlockingEnabled]. */
    suspend fun setBlockingEnabled(enabled: Boolean)
}
