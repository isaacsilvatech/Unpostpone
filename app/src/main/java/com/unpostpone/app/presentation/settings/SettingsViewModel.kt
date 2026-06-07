package com.unpostpone.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unpostpone.app.domain.model.BlockedApp
import com.unpostpone.app.domain.usecase.blockedapp.AddBlockedAppUseCase
import com.unpostpone.app.domain.usecase.blockedapp.GetBlockedAppsUseCase
import com.unpostpone.app.domain.usecase.blockedapp.RemoveBlockedAppUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getBlockedAppsUseCase: GetBlockedAppsUseCase,
    private val addBlockedAppUseCase: AddBlockedAppUseCase,
    private val removeBlockedAppUseCase: RemoveBlockedAppUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init { loadBlockedApps() }

    private fun loadBlockedApps() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getBlockedAppsUseCase()
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { apps -> _uiState.update { it.copy(blockedApps = apps, isLoading = false) } }
        }
    }

    fun toggleApp(packageName: String, displayName: String) {
        viewModelScope.launch {
            val current = _uiState.value.blockedApps.find { it.packageName == packageName }
            if (current != null) {
                removeBlockedAppUseCase(current)
            } else {
                addBlockedAppUseCase(BlockedApp(packageName = packageName, displayName = displayName))
            }
        }
    }
}
