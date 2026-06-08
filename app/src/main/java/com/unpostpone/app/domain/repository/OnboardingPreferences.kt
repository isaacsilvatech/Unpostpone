package com.unpostpone.app.domain.repository

/**
 * First-launch + onboarding-completion flag.
 *
 * Implementations should persist this across app restarts. Backed by
 * SharedPreferences in the production wiring.
 */
interface OnboardingPreferences {
    fun hasCompletedOnboarding(): Boolean
    fun markOnboardingCompleted()
}
