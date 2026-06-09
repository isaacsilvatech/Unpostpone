package com.unpostpone.app.presentation.dashboard

import android.content.ComponentName
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.unpostpone.app.R
import com.unpostpone.app.domain.model.Goal
import com.unpostpone.app.domain.model.Statistics
import com.unpostpone.app.presentation.dashboard.components.HeroFocusRing
import com.unpostpone.app.presentation.navigation.Screen
import com.unpostpone.app.service.accessibility.UnpostponeAccessibilityService
import com.unpostpone.app.ui.theme.Dimens
import com.unpostpone.app.ui.theme.HeroRadius
import com.unpostpone.app.ui.theme.NumberHeadline
import com.unpostpone.app.ui.theme.NumberTitle
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
        floatingActionButton = {
            // FAB is the 15% brand touch — teal #0C4D5B with a white plus.
            // Override the M3 default which would use primaryContainer.
            FloatingActionButton(
                onClick = { navController.navigate(Screen.Goals.route) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = androidx.compose.foundation.shape.CircleShape,
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.dashboard_add_goal_cd),
                )
            }
        },
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
        } else {
            val focusedMinutes = uiState.todayStatistics?.focusedMinutes ?: 0
            val blockCount = uiState.todayStatistics?.blockCount ?: 0
            val unlockAttempts = uiState.todayStatistics?.unlockAttempts ?: 0
            val completedGoals = uiState.completedGoalsCount
            val totalGoals = uiState.totalGoalsCount
            val heroSubtitle = if (totalGoals == 0) {
                stringResource(R.string.dashboard_hero_subtitle_empty)
            } else {
                stringResource(
                    R.string.dashboard_hero_subtitle,
                    completedGoals,
                    totalGoals,
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(
                    horizontal = Dimens.SpacingXL,
                    vertical = Dimens.SpacingL,
                ),
                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingL),
            ) {
                // i. Hero focus ring
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        HeroFocusRing(
                            focusedMinutes = focusedMinutes,
                            dailyTargetMinutes = 480,
                            eyebrow = stringResource(R.string.dashboard_hero_eyebrow),
                            subtitle = heroSubtitle,
                        )
                    }
                }

                // ii. Compact blocking-toggle row
                item {
                    BlockingToggleCard(
                        isActive = isEffectivelyActive,
                        onToggle = viewModel::onToggleBlocking,
                    )
                }

                // iii. "Today's activity" section header
                item {
                    Spacer(Modifier.height(Dimens.SectionTitleTopGap))
                    ActivitySectionHeader(focusedMinutes = focusedMinutes)
                }

                // iv. Stat strip (Blocks / Attempts / Goals done)
                item {
                    StatStrip(
                        blocks = blockCount,
                        attempts = unlockAttempts,
                        goalsDone = completedGoals,
                    )
                }

                // v + vi. Today's goals section
                if (uiState.todayGoals.isEmpty()) {
                    // vii. Empty state — small focus ring at 64dp
                    item { EmptyGoalsCard(onAddGoal = { navController.navigate(Screen.Goals.route) }) }
                } else {
                    item {
                        Spacer(Modifier.height(Dimens.SectionTitleTopGap))
                        Text(
                            text = stringResource(R.string.dashboard_today_goals_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                    items(uiState.todayGoals) { goal -> GoalProgressCard(goal = goal) }
                }
            }
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
}

// ═══════════════════════════════════════════════════════════════════════════
//  Blocking toggle — compact row, not a giant card
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun BlockingToggleCard(isActive: Boolean, onToggle: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(HeroRadius - 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isActive) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outlineVariant,
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(
                horizontal = Dimens.CardPadding,
                vertical = Dimens.SpacingS + Dimens.SpacingXS, // ~30% tighter
            ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isActive) stringResource(R.string.dashboard_blocking_active)
                           else stringResource(R.string.dashboard_blocking_inactive),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = if (isActive) stringResource(R.string.dashboard_blocking_active_subtitle)
                           else stringResource(R.string.dashboard_blocking_inactive_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Switch(
                checked = isActive,
                onCheckedChange = { onToggle() },
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
}

// ═══════════════════════════════════════════════════════════════════════════
//  "Today's activity" section header — title + JetBrains Mono focused span
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun ActivitySectionHeader(focusedMinutes: Int) {
    val focusedText = remember(focusedMinutes) {
        com.unpostpone.app.presentation.dashboard.format
            .FocusTimeFormatter.formatFocusTime(focusedMinutes)
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.dashboard_section_activity),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = focusedText,
                style = NumberTitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.width(Dimens.SpacingXS))
            Text(
                text = stringResource(R.string.dashboard_focused_short),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  Stat strip — three equal columns separated by hairlines
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun StatStrip(
    blocks: Int,
    attempts: Int,
    goalsDone: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = Dimens.SpacingS),
        verticalAlignment = Alignment.Bottom,
    ) {
        StatTile(
            value = blocks.toString(),
            label = stringResource(R.string.dashboard_stat_blocks),
            modifier = Modifier.weight(1f),
        )
        VerticalHairline()
        StatTile(
            value = attempts.toString(),
            label = stringResource(R.string.dashboard_stat_attempts),
            modifier = Modifier.weight(1f),
        )
        VerticalHairline()
        StatTile(
            value = goalsDone.toString(),
            label = stringResource(R.string.dashboard_stat_goals_done),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun VerticalHairline() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(40.dp),
    ) {
        HorizontalDivider(
            modifier = Modifier
                .width(1.dp)
                .height(40.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
        )
    }
}

@Composable
private fun StatTile(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            style = NumberHeadline,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(Dimens.SpacingXS))
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  Goal progress card
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun GoalProgressCard(goal: Goal) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (goal.isCompleted) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outlineVariant,
        ),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(Dimens.CardPadding)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = goal.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                )
                if (goal.isCompleted) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = stringResource(R.string.dashboard_goal_completed_cd),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(Dimens.IconS),
                    )
                }
            }
            Spacer(Modifier.height(Dimens.SpacingM))
            LinearProgressIndicator(
                progress = { goal.progressPercent },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
            )
            Spacer(Modifier.height(Dimens.SpacingS))
            Text(
                text = "${goal.progressMinutes}/${goal.targetMinutes} min",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  Empty goals card — small focus-ring instead of flag
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun EmptyGoalsCard(onAddGoal: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(Dimens.CardPaddingLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Small focus ring at 64dp — passing dailyTargetMinutes = 0
            // tells the ring to render the track only (no progress arc).
            HeroFocusRing(
                focusedMinutes = 0,
                dailyTargetMinutes = 0,
                eyebrow = "",
                subtitle = "",
                modifier = Modifier.size(64.dp),
            )
            Spacer(Modifier.height(Dimens.SpacingM))
            Text(
                text = stringResource(R.string.dashboard_empty_goals_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
            )
            Spacer(Modifier.height(Dimens.SpacingM))
            TextButton(
                onClick = onAddGoal,
                colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
            ) {
                Text(
                    text = stringResource(R.string.dashboard_empty_goals_action),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
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

// ═══════════════════════════════════════════════════════════════════════════
//  Previews
// ═══════════════════════════════════════════════════════════════════════════

@Preview(name = "Dashboard — Populated (light)", showBackground = true)
@Composable
private fun DashboardScreenPreview_Populated() {
    UnpostponeTheme(darkTheme = false) {
        // We can't preview the real nav here — call the inner content directly.
        DashboardContentPreview(
            uiState = DashboardUiState(
                isLoading = false,
                isBlockingActive = true,
                todayStatistics = Statistics(
                    date = "2024-09-01",
                    focusedMinutes = 145,
                    blockCount = 12,
                    unlockAttempts = 3,
                ),
                todayGoals = listOf(
                    Goal(id = 1, name = "Write product spec", targetMinutes = 60, progressMinutes = 45, date = "2024-09-01"),
                    Goal(id = 2, name = "Review pull requests", targetMinutes = 30, progressMinutes = 30, isCompleted = true, date = "2024-09-01"),
                    Goal(id = 3, name = "Reply to customer emails", targetMinutes = 45, progressMinutes = 20, date = "2024-09-01"),
                ),
            ),
        )
    }
}

@Preview(name = "Dashboard — Empty (light)", showBackground = true)
@Composable
private fun DashboardScreenPreview_Empty() {
    UnpostponeTheme(darkTheme = false) {
        DashboardContentPreview(
            uiState = DashboardUiState(isLoading = false, isBlockingActive = false),
        )
    }
}

/**
 * Inner composable that mirrors the production [DashboardScreen] body but
 * accepts a fake [DashboardUiState] so we can preview every state without
 * the Hilt ViewModel. Re-uses the same private helpers.
 */
@Composable
private fun DashboardContentPreview(uiState: DashboardUiState) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = androidx.compose.foundation.shape.CircleShape,
            ) { Icon(Icons.Default.Add, contentDescription = null) }
        },
    ) { paddingValues ->
        val focusedMinutes = uiState.todayStatistics?.focusedMinutes ?: 0
        val blockCount = uiState.todayStatistics?.blockCount ?: 0
        val unlockAttempts = uiState.todayStatistics?.unlockAttempts ?: 0
        val completedGoals = uiState.completedGoalsCount
        val totalGoals = uiState.totalGoalsCount
        val isEffectivelyActive =
            uiState.isBlockingActive && uiState.isAccessibilityServiceEnabled
        val heroSubtitle = if (totalGoals == 0) {
            stringResource(R.string.dashboard_hero_subtitle_empty)
        } else {
            stringResource(
                R.string.dashboard_hero_subtitle,
                completedGoals,
                totalGoals,
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(
                horizontal = Dimens.SpacingXL,
                vertical = Dimens.SpacingL,
            ),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingL),
        ) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    HeroFocusRing(
                        focusedMinutes = focusedMinutes,
                        dailyTargetMinutes = 480,
                        eyebrow = stringResource(R.string.dashboard_hero_eyebrow),
                        subtitle = heroSubtitle,
                    )
                }
            }
            item {
                BlockingToggleCard(
                    isActive = isEffectivelyActive,
                    onToggle = {},
                )
            }
            item {
                Spacer(Modifier.height(Dimens.SectionTitleTopGap))
                ActivitySectionHeader(focusedMinutes = focusedMinutes)
            }
            item {
                StatStrip(blocks = blockCount, attempts = unlockAttempts, goalsDone = completedGoals)
            }
            if (uiState.todayGoals.isEmpty()) {
                item { EmptyGoalsCard(onAddGoal = {}) }
            } else {
                item {
                    Spacer(Modifier.height(Dimens.SectionTitleTopGap))
                    Text(
                        text = stringResource(R.string.dashboard_today_goals_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
                items(uiState.todayGoals) { goal -> GoalProgressCard(goal = goal) }
            }
        }
    }
}
