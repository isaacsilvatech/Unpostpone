package com.unpostpone.app.core.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import java.util.Locale


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
        javaLocale = Locale.forLanguageTag("pt-BR"),
        nativeName = "Português (Brasil)",
        displayName = "Português (Brasil)",
    ),
    ;

    companion object {
        fun fromTag(tag: String?): SupportedLanguage =
            entries.firstOrNull { it.tag == tag } ?: SystemDefault

        val pickable: List<SupportedLanguage> = listOf(English, PortugueseBrazil)
    }
}

val LocalAppLocale = staticCompositionLocalOf { SupportedLanguage.SystemDefault }

object AppLocale {
    val current: SupportedLanguage
        @Composable
        @ReadOnlyComposable
        get() = LocalAppLocale.current

    val formatting: Locale
        @Composable
        @ReadOnlyComposable
        get() = LocalAppLocale.current.javaLocale
}
