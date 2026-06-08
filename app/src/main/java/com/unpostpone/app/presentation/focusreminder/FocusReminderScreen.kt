package com.unpostpone.app.presentation.focusreminder

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.unpostpone.app.ui.components.BrandMark
import com.unpostpone.app.ui.components.BrandMarkMode
import com.unpostpone.app.ui.theme.Dimens
import com.unpostpone.app.ui.theme.UnpostponeTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

// ════════════════════════════════════════════════════════════════════════════
//  Focus Reminder
//
//  Triggered by the AccessibilityService when the user opens a blocked app.
//  Not a "blocker" — a "reminder". The user can always proceed. The screen
//  exists to add a moment of awareness between impulse and action.
//
//  Flow:
//    1. Show the brand mark with gentle floating motion + a motivational
//       message. Allow 5 seconds of pure read-time.
//    2. Reveal the two actions: "Stay focused" (primary) and "Continue" (text).
//
//  The mark is the icon's "friendlier variation" — same shapes, no aggressive
//  fills, no red, no alarm clock. The C/arc stays open, the leaf stays small,
//  the hands stay short. The product is asking, not telling.
// ════════════════════════════════════════════════════════════════════════════

@HiltViewModel
class FocusReminderViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(
        FocusReminderUiState(
            message = pickMessage(0),
        ),
    )
    val state: StateFlow<FocusReminderUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            // Show the message for 5 seconds, then reveal the actions.
            delay(REFLECTION_DURATION_MS)
            _state.update { it.copy(actionsVisible = true) }
        }
    }

    fun onStayFocused() = _state.update { it.copy(outcome = FocusOutcome.StayFocused) }
    fun onContinue()    = _state.update { it.copy(outcome = FocusOutcome.Continue) }

    private fun pickMessage(seed: Int): String = MESSAGES[seed % MESSAGES.size]

    companion object {
        const val REFLECTION_DURATION_MS = 5_000L
        private val MESSAGES = listOf(
            "Small progress every day creates meaningful results.",
            "Your future goals deserve your attention right now.",
            "Consistency builds momentum — one moment at a time.",
            "Focus on what matters most today.",
            "The work you do now is the work that compounds.",
        )
    }
}

data class FocusReminderUiState(
    val message: String,
    val actionsVisible: Boolean = false,
    val outcome: FocusOutcome? = null,
)

enum class FocusOutcome { StayFocused, Continue }

@Composable
fun FocusReminderScreen(
    onStayFocused: () -> Unit,
    onContinue: () -> Unit,
    viewModel: FocusReminderViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Gentle floating motion for the mark: 4dp vertical drift over 3.2s,
    // soft sine curve, no overshoot.
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val floatY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "floatY",
    )
    val scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "scale",
    )

    LaunchedEffect(state.outcome) {
        when (state.outcome) {
            FocusOutcome.StayFocused -> onStayFocused()
            FocusOutcome.Continue    -> onContinue()
            null -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Dimens.SpacingXXL, vertical = Dimens.SpacingScreen),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Spacer(Modifier.height(Dimens.SpacingHuge))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .graphicsLayer { translationY = -floatY }
                    .scale(scale),
            ) {
                BrandMark(
                    progress = 1f,
                    size = 160.dp,
                    mode = BrandMarkMode.Hero,
                    arcColor = MaterialTheme.colorScheme.onBackground,
                    handColor = MaterialTheme.colorScheme.tertiary,
                    leafColor = UnpostponeTheme.semantic.success,
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = state.message,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(Dimens.SpacingL))
                Text(
                    text = "Take a moment.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }

            // Actions — fade in after the reflection period
            androidx.compose.animation.AnimatedVisibility(
                visible = state.actionsVisible,
                enter = androidx.compose.animation.fadeIn(
                    animationSpec = tween(durationMillis = 500),
                ),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Button(
                        onClick = viewModel::onStayFocused,
                        modifier = Modifier.fillMaxWidth(),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary,
                        ),
                    ) {
                        Text(
                            text = "Stay focused",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(vertical = Dimens.SpacingS),
                        )
                    }
                    Spacer(Modifier.height(Dimens.SpacingM))
                    TextButton(
                        onClick = viewModel::onContinue,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "Continue",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}
