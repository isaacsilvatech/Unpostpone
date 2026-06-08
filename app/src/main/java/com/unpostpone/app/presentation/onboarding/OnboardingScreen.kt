package com.unpostpone.app.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.unpostpone.app.domain.repository.OnboardingPreferences
import com.unpostpone.app.ui.components.BrandMark
import com.unpostpone.app.ui.components.BrandMarkMode
import com.unpostpone.app.ui.theme.Dimens
import com.unpostpone.app.ui.theme.UnpostponeTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// ════════════════════════════════════════════════════════════════════════════
//  Welcome Onboarding
//
//  A 4-page HorizontalPager:
//    0 — Welcome         : brand mark, title, subtitle, "Get Started"
//    1 — Features        : 3 cards (Focus, Goals, Insights) on cream
//    2 — Privacy         : 2 cards (what we collect / what we don't)
//    3 — Permissions     : 3 cards (Accessibility, Usage Access, Notifications)
//
//  The user swipes between pages. The "Continue" pill advances; the "Learn
//  More" text button is always available for users who want a deeper
//  explanation of a particular permission before granting it.
// ════════════════════════════════════════════════════════════════════════════

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingPreferences: OnboardingPreferences,
) : androidx.lifecycle.ViewModel() {

    private val _state = MutableStateFlow(OnboardingUiState())
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    fun onPageChanged(index: Int) {
        _state.value = _state.value.copy(currentPage = index)
    }

    fun onContinueClicked() {
        // No-op in the screen; the screen drives page advancement via the pager
    }

    fun completeOnboarding() {
        onboardingPreferences.markOnboardingCompleted()
        _state.value = _state.value.copy(completed = true)
    }
}

data class OnboardingUiState(
    val currentPage: Int = 0,
    val completed: Boolean = false,
)

@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit,
    onRequestAccessibilityPermission: () -> Unit,
    onRequestUsageAccessPermission: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.completed) {
        if (state.completed) onOnboardingComplete()
    }

    val pagerState = rememberPagerState(pageCount = { 4 })
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        viewModel.onPageChanged(pagerState.currentPage)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = Dimens.ScreenGutter),
                pageSpacing = Dimens.SpacingL,
            ) { page ->
                when (page) {
                    0 -> WelcomePage()
                    1 -> FeaturesPage()
                    2 -> PrivacyPage()
                    3 -> PermissionsPage(
                        onRequestAccessibilityPermission = onRequestAccessibilityPermission,
                        onRequestUsageAccessPermission = onRequestUsageAccessPermission,
                        onRequestNotificationPermission = onRequestNotificationPermission,
                    )
                }
            }

            OnboardingBottomBar(
                currentPage = pagerState.currentPage,
                pagerState = pagerState,
                onContinue = {
                    if (pagerState.currentPage < 3) {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        viewModel.completeOnboarding()
                    }
                },
                onLearnMore = {
                    // Open the privacy/permissions page directly
                    scope.launch { pagerState.animateScrollToPage(2) }
                },
            )
        }
    }
}

// ── Page 0 — Welcome ──────────────────────────────────────────────────────

@Composable
private fun WelcomePage() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        BrandMark(
            progress = 1f,
            size = 180.dp,
            mode = BrandMarkMode.Hero,
            arcColor = MaterialTheme.colorScheme.onBackground,
            handColor = MaterialTheme.colorScheme.tertiary,
            leafColor = UnpostponeTheme.semantic.success,
        )
        Spacer(Modifier.height(Dimens.SpacingHuge))
        Text(
            text = "Take control of your time",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Dimens.SpacingL))
        Text(
            text = "Unpostpone helps you stay focused, build healthy digital habits, " +
                   "and spend time on what matters most — without the noise.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Dimens.SpacingL),
        )
    }
}

// ── Page 1 — Features ─────────────────────────────────────────────────────

