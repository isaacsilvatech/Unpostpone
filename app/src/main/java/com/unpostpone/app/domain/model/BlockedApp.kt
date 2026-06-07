package com.unpostpone.app.domain.model

data class BlockedApp(
    val packageName: String,
    val displayName: String,
    val isEnabled: Boolean = true,
    val addedAt: Long = System.currentTimeMillis()
)
