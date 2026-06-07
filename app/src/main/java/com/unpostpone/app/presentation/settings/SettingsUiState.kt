package com.unpostpone.app.presentation.settings

import com.unpostpone.app.domain.model.BlockedApp

data class SettingsUiState(
    val blockedApps: List<BlockedApp> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
