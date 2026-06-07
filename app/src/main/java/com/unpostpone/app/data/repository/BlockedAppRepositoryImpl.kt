package com.unpostpone.app.data.repository

import com.unpostpone.app.data.datasource.BlockedAppLocalDataSource
import com.unpostpone.app.data.local.entity.BlockedAppEntity
import com.unpostpone.app.domain.model.BlockedApp
import com.unpostpone.app.domain.repository.BlockedAppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BlockedAppRepositoryImpl @Inject constructor(
    private val localDataSource: BlockedAppLocalDataSource
) : BlockedAppRepository {

    override fun getAllBlockedApps(): Flow<List<BlockedApp>> =
        localDataSource.getAllBlockedApps().map { it.map(BlockedAppEntity::toDomain) }

    override fun getEnabledBlockedApps(): Flow<List<BlockedApp>> =
        localDataSource.getEnabledBlockedApps().map { it.map(BlockedAppEntity::toDomain) }

    override suspend fun isAppBlocked(packageName: String): Boolean =
        localDataSource.isAppBlocked(packageName)

    override suspend fun addBlockedApp(app: BlockedApp) =
        localDataSource.insertBlockedApp(app.toEntity())

    override suspend fun removeBlockedApp(app: BlockedApp) =
        localDataSource.deleteBlockedApp(app.toEntity())

    override suspend fun setAppEnabled(packageName: String, isEnabled: Boolean) =
        localDataSource.setAppEnabled(packageName, isEnabled)
}

private fun BlockedAppEntity.toDomain() = BlockedApp(
    packageName = packageName, displayName = displayName,
    isEnabled = isEnabled, addedAt = addedAt
)

private fun BlockedApp.toEntity() = BlockedAppEntity(
    packageName = packageName, displayName = displayName,
    isEnabled = isEnabled, addedAt = addedAt
)
