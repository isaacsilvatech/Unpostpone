package com.unpostpone.app.presentation.settings

import android.content.ComponentName
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.unpostpone.app.BuildConfig
import com.unpostpone.app.R
import com.unpostpone.app.core.theme.ThemeMode
import com.unpostpone.app.core.util.AppLabelResolver
import com.unpostpone.app.domain.model.BlockedApp
import com.unpostpone.app.presentation.dashboard.BottomNavigationBar
import com.unpostpone.app.presentation.navigation.Screen
import com.unpostpone.app.service.accessibility.UnpostponeAccessibilityService
import com.unpostpone.app.ui.theme.Dimens
import com.unpostpone.app.ui.theme.UnpostponeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val currentTheme by viewModel.currentTheme.collectAsStateWithLifecycle()
    val currentDuration by viewModel.currentDuration.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showThemePicker by remember { mutableStateOf(false) }
    var showDurationPicker by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.settings_back_cd),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
    ) { paddingValues ->
        SettingsContent(
            uiState = uiState,
            currentTheme = currentTheme,
            currentDuration = currentDuration,
            onToggleApp = viewModel::toggleApp,
            onThemeClick = { showThemePicker = true },
            onDurationClick = { showDurationPicker = true },
            onAccessibilityClick = {
                val serviceComponent = ComponentName(
                    context,
                    UnpostponeAccessibilityService::class.java,
                )
                context.startActivity(
                    Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        .putExtra(
                            Intent.EXTRA_COMPONENT_NAME,
                            serviceComponent.flattenToString(),
                        )
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            },
            onReplayOnboardingClick = {
                viewModel.replayOnboarding()
                navController.navigate(Screen.Onboarding.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = false }
                }
            },
            contentPadding = paddingValues,
        )
    }

    if (showThemePicker) {
        ThemePickerDialog(onDismiss = { showThemePicker = false })
    }

    if (showDurationPicker) {
        DurationPickerDialog(onDismiss = { showDurationPicker = false })
    }
}

// ── Inner content (preview-friendly, takes pure state + lambdas) ────────

@Composable
private fun SettingsContent(
    uiState: SettingsUiState,
    currentTheme: ThemeMode,
    currentDuration: Int,
    onToggleApp: (String, Boolean) -> Unit,
    onThemeClick: () -> Unit,
    onDurationClick: () -> Unit,
    onAccessibilityClick: () -> Unit,
    onReplayOnboardingClick: () -> Unit,
    contentPadding: PaddingValues,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(contentPadding),
        contentPadding = PaddingValues(
            horizontal = Dimens.ScreenGutter,
            vertical = Dimens.SpacingL,
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingXXL),
    ) {
        // ── Section 1: Focus ─────────────────────────────────────────
        item {
            SettingsSectionCard(
                title = stringResource(R.string.settings_section_focus),
                subtitle = stringResource(R.string.settings_section_focus_subtitle),
            ) {
                when {
                    uiState.isLoading -> {
                        SettingsLoadingRow()
                    }
                    uiState.blockedApps.isEmpty() -> {
                        SettingsEmptyRow(
                            message = stringResource(R.string.settings_empty_section),
                        )
                    }
                    else -> {
                        uiState.blockedApps.forEachIndexed { index, app ->
                            if (index > 0) SettingsRowDivider()
                            AppBlockToggleItem(
                                displayName = app.displayName,
                                packageName = app.packageName,
                                isBlocked = app.isEnabled,
                                onToggle = { onToggleApp(app.packageName, it) },
                            )
                        }
                    }
                }
                SettingsRowDivider()
                AccessibilityServiceRow(onClick = onAccessibilityClick)
            }
        }

        // ── Section 2: Preferences ───────────────────────────────────
        item {
            SettingsSectionCard(
                title = stringResource(R.string.settings_section_preferences),
            ) {
                ThemeRow(
                    current = currentTheme,
                    onClick = onThemeClick,
                )
                SettingsRowDivider()
                DurationRow(
                    currentMinutes = currentDuration,
                    onClick = onDurationClick,
                )
            }
        }

        // ── Section 3: About ─────────────────────────────────────────
        item {
            SettingsSectionCard(
                title = stringResource(R.string.settings_section_about),
            ) {
                AboutVersionRow(
                    version = BuildConfig.VERSION_NAME,
                )
                SettingsRowDivider()
                ReplayOnboardingRow(
                    onClick = onReplayOnboardingClick,
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  Section card primitives
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun SettingsSectionCard(
    title: String,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.SpacingXL, vertical = Dimens.SpacingL),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(Modifier.height(Dimens.SpacingXS))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(Dimens.SpacingM))
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
            )
            Column(content = content)
        }
    }
}

@Composable
private fun SettingsRowDivider() {
    HorizontalDivider(
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant,
    )
}

