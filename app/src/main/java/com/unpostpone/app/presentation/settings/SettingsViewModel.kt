package com.unpostpone.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unpostpone.app.core.locale.LanguageManager
import com.unpostpone.app.core.locale.SupportedLanguage
import com.unpostpone.app.core.util.Constants
import com.unpostpone.app.domain.model.BlockedApp
import com.unpostpone.app.domain.usecase.blockedapp.AddBlockedAppUseCase
import com.unpostpone.app.domain.usecase.blockedapp.GetBlockedAppsUseCase
import com.unpostpone.app.domain.usecase.blockedapp.SetAppEnabledUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val languageManager: LanguageManager,
    private val getBlockedApps: GetBlockedAppsUseCase,
    private val addBlockedApp: AddBlockedAppUseCase,
    private val setAppEnabled: SetAppEnabledUseCase,
) : ViewModel() {
    val currentLanguage: StateFlow<SupportedLanguage> = languageManager.current

    private val _uiState = MutableStateFlow(SettingsUiState(isLoading = true))
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observeBlockedApps()
    }

    fun setLanguage(language: SupportedLanguage) {
        languageManager.setLanguage(language)
    }
    fun toggleApp(packageName: String, isEnabled: Boolean) {
        viewModelScope.launch {
            setAppEnabled(packageName, isEnabled)
        }
    }

    private fun observeBlockedApps() {
        viewModelScope.launch {
            val current = getBlockedApps().first()
            if (current.isEmpty()) {
                Constants.DEFAULT_BLOCKED_APPS.forEach { def ->
                    addBlockedApp(
                        BlockedApp(
                            packageName = def.packageName,
                            displayName = def.displayName,
                            isEnabled = false,
                        )
                    )
                }
            }
            getBlockedApps().collect { apps ->
                _uiState.update { it.copy(blockedApps = apps, isLoading = false) }
            }
        }
    }
}
