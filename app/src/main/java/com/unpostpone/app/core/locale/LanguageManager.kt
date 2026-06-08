package com.unpostpone.app.core.locale

import kotlinx.coroutines.flow.StateFlow

/**
 * Owns the user's chosen language and the system-side effect that applies it.
 *
 *  • `current`     — a hot StateFlow of the active language (single source of truth)
 *  • `setLanguage` — updates the StateFlow AND calls
 *                    `AppCompatDelegate.setApplicationLocales()` so Android
 *                    updates the configuration. The configuration change
 *                    recreates the activity, which re-reads all `stringResource`
 *                    calls with the new locale.
 *
 * The StateFlow lets composables that need fine-grained control (a
 * language picker that updates its checkmark without waiting for the
 * activity recreation) observe the change immediately. Most UI just uses
 * `stringResource`, which picks up the change on activity recreation.
 */
interface LanguageManager {
    val current: StateFlow<SupportedLanguage>
    fun setLanguage(language: SupportedLanguage)
}
