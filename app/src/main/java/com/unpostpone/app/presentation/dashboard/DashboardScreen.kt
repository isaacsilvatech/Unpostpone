package com.unpostpone.app.presentation.dashboard

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.unpostpone.app.R
import com.unpostpone.app.core.util.NotificationPermissionHelper
import com.unpostpone.app.domain.model.BlockedApp
import com.unpostpone.app.domain.model.Goal
import com.unpostpone.app.domain.model.Statistics
import com.unpostpone.app.presentation.dashboard.components.*
import com.unpostpone.app.presentation.navigation.Screen
import com.unpostpone.app.service.accessibility.UnpostponeAccessibilityService
import com.unpostpone.app.ui.theme.Dimens
import com.unpostpone.app.ui.theme.HeroRadius
import com.unpostpone.app.ui.theme.UnpostponeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshAccessibilityState()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !NotificationPermissionHelper.isGranted(context)
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val isEffectivelyActive =
        uiState.isBlockingActive && uiState.isAccessibilityServiceEnabled

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.dashboard_top_bar_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = stringResource(R.string.dashboard_settings_cd),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
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
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
        } else {
                DashboardContent(
                    uiState = uiState,
                    isEffectivelyActive = isEffectivelyActive,
                    onToggleBlocking = viewModel::onToggleBlocking,
                    onAddGoal = { navController.navigate(Screen.Goals.route) },
                    onOpenGoals = { navController.navigate(Screen.Goals.route) },
                    onOpenStatistics = { navController.navigate(Screen.Statistics.route) },
                    onOpenPomodoro = { navController.navigate(Screen.Pomodoro.route) },
                    contentPadding = paddingValues,
                )
        }

        uiState.error?.let { LaunchedEffect(it) { viewModel.dismissError() } }
    }

    if (uiState.showAccessibilityPrompt) {
        AlertDialog(
            onDismissRequest = viewModel::dismissAccessibilityPrompt,
            title = {
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
                        text = stringResource(R.string.dashboard_accessibility_prompt_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            },
            text = {
                Text(
                    text = stringResource(R.string.dashboard_accessibility_prompt_body),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.dismissAccessibilityPrompt()
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
                ) {
                    Text(stringResource(R.string.dashboard_accessibility_open_settings))
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissAccessibilityPrompt) {
                    Text(stringResource(R.string.dashboard_accessibility_cancel))
                }
            },
        )
    }

    if (uiState.showDisableBlockingConfirm) {
        AlertDialog(
            onDismissRequest = viewModel::dismissDisableBlockingConfirm,
            title = {
                Text(
                    text = stringResource(R.string.dashboard_disable_focus_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.dashboard_disable_focus_body),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            confirmButton = {
                TextButton(onClick = viewModel::confirmDisableBlocking) {
                    Text(stringResource(R.string.dashboard_disable_focus_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissDisableBlockingConfirm) {
                    Text(stringResource(R.string.dashboard_disable_focus_cancel))
                }
            },
        )
    }
}

@Composable
private fun DashboardContent(
    uiState: DashboardUiState,
    isEffectivelyActive: Boolean,
    onToggleBlocking: (Boolean) -> Unit,
    onAddGoal: () -> Unit,
    onOpenGoals: () -> Unit,
    onOpenStatistics: () -> Unit,
    onOpenPomodoro: () -> Unit,
    contentPadding: PaddingValues,
) {
    val todayGoal: Goal? = uiState.todayGoals.firstOrNull()
    val tools = defaultDashboardTools(
        onPomodoro = onOpenPomodoro,
        onTasks = { },
        onNotes = { },
        onGoals = onOpenGoals,
        onStatistics = onOpenStatistics,
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(contentPadding),
        contentPadding = PaddingValues(
            horizontal = Dimens.SpacingXL,
            vertical = Dimens.SpacingL,
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingXL),
    ) {
        item {
            HeroFocusCard(
                isActive = isEffectivelyActive,
                enabledBlockedAppCount = uiState.enabledBlockedAppCount,
                enabledBlockedApps = uiState.enabledBlockedApps,
                onToggle = onToggleBlocking,
            )
        }

        item { ToolsRow(tools = tools) }

        item {
            TodaysFocusCard(
                focusedMinutes = uiState.focusedMinutes,
                dailyTargetMinutes = uiState.dailyTargetMinutes,
                progressPercent = uiState.dailyGoalProgressPercent,
            )
        }

        item {
            if (todayGoal == null) {
                EmptyGoalCard(onAddGoal = onAddGoal)
            } else {
                TodayGoalCard(goal = todayGoal)
            }
        }

        item {
            InsightsRow(
                blocked = uiState.blockCount,
                attempts = uiState.unlockAttempts,
                goals = uiState.completedGoalsCount,
                focusedMinutes = uiState.focusedMinutes,
            )
        }

        item {
            MomentumCard(
                streakDays = uiState.streakDays,
                quote = stringResource(R.string.dashboard_momentum_quote),
            )
        }
    }
}

@Composable
private fun TodayGoalCard(goal: Goal) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionLabel(text = stringResource(R.string.dashboard_today_goals_title))
        Spacer(Modifier.height(Dimens.SpacingM))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(HeroRadius),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            border = BorderStroke(
                width = 1.dp,
                color = if (goal.isCompleted) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outlineVariant,
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.SpacingXXL),
            ) {
                Text(
                    text = goal.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(Dimens.SpacingS))
                Text(
                    text = stringResource(
                        R.string.dashboard_today_goal_progress,
                        goal.progressMinutes,
                        goal.targetMinutes,
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(Dimens.SpacingM))
                LinearProgressIndicator(
                    progress = { goal.progressPercent },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(Dimens.SpacingS)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
                )
            }
        }
    }
}

@Composable
private fun EmptyGoalCard(onAddGoal: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionLabel(text = stringResource(R.string.dashboard_today_goals_title))
        Spacer(Modifier.height(Dimens.SpacingM))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(HeroRadius),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            onClick = onAddGoal,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.SpacingXXL),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.dashboard_empty_goals_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(Modifier.height(Dimens.SpacingS))
                Text(
                    text = stringResource(R.string.dashboard_empty_goals_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(Dimens.SpacingM))
                TextButton(onClick = onAddGoal) {
                    Icon(
                        Icons.Default.Flag,
                        contentDescription = null,
                        modifier = Modifier.size(Dimens.IconS),
                    )
                    Spacer(Modifier.width(Dimens.SpacingS))
                    Text(
                        text = stringResource(R.string.dashboard_empty_goals_action),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
    ) {
        NavigationBarItem(
            selected = currentRoute == Screen.Dashboard.route,
            onClick = { navController.navigate(Screen.Dashboard.route) { launchSingleTop = true } },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_dashboard), style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Goals.route,
            onClick = { navController.navigate(Screen.Goals.route) { launchSingleTop = true } },
            icon = { Icon(Icons.Default.Flag, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_goals), style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Statistics.route,
            onClick = { navController.navigate(Screen.Statistics.route) { launchSingleTop = true } },
            icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_statistics), style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Settings.route,
            onClick = { navController.navigate(Screen.Settings.route) { launchSingleTop = true } },
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            label = { Text(stringResource(R.string.nav_settings), style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )
    }
}

@Preview(name = "Dashboard — Populated (light)", showBackground = true)
@Composable
private fun DashboardScreenPreview_Populated() {
    UnpostponeTheme(darkTheme = false) {
        DashboardContentPreview(
            uiState = DashboardUiState(
                isLoading = false,
                isBlockingActive = true,
                enabledBlockedAppCount = 3,
                enabledBlockedApps = listOf(
                    BlockedApp(packageName = "com.instagram.android",   displayName = "Instagram", isEnabled = true),
                    BlockedApp(packageName = "com.zhiliaoapp.musically", displayName = "TikTok",    isEnabled = true),
                    BlockedApp(packageName = "com.twitter.android",     displayName = "Twitter",   isEnabled = true),
                ),
                streakDays = 7,
                todayStatistics = Statistics(
                    date = "2024-09-01",
                    focusedMinutes = 155,
                    blockCount = 12,
                    unlockAttempts = 4,
                ),
                todayGoals = listOf(
                    Goal(id = 1, name = "Deep Work", targetMinutes = 90, progressMinutes = 45, date = "2024-09-01"),
                ),
            ),
            isEffectivelyActive = true,
        )
    }
}

@Preview(name = "Dashboard — Empty (light)", showBackground = true)
@Composable
private fun DashboardScreenPreview_Empty() {
    UnpostponeTheme(darkTheme = false) {
        DashboardContentPreview(
            uiState = DashboardUiState(isLoading = false, isBlockingActive = false),
            isEffectivelyActive = false,
        )
    }
}

@Composable
private fun DashboardContentPreview(
    uiState: DashboardUiState,
    isEffectivelyActive: Boolean,
) {
    DashboardContent(
        uiState = uiState,
        isEffectivelyActive = isEffectivelyActive,
        onToggleBlocking = {},
        onAddGoal = {},
        onOpenGoals = {},
        onOpenStatistics = {},
        onOpenPomodoro = {},
        contentPadding = PaddingValues(0.dp),
    )
}
