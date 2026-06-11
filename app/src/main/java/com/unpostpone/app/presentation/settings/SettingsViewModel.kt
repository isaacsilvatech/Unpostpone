package com.unpostpone.app.presentation.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unpostpone.app.core.theme.ThemeMode
import com.unpostpone.app.core.util.AppInstalledChecker
import com.unpostpone.app.core.util.Constants
import com.unpostpone.app.domain.model.BlockedApp
import com.unpostpone.app.domain.repository.OnboardingPreferences
import com.unpostpone.app.domain.repository.ThemePreferences
import com.unpostpone.app.domain.repository.UnlockDurationPreferences
import com.unpostpone.app.domain.usecase.blockedapp.AddBlockedAppUseCase
import com.unpostpone.app.domain.usecase.blockedapp.GetBlockedAppsUseCase
import com.unpostpone.app.domain.usecase.blockedapp.SetAppEnabledUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getBlockedApps: GetBlockedAppsUseCase,
    private val addBlockedApp: AddBlockedAppUseCase,
    private val setAppEnabled: SetAppEnabledUseCase,
    private val onboardingPreferences: OnboardingPreferences,
    private val themePreferences: ThemePreferences,
    private val unlockDurationPreferences: UnlockDurationPreferences,
) : ViewModel() {

    val currentTheme: StateFlow<ThemeMode> = themePreferences.current
    val currentDuration: StateFlow<Int> = unlockDurationPreferences.current

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

    fun setDuration(minutes: Int) {
        unlockDurationPreferences.setDuration(minutes)
    }

    fun replayOnboarding() {
        onboardingPreferences.resetOnboarding()
    }

    private fun observeBlockedApps() {
        viewModelScope.launch {
            val current = getBlockedApps().first()
            val knownPackages = current.map { it.packageName }.toSet()
            val missing = Constants.DEFAULT_BLOCKED_APPS.filter { it.packageName !in knownPackages }
            missing.forEach { def ->
                addBlockedApp(
                    BlockedApp(
                        packageName = def.packageName,
                        displayName = def.displayName,
                        isEnabled = false,
                    )
                )
            }
            getBlockedApps().collect { apps ->
                val visible = apps.filter { AppInstalledChecker.isInstalled(context, it.packageName) }
                _uiState.update { it.copy(blockedApps = visible, isLoading = false) }
            }
        }
    }
}
