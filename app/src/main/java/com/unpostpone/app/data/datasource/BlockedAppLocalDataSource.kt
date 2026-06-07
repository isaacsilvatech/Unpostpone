package com.unpostpone.app.data.datasource

import com.unpostpone.app.data.local.dao.BlockedAppDao
import com.unpostpone.app.data.local.entity.BlockedAppEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BlockedAppLocalDataSource @Inject constructor(
    private val blockedAppDao: BlockedAppDao
) {
    fun getAllBlockedApps(): Flow<List<BlockedAppEntity>> = blockedAppDao.getAllBlockedApps()
    fun getEnabledBlockedApps(): Flow<List<BlockedAppEntity>> = blockedAppDao.getEnabledBlockedApps()
    suspend fun isAppBlocked(packageName: String): Boolean = blockedAppDao.isAppBlocked(packageName)
    suspend fun insertBlockedApp(app: BlockedAppEntity) = blockedAppDao.insertBlockedApp(app)
    suspend fun deleteBlockedApp(app: BlockedAppEntity) = blockedAppDao.deleteBlockedApp(app)
    suspend fun setAppEnabled(packageName: String, isEnabled: Boolean) =
        blockedAppDao.setAppEnabled(packageName, isEnabled)
}
