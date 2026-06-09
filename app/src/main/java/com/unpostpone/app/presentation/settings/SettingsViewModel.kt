package com.unpostpone.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unpostpone.app.core.theme.ThemeMode
import com.unpostpone.app.core.util.Constants
import com.unpostpone.app.domain.model.BlockedApp
import com.unpostpone.app.domain.repository.OnboardingPreferences
import com.unpostpone.app.domain.repository.ThemePreferences
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
    private val getBlockedApps: GetBlockedAppsUseCase,
    private val addBlockedApp: AddBlockedAppUseCase,
    private val setAppEnabled: SetAppEnabledUseCase,
    private val onboardingPreferences: OnboardingPreferences,
    private val themePreferences: ThemePreferences,
) : ViewModel() {

    val currentTheme: StateFlow<ThemeMode> = themePreferences.current

    private val _uiState = MutableStateFlow(SettingsUiState(isLoading = true))
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observeBlockedApps()
    }

    fun toggleApp(packageName: String, isEnabled: Boolean) {
        viewModelScope.launch {
            setAppEnabled(packageName, isEnabled)
        }
    }

    fun setTheme(mode: ThemeMode) {
        themePreferences.setTheme(mode)
    }

    fun replayOnboarding() {
        onboardingPreferences.resetOnboarding()
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
