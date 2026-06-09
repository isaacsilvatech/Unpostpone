package com.unpostpone.app.core.locale

import kotlinx.coroutines.flow.StateFlow

interface LanguageManager {
    val current: StateFlow<SupportedLanguage>
    fun setLanguage(language: SupportedLanguage)
}
