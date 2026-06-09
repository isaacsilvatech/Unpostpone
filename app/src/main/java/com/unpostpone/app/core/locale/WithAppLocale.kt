package com.unpostpone.app.core.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.unpostpone.app.data.locale.LanguageManagerImpl

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
