package com.unpostpone.app.data.locale

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.unpostpone.app.core.locale.LanguageManager
import com.unpostpone.app.core.locale.SupportedLanguage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageManagerImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) : LanguageManager {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _current = MutableStateFlow(readPersisted())
    override val current: StateFlow<SupportedLanguage> = _current.asStateFlow()

    override fun setLanguage(language: SupportedLanguage) {
        if (language == _current.value) return
        persist(language)
        _current.value = language
        applyToAppCompat(language)
    }

    /**
     * Called once from `MainActivity.onCreate` to push the persisted
     * language into the Activity's `AppCompatDelegate` before the first
     * composition. This means a returning user sees the right strings
     * immediately, with no flash of English-then-Portuguese.
     */
    fun applyPersistedToAppCompat() {
        applyToAppCompat(_current.value)
    }

    // ── internals ─────────────────────────────────────────────────────

    private fun readPersisted(): SupportedLanguage {
        val tag = sharedPreferences.getString(KEY_LOCALE_TAG, null)
        return SupportedLanguage.fromTag(tag)
    }

    private fun persist(language: SupportedLanguage) {
        scope.launch {
            sharedPreferences.edit().apply {
                if (language == SupportedLanguage.SystemDefault) {
                    remove(KEY_LOCALE_TAG)
                } else {
                    putString(KEY_LOCALE_TAG, language.tag)
                }
            }.apply()
        }
    }

    private fun applyToAppCompat(language: SupportedLanguage) {
        val list = if (language == SupportedLanguage.SystemDefault) {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(language.tag)
        }
        // setApplicationLocales handles both API 33+ (native) and API 29-32
        // (via the AppCompat backport that we're now depending on).
        AppCompatDelegate.setApplicationLocales(list)
    }

    private companion object {
        const val KEY_LOCALE_TAG = "unpostpone.locale.tag"
    }
}
