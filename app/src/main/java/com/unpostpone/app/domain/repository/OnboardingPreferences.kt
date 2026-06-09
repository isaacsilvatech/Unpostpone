package com.unpostpone.app.domain.repository

interface OnboardingPreferences {
    fun hasCompletedOnboarding(): Boolean
    fun markOnboardingCompleted()
}
