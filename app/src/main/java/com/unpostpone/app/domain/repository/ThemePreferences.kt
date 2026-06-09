package com.unpostpone.app.domain.repository

import com.unpostpone.app.core.theme.ThemeMode
import kotlinx.coroutines.flow.StateFlow

interface ThemePreferences {
    val current: StateFlow<ThemeMode>
    fun setTheme(mode: ThemeMode)
}
