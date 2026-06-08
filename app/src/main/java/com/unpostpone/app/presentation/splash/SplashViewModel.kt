package com.unpostpone.app.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unpostpone.app.domain.repository.OnboardingPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Drives the splash animation and decides where to go next.
 *
 * The animation progresses from 0 → 1 over [ANIMATION_DURATION_MS].
 * At the end we consult [OnboardingPreferences.hasCompletedOnboarding]:
 *   • first launch  → Onboarding
 *   • subsequent    → Dashboard
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val onboardingPreferences: OnboardingPreferences,
) : ViewModel() {

    private val _state = MutableStateFlow(SplashUiState())
    val state: StateFlow<SplashUiState> = _state.asStateFlow()

    init {
        playAnimation()
    }

    private fun playAnimation() {
        viewModelScope.launch {
            // Animate master progress 0 → 1 in 2.3 s using the
            // EmphasizedEasing, then settle for a moment before navigating.
            val steps = 60
            val perStep = ANIMATION_DURATION_MS / steps
            for (i in 1..steps) {
                delay(perStep)
                _state.update { it.copy(animationProgress = i / steps.toFloat()) }
            }
            // Small hold so the final composition reads
            delay(300L)
            val firstLaunch = !onboardingPreferences.hasCompletedOnboarding()
            _state.update {
                it.copy(
                    animationProgress = 1f,
                    shouldNavigate = true,
                    target = if (firstLaunch) SplashTarget.Onboarding else SplashTarget.Dashboard,
                )
            }
        }
    }

    companion object {
        const val ANIMATION_DURATION_MS = 2300L
    }
}

data class SplashUiState(
    val animationProgress: Float = 0f,
    val shouldNavigate: Boolean = false,
    val target: SplashTarget = SplashTarget.Onboarding,
)

enum class SplashTarget { Onboarding, Dashboard }
