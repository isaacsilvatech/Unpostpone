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

    /**
     * Live read of the active language so the picker can show a checkmark
     * even before the activity recreation propagates the new strings.
     * Kept separate from [uiState] so language changes don't churn the
     * blocked-apps list for the spinner/loading state.
     */
    val currentLanguage: StateFlow<SupportedLanguage> = languageManager.current

    private val _uiState = MutableStateFlow(SettingsUiState(isLoading = true))
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observeBlockedApps()
    }

    fun setLanguage(language: SupportedLanguage) {
        languageManager.setLanguage(language)
    }

    /**
     * Flip the on/off state of a single blocked-app row. The row must
     * already exist (seeded on first run). We use [SetAppEnabledUseCase]
     * (a targeted UPDATE) instead of [AddBlockedAppUseCase] (REPLACE) so
     * the `addedAt` timestamp is preserved.
     */
    fun toggleApp(packageName: String, isEnabled: Boolean) {
        viewModelScope.launch {
            setAppEnabled(packageName, isEnabled)
        }
    }

    private fun observeBlockedApps() {
        viewModelScope.launch {
            // Seed the default list once. We insert with isEnabled = false so
            // the user explicitly opts in to blocking each app — nothing
            // changes at runtime until they tap a switch.
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
            // Now stream the live list to the UI.
            getBlockedApps().collect { apps ->
                _uiState.update { it.copy(blockedApps = apps, isLoading = false) }
            }
        }
    }
}
