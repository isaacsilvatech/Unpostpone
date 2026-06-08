package com.unpostpone.app.presentation.settings

import androidx.lifecycle.ViewModel
import com.unpostpone.app.core.locale.LanguageManager
import com.unpostpone.app.core.locale.SupportedLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val languageManager: LanguageManager,
) : ViewModel() {

    /** Live read of the active language so the picker can show a checkmark
     *  even before the activity recreation propagates the new strings. */
    val currentLanguage: StateFlow<SupportedLanguage> = languageManager.current

    fun setLanguage(language: SupportedLanguage) {
        languageManager.setLanguage(language)
    }

    // TODO (separate ticket): add replay-onboarding, blocked-apps list,
    // accessibility service enable flow.
}
