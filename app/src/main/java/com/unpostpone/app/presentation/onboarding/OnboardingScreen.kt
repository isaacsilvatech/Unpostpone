package com.unpostpone.app.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.unpostpone.app.R
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

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingPreferences: OnboardingPreferences,
) : androidx.lifecycle.ViewModel() {

    private val _state = MutableStateFlow(OnboardingUiState())
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    fun onPageChanged(index: Int) {
        _state.value = _state.value.copy(currentPage = index)
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
    val pagerState = rememberPagerState(pageCount = { 4 })
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.completed) {
        if (state.completed) onOnboardingComplete()
    }
    LaunchedEffect(pagerState.currentPage) {
        viewModel.onPageChanged(pagerState.currentPage)
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth().weight(1f),
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
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        viewModel.completeOnboarding()
                    }
                },
                onLearnMore = { scope.launch { pagerState.animateScrollToPage(2) } },
            )
        }
    }
}

@Composable
private fun WelcomePage() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        BrandMark(
            progress = 1f, size = 180.dp, mode = BrandMarkMode.Hero,
            arcColor = MaterialTheme.colorScheme.onBackground,
            handColor = MaterialTheme.colorScheme.tertiary,
            leafColor = UnpostponeTheme.semantic.success,
        )
        Spacer(Modifier.height(Dimens.SpacingHuge))
        Text(stringResource(R.string.onboarding_welcome_title),
            style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Center)
        Spacer(Modifier.height(Dimens.SpacingL))
        Text(stringResource(R.string.onboarding_welcome_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Dimens.SpacingL))
    }
}

@Composable
private fun FeaturesPage() {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Text(stringResource(R.string.onboarding_features_title),
            style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(Dimens.SpacingS))
        Text(stringResource(R.string.onboarding_features_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(Dimens.SpacingHuge))
        FeatureCard(
            Icons.Default.Timer,
            stringResource(R.string.onboarding_feature_focus_title),
            stringResource(R.string.onboarding_feature_focus_body),
        )
        Spacer(Modifier.height(Dimens.SpacingL))
        FeatureCard(
            Icons.Default.Flag,
            stringResource(R.string.onboarding_feature_goals_title),
            stringResource(R.string.onboarding_feature_goals_body),
        )
        Spacer(Modifier.height(Dimens.SpacingL))
        FeatureCard(
            Icons.Default.BarChart,
            stringResource(R.string.onboarding_feature_insights_title),
            stringResource(R.string.onboarding_feature_insights_body),
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
            modifier = Modifier.fillMaxWidth().padding(Dimens.CardPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.size(Dimens.SpacingL))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(Dimens.SpacingXS))
                Text(body, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun PrivacyPage() {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Text(stringResource(R.string.onboarding_privacy_title),
            style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(Dimens.SpacingS))
        Text(stringResource(R.string.onboarding_privacy_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(Dimens.SpacingHuge))
        PrivacyCard(
            Icons.Default.TrackChanges,
            stringResource(R.string.onboarding_privacy_collect_title),
            stringResource(R.string.onboarding_privacy_collect_body),
            MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(Dimens.SpacingL))
        PrivacyCard(
            Icons.Default.PrivacyTip,
            stringResource(R.string.onboarding_privacy_no_collect_title),
            stringResource(R.string.onboarding_privacy_no_collect_body),
            UnpostponeTheme.semantic.success,
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
            modifier = Modifier.fillMaxWidth().padding(Dimens.CardPadding),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape)
                    .background(accent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, null, tint = accent, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.size(Dimens.SpacingL))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(Dimens.SpacingXS))
                Text(body, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun PermissionsPage(
    onRequestAccessibilityPermission: () -> Unit,
    onRequestUsageAccessPermission: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Text(stringResource(R.string.onboarding_permissions_title),
            style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(Dimens.SpacingS))
        Text(stringResource(R.string.onboarding_permissions_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(Dimens.SpacingHuge))
        PermissionCard(
            Icons.Default.Accessibility,
            stringResource(R.string.perm_accessibility_title),
            stringResource(R.string.perm_accessibility_body),
            onCta = onRequestAccessibilityPermission,
        )
        Spacer(Modifier.height(Dimens.SpacingL))
        PermissionCard(
            Icons.Default.Timer,
            stringResource(R.string.perm_usage_title),
            stringResource(R.string.perm_usage_body),
            onCta = onRequestUsageAccessPermission,
        )
        Spacer(Modifier.height(Dimens.SpacingL))
        PermissionCard(
            Icons.Default.TrackChanges,
            stringResource(R.string.perm_notifications_title),
            stringResource(R.string.perm_notifications_body),
            onCta = onRequestNotificationPermission,
        )
    }
}

@Composable
private fun PermissionCard(
    icon: ImageVector, title: String, body: String, onCta: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(Dimens.CardPadding)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(icon, null, tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.size(Dimens.SpacingM))
                Text(title, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(Modifier.height(Dimens.SpacingM))
            Text(body, style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(Dimens.SpacingM))
            TextButton(onClick = onCta,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)) {
                Text(stringResource(R.string.perm_enable), fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun OnboardingBottomBar(
    currentPage: Int,
    pagerState: PagerState,
    onContinue: () -> Unit,
    onLearnMore: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)
            .padding(horizontal = Dimens.ScreenGutter, vertical = Dimens.SpacingXL),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = Dimens.SpacingXL),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(4) { index ->
                val isActive = index == currentPage
                val width = if (isActive) 24.dp else 8.dp
                val a11yLabel = stringResource(R.string.onboarding_page_indicator, index + 1, 4)
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(width = width, height = 8.dp)
                        .clip(CircleShape)
                        .background(if (isActive) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outlineVariant)
                        .semantics { contentDescription = a11yLabel },
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onLearnMore) {
                Text(stringResource(R.string.action_learn_more),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                    text = if (currentPage == 3) stringResource(R.string.action_get_started)
                           else stringResource(R.string.action_continue),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = Dimens.SpacingM),
                )
            }
        }
    }
}
