package com.unpostpone.app.presentation.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.unpostpone.app.R
import com.unpostpone.app.presentation.navigation.Screen
import com.unpostpone.app.ui.components.BrandMark
import com.unpostpone.app.ui.components.BrandMarkMode
import com.unpostpone.app.ui.theme.Dimens
import com.unpostpone.app.ui.theme.UnpostponeTheme

/**
 * The launch experience.
 *
 * 1. Plays the brand mark reveal (clock arc → center → hands → leaf)
 * 2. Fades in the wordmark
 * 3. Decides where to navigate (Onboarding if first launch, Dashboard otherwise)
 * 4. Crossfades to the next screen
 *
 * The whole screen is edge-to-edge on the brand cream. No status-bar tinting,
 * no splash-image, no Compose Activity splash — the live animated mark IS
 * the splash.
 */
@Composable
fun SplashScreen(
    onNavigateTo: (target: SplashTarget) -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.shouldNavigate) {
        if (state.shouldNavigate) onNavigateTo(state.target)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = Dimens.ScreenGutter),
        ) {
            BrandMark(
                progress = state.animationProgress,
                size = 240.dp,
                mode = BrandMarkMode.Splash,
                arcColor = MaterialTheme.colorScheme.onBackground,
                handColor = MaterialTheme.colorScheme.tertiary,
                leafColor = UnpostponeTheme.semantic.success,
            )

            Spacer(Modifier.height(Dimens.SpacingXXL))

            AnimatedVisibility(
                visible = state.animationProgress > 0.85f,
                enter = fadeIn(animationSpec = androidx.compose.animation.core.tween(400)),
                exit  = fadeOut(animationSpec = androidx.compose.animation.core.tween(200)),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(Dimens.SpacingS))
                    Text(
                        text = stringResource(R.string.app_tagline),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
