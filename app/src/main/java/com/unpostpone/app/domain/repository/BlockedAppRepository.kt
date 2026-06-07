package com.unpostpone.app.domain.repository

import com.unpostpone.app.domain.model.BlockedApp
import kotlinx.coroutines.flow.Flow

interface BlockedAppRepository {
    fun getAllBlockedApps(): Flow<List<BlockedApp>>
    fun getEnabledBlockedApps(): Flow<List<BlockedApp>>
    suspend fun isAppBlocked(packageName: String): Boolean
    suspend fun addBlockedApp(app: BlockedApp)
    suspend fun removeBlockedApp(app: BlockedApp)
    suspend fun setAppEnabled(packageName: String, isEnabled: Boolean)
}
