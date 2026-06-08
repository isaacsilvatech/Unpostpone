package com.unpostpone.app.core.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import java.util.Locale

/**
 * The locales Unpostpone ships with.
 *
 *  • `displayName`     — the name the user sees in the language picker
 *  • `nativeName`      — the name in its OWN language ("Português", not "Portuguese")
 *  • `tag`             — BCP-47 tag used by Android resources
 *  • `javaLocale`      — the `java.util.Locale` for `DateTimeFormatter` / `NumberFormat`
 *
 * To add a language:
 *   1. Add an entry here (and `nativeName` in its own script).
 *   2. Create `res/values-<tag>/<feature>_strings.xml` mirror files.
 *   3. Add any locale-specific plural rules in the resources (not in code).
 */
enum class SupportedLanguage(
    val tag: String,
    val javaLocale: Locale,
    val nativeName: String,
    val displayName: String,
) {
    SystemDefault(
        tag = "",
        javaLocale = Locale.getDefault(),
        nativeName = "System default",
        displayName = "System default",
    ),
    English(
        tag = "en",
        javaLocale = Locale.ENGLISH,
        nativeName = "English",
        displayName = "English",
    ),
    PortugueseBrazil(
        tag = "pt-BR",
        javaLocale = Locale("pt", "BR"),
        nativeName = "Português (Brasil)",
        displayName = "Português (Brasil)",
    ),
    ;

    companion object {
        fun fromTag(tag: String?): SupportedLanguage =
            entries.firstOrNull { it.tag == tag } ?: SystemDefault

        /** Languages the user can actually pick (excludes the system default). */
        val pickable: List<SupportedLanguage> = listOf(English, PortugueseBrazil)
    }
}

/**
 * Composition-local for the active language. Updated by the top-level
 * `UnpostponeRoot` so all composables can react to language switches
 * without a full activity recreation.
 *
 * Read it via `LocalAppLocale.current`. Defaults to `SystemDefault` so
 * previews and unit tests never crash.
 */
val LocalAppLocale = staticCompositionLocalOf { SupportedLanguage.SystemDefault }

object AppLocale {
    val current: SupportedLanguage
        @Composable
        @ReadOnlyComposable
        get() = LocalAppLocale.current

    /**
     * The `Locale` to use for formatting (dates, numbers, plurals).
     * For `SystemDefault` we return `Locale.getDefault()`.
     */
    val formatting: Locale
        @Composable
        @ReadOnlyComposable
        get() = LocalAppLocale.current.javaLocale
}
