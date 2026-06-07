package com.unpostpone.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blocked_apps")
data class BlockedAppEntity(
    @PrimaryKey
    val packageName: String,
    val displayName: String,
    val isEnabled: Boolean = true,
    val addedAt: Long = System.currentTimeMillis()
)
