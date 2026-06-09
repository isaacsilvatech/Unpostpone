package com.unpostpone.app.data.repository

import android.content.SharedPreferences
import com.unpostpone.app.domain.repository.BlockingPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlockingPreferencesImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) : BlockingPreferences {

    private val _isBlockingEnabled = MutableStateFlow(
        sharedPreferences.getBoolean(KEY_BLOCKING_ENABLED, false)
    )

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        if (key == KEY_BLOCKING_ENABLED) {
            _isBlockingEnabled.value = prefs.getBoolean(KEY_BLOCKING_ENABLED, false)
        }
    }

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override val isBlockingEnabled = _isBlockingEnabled.asStateFlow()

    override suspend fun setBlockingEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_BLOCKING_ENABLED, enabled)
            .apply()
        // The change listener will update the flow, but we also set it directly
        // so the in-memory value is consistent even if the listener fires late
        // (apply() is async). Cheap idempotent update.
        _isBlockingEnabled.value = enabled
    }

    private companion object {
        const val KEY_BLOCKING_ENABLED = "unpostpone.blocking.enabled"
    }
}
