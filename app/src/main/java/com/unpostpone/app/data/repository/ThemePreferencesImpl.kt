package com.unpostpone.app.data.repository

import android.content.SharedPreferences
import com.unpostpone.app.core.theme.ThemeMode
import com.unpostpone.app.domain.repository.ThemePreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemePreferencesImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) : ThemePreferences {

    private val _current = MutableStateFlow(readPersisted())
    override val current: StateFlow<ThemeMode> = _current.asStateFlow()

    override fun setTheme(mode: ThemeMode) {
        if (mode == _current.value) return
        sharedPreferences.edit()
            .putString(KEY_THEME_MODE, mode.name)
            .apply()
        _current.value = mode
    }

    private fun readPersisted(): ThemeMode =
        ThemeMode.fromName(sharedPreferences.getString(KEY_THEME_MODE, null))

    private companion object {
        const val KEY_THEME_MODE = "unpostpone.theme.mode"
    }
}