@Composable
private fun FeaturesPage() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Designed for focus",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(Dimens.SpacingS))
        Text(
            text = "Three tools, one calm interface.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(Dimens.SpacingHuge))

        FeatureCard(
            icon = Icons.Default.Timer,
            title = "Focus Sessions",
            body = "Start a session and stay with one activity. " +
                   "We'll hold the line for you.",
        )
        Spacer(Modifier.height(Dimens.SpacingL))
        FeatureCard(
            icon = Icons.Default.Flag,
            title = "Daily Goals",
            body = "Pick the work that matters today. Unpostpone keeps it visible.",
        )
        Spacer(Modifier.height(Dimens.SpacingL))
        FeatureCard(
            icon = Icons.Default.BarChart,
            title = "Gentle Insights",
            body = "See your week at a glance. No streaks to defend, no scores to chase.",
        )
    }
}

@Composable
private fun FeatureCard(icon: ImageVector, title: String, body: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.CardPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp),
                )
            }
            Spacer(Modifier.size(Dimens.SpacingL))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(Dimens.SpacingXS))
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// ── Page 2 — Privacy ──────────────────────────────────────────────────────

@Composable
private fun PrivacyPage() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Privacy first",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(Dimens.SpacingS))
        Text(
            text = "What stays on your device, and what never leaves it.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(Dimens.SpacingHuge))

        PrivacyCard(
            icon = Icons.Default.TrackChanges,
            title = "What we use",
            body = "Only the data needed to detect which app you're using. " +
                   "Stored locally, on your phone, never uploaded.",
            accent = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(Dimens.SpacingL))
        PrivacyCard(
            icon = Icons.Default.PrivacyTip,
            title = "What we don't collect",
            body = "No analytics, no tracking, no ads, no account required. " +
                   "No content from your apps. No screenshots. No keystrokes.",
            accent = UnpostponeTheme.semantic.success,
        )
    }
}

@Composable
private fun PrivacyCard(icon: ImageVector, title: String, body: String, accent: androidx.compose.ui.graphics.Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.CardPadding),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.size(Dimens.SpacingL))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(Dimens.SpacingXS))
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// ── Page 3 — Permissions ──────────────────────────────────────────────────

@Composable
private fun PermissionsPage(
    onRequestAccessibilityPermission: () -> Unit,
    onRequestUsageAccessPermission: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "A few permissions",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(Dimens.SpacingS))
        Text(
            text = "Unpostpone only asks for what's needed to do its job.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(Dimens.SpacingHuge))

        PermissionCard(
            icon = Icons.Default.Accessibility,
            title = "Accessibility",
            body = "Required to detect when you open a distracting app and " +
                   "show you a calm reminder. We do not read screen content.",
            cta = "Enable",
            onCta = onRequestAccessibilityPermission,
        )
        Spacer(Modifier.height(Dimens.SpacingL))
        PermissionCard(
            icon = Icons.Default.Timer,
            title = "Usage access",
            body = "Used to count time spent in apps. Stored locally, never sent anywhere.",
            cta = "Enable",
            onCta = onRequestUsageAccessPermission,
        )
        Spacer(Modifier.height(Dimens.SpacingL))
        PermissionCard(
            icon = Icons.Default.TrackChanges,
            title = "Notifications",
            body = "Optional. Used to surface focus-session start and end reminders.",
            cta = "Enable",
            onCta = onRequestNotificationPermission,
        )
    }
}

@Composable
private fun PermissionCard(
    icon: ImageVector,
    title: String,
    body: String,
    cta: String,
    onCta: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.CardPadding),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Spacer(Modifier.size(Dimens.SpacingM))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Spacer(Modifier.height(Dimens.SpacingM))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(Dimens.SpacingM))
            TextButton(
                onClick = onCta,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
            ) {
                Text(text = cta, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ── Bottom bar (page dots + actions) ──────────────────────────────────────

@Composable
private fun OnboardingBottomBar(
    currentPage: Int,
    pagerState: PagerState,
    onContinue: () -> Unit,
    onLearnMore: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = Dimens.ScreenGutter, vertical = Dimens.SpacingXL),
    ) {
        // Page dots
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimens.SpacingXL),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(4) { index ->
                val isActive = index == currentPage
                val width = if (isActive) 24.dp else 8.dp
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(width = width, height = 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (isActive) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outlineVariant
                        ),
                )
            }
        }

        // Action row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onLearnMore) {
                Text(
                    text = "Learn more",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Button(
                onClick = onContinue,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                ),
            ) {
                Text(
                    text = if (currentPage == 3) "Get started" else "Continue",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = Dimens.SpacingM),
                )
            }
        }
    }
}
