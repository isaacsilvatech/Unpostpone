package com.unpostpone.app.domain.repository

import kotlinx.coroutines.flow.Flow


interface BlockingPreferences {

    val isBlockingEnabled: Flow<Boolean>

    suspend fun setBlockingEnabled(enabled: Boolean)
}
