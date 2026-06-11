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

    private val _hasAutoEnabledOnce = MutableStateFlow(
        sharedPreferences.getBoolean(KEY_HAS_AUTO_ENABLED_ONCE, false)
    )

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        when (key) {
            KEY_BLOCKING_ENABLED ->
                _isBlockingEnabled.value = prefs.getBoolean(KEY_BLOCKING_ENABLED, false)
            KEY_HAS_AUTO_ENABLED_ONCE ->
                _hasAutoEnabledOnce.value = prefs.getBoolean(KEY_HAS_AUTO_ENABLED_ONCE, false)
        }
    }

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override val isBlockingEnabled = _isBlockingEnabled.asStateFlow()

    override val hasAutoEnabledOnce = _hasAutoEnabledOnce.asStateFlow()

    override suspend fun setBlockingEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_BLOCKING_ENABLED, enabled)
            .apply()
        _isBlockingEnabled.value = enabled
    }

    override suspend fun markAutoEnabled() {
        sharedPreferences.edit()
            .putBoolean(KEY_HAS_AUTO_ENABLED_ONCE, true)
            .apply()
        _hasAutoEnabledOnce.value = true
    }

    private companion object {
        const val KEY_BLOCKING_ENABLED = "unpostpone.blocking.enabled"
        const val KEY_HAS_AUTO_ENABLED_ONCE = "unpostpone.blocking.hasAutoEnabledOnce"
    }
}
