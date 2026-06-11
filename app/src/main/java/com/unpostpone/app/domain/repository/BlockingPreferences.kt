package com.unpostpone.app.domain.repository

import kotlinx.coroutines.flow.Flow


interface BlockingPreferences {

    val isBlockingEnabled: Flow<Boolean>

    val hasAutoEnabledOnce: Flow<Boolean>

    suspend fun setBlockingEnabled(enabled: Boolean)

    suspend fun markAutoEnabled()
}