@Composable
private fun SettingsEmptyRow(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.SpacingM),
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SettingsLoadingRow() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = Dimens.SpacingL),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 2.dp,
            modifier = Modifier.size(20.dp),
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  Row composables used inside section cards
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun AppBlockToggleItem(
    displayName: String,
    packageName: String,
    isBlocked: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.SpacingM),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingM),
        ) {
            AppIcon(packageName = packageName, contentDescription = displayName)
            Text(
                text = displayName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Switch(
            checked = isBlocked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                checkedBorderColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                uncheckedBorderColor = MaterialTheme.colorScheme.outline,
            ),
        )
    }
}

@Composable
private fun AppIcon(packageName: String, contentDescription: String) {
    val drawable = AppLabelResolver.resolveIcon(LocalContext.current, packageName)
    if (drawable != null) {
        val bitmap = remember(drawable) { drawable.toBitmap().asImageBitmap() }
        Image(
            bitmap = bitmap,
            contentDescription = contentDescription,
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape),
        )
    } else {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Smartphone,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}

@Composable
private fun AccessibilityServiceRow(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = Dimens.SpacingM),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingS),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingS),
        ) {
            Icon(
                Icons.Default.Accessibility,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(Dimens.IconS),
            )
            Text(
                text = stringResource(R.string.settings_accessibility_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Text(
            text = stringResource(R.string.settings_accessibility_body),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ThemeRow(
    current: ThemeMode,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = Dimens.SpacingM),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.settings_theme_title),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = current.nativeName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun DurationRow(
    currentMinutes: Int,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = Dimens.SpacingM),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.settings_unlock_duration_title),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(R.string.settings_unlock_duration_value, currentMinutes),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun AboutVersionRow(version: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = Dimens.SpacingM),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.settings_about_version, version),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun ReplayOnboardingRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = Dimens.SpacingM),
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingM),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Default.Replay,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(Dimens.IconS),
        )
        Text(
            text = stringResource(R.string.settings_replay_onboarding),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  Previews
// ═══════════════════════════════════════════════════════════════════════════

@Preview(name = "Settings — Populated (light)", showBackground = true)
@Composable
private fun SettingsContentPreview_Populated() {
    UnpostponeTheme(darkTheme = false) {
        SettingsContent(
            uiState = SettingsUiState(
                isLoading = false,
                blockedApps = listOf(
                    BlockedApp(packageName = "com.instagram.android", displayName = "Instagram", isEnabled = true),
                    BlockedApp(packageName = "com.twitter.android",   displayName = "Twitter",   isEnabled = false),
                    BlockedApp(packageName = "com.zhiliaoapp.musically", displayName = "TikTok", isEnabled = true),
                ),
            ),
            currentTheme = ThemeMode.SystemDefault,
            currentDuration = 5,
            onToggleApp = { _, _ -> },
            onThemeClick = {},
            onDurationClick = {},
            onAccessibilityClick = {},
            onReplayOnboardingClick = {},
            contentPadding = PaddingValues(0.dp),
        )
    }
}

@Preview(name = "Settings — Empty blocked apps (light)", showBackground = true)
@Composable
private fun SettingsContentPreview_EmptyBlocked() {
    UnpostponeTheme(darkTheme = false) {
        SettingsContent(
            uiState = SettingsUiState(isLoading = false),
            currentTheme = ThemeMode.Light,
            currentDuration = 15,
            onToggleApp = { _, _ -> },
            onThemeClick = {},
            onDurationClick = {},
            onAccessibilityClick = {},
            onReplayOnboardingClick = {},
            contentPadding = PaddingValues(0.dp),
        )
    }
}

@Preview(name = "Settings — Loading (light)", showBackground = true)
@Composable
private fun SettingsContentPreview_Loading() {
    UnpostponeTheme(darkTheme = false) {
        SettingsContent(
            uiState = SettingsUiState(isLoading = true),
            currentTheme = ThemeMode.SystemDefault,
            currentDuration = 5,
            onToggleApp = { _, _ -> },
            onThemeClick = {},
            onDurationClick = {},
            onAccessibilityClick = {},
            onReplayOnboardingClick = {},
            contentPadding = PaddingValues(0.dp),
        )
    }
}

@Preview(name = "Settings — Populated (dark)", showBackground = true)
@Composable
private fun SettingsContentPreview_Populated_Dark() {
    UnpostponeTheme(darkTheme = true) {
        SettingsContent(
            uiState = SettingsUiState(
                isLoading = false,
                blockedApps = listOf(
                    BlockedApp(packageName = "com.instagram.android", displayName = "Instagram", isEnabled = true),
                    BlockedApp(packageName = "com.twitter.android",   displayName = "Twitter",   isEnabled = false),
                ),
            ),
            currentTheme = ThemeMode.Dark,
            currentDuration = 30,
            onToggleApp = { _, _ -> },
            onThemeClick = {},
            onDurationClick = {},
            onAccessibilityClick = {},
            onReplayOnboardingClick = {},
            contentPadding = PaddingValues(0.dp),
        )
    }
}
