package com.unpostpone.app.core.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.unpostpone.app.data.locale.LanguageManagerImpl

/**
 * The single wrapper that should sit at the very top of the Compose tree
 * (inside the activity, above the NavHost). It:
 *   1. Subscribes to the LanguageManager's current language
 *   2. Provides it via [LocalAppLocale] for composables that want to react
 *      without waiting for the activity recreation
 *
 * For most UI, this is NOT necessary — `stringResource(R.string.x)` reads
 * from the active `Configuration`'s locale, which Android updates when
 * `AppCompatDelegate.setApplicationLocales()` is called. This wrapper is
 * for the rare case where a composable needs to read the language
 * directly (e.g. to show a checkmark in a language picker without a
 * recreation lag).
 *
 * The `LanguageManager` is passed in from `MainActivity` (where Hilt
 * has already injected it) to keep this wrapper free of Hilt plumbing
 * — it has no `hiltViewModel` / `hiltInject` calls inside.
 */
@Composable
fun WithAppLocale(
    languageManager: LanguageManagerImpl,
    content: @Composable () -> Unit,
) {
    val language by languageManager.current.collectAsState()
    CompositionLocalProvider(LocalAppLocale provides language) {
        content()
    }
}
