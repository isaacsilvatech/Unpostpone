package com.unpostpone.app.data.repository

import android.content.SharedPreferences
import com.unpostpone.app.domain.repository.OnboardingPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingPreferencesImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) : OnboardingPreferences {

    override fun hasCompletedOnboarding(): Boolean =
        sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETED, false)

    override fun markOnboardingCompleted() {
        sharedPreferences.edit()
            .putBoolean(KEY_ONBOARDING_COMPLETED, true)
            .apply()
    }

    override fun resetOnboarding() {
        sharedPreferences.edit()
            .remove(KEY_ONBOARDING_COMPLETED)
            .apply()
    }

    private companion object {
        const val KEY_ONBOARDING_COMPLETED = "unpostpone.onboarding.completed"
    }
}
