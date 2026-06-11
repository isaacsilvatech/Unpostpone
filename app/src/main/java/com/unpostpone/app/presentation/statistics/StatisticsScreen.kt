package com.unpostpone.app.presentation.statistics

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.unpostpone.app.R
import com.unpostpone.app.core.util.DateFormatter
import com.unpostpone.app.domain.model.Statistics
import com.unpostpone.app.presentation.dashboard.BottomNavigationBar
import com.unpostpone.app.ui.theme.DarkHeroSurface
import com.unpostpone.app.ui.theme.Dimens
import com.unpostpone.app.ui.theme.LightHeroSurface
import com.unpostpone.app.ui.theme.LocalIsDarkTheme
import com.unpostpone.app.ui.theme.NumberBody
import com.unpostpone.app.ui.theme.NumberDisplayLarge
import com.unpostpone.app.ui.theme.NumberHeadline
import com.unpostpone.app.ui.theme.UnpostponeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    navController: NavController,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.statistics_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            StatisticsContent(
                uiState = uiState,
                contentPadding = paddingValues,
            )
        }
    }
}

@Composable
private fun StatisticsContent(
    uiState: StatisticsUiState,
    contentPadding: PaddingValues,
) {
    val isDark = LocalIsDarkTheme.current
    val heroSurface = if (isDark) DarkHeroSurface else LightHeroSurface
    val totalMinutes = uiState.totalFocusedMinutes
    val totalHours = totalMinutes / 60

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentPadding = PaddingValues(
            horizontal = Dimens.ScreenGutter,
            vertical = Dimens.SpacingL,
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingL),
    ) {
        // 1. Hero band — total focused minutes on a HeroSurface card
        item {
            HeroBand(
                containerColor = heroSurface,
                eyebrow = stringResource(R.string.statistics_hero_eyebrow),
                value = totalMinutes.toString(),
                valueUnit = stringResource(R.string.statistics_label_focus),
                subtitle = stringResource(R.string.statistics_hero_subtitle, totalHours),
            )
        }

        // 2. 2-column grid — secondary summary cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingM),
            ) {
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.statistics_total_blocks),
                    value = uiState.totalBlockCount.toString(),
                    icon = Icons.Default.Block,
                )
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.statistics_unlock_attempts),
                    value = uiState.totalUnlockAttempts.toString(),
                    icon = Icons.Default.LockOpen,
                )
            }
        }

        // 3. Daily history
        if (uiState.recentStats.isNotEmpty()) {
            item {
                Spacer(Modifier.height(Dimens.SectionTitleTopGap))
                Text(
                    text = stringResource(R.string.statistics_daily_history),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            items(uiState.recentStats) { stat -> DailyStatItem(stat) }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.SpacingHuge),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.statistics_empty),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  Hero band
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun HeroBand(
    containerColor: androidx.compose.ui.graphics.Color,
    eyebrow: String,
    value: String,
    valueUnit: String,
    subtitle: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(Dimens.HeroCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = Dimens.CardPaddingLarge,
                    vertical = Dimens.SpacingXL,
                ),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingS),
        ) {
            Text(
                text = eyebrow.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingS),
            ) {
                Text(
                    text = value,
                    style = NumberDisplayLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = valueUnit,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  Secondary summary card — 2-column grid item
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun SummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier.padding(Dimens.SpacingL),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingS),
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(Dimens.IconS),
            )
            Text(
                text = value,
                style = NumberHeadline,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  Daily history row
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun DailyStatItem(stat: Statistics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.CardPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = DateFormatter.formatForUser(stat.date),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingL)) {
                StatColumn(
                    value = "${stat.focusedMinutes}",
                    label = stringResource(R.string.statistics_label_focus),
                )
                StatColumn(
                    value = "${stat.blockCount}",
                    label = stringResource(R.string.statistics_label_blocks),
                )
                StatColumn(
                    value = "${stat.unlockAttempts}",
                    label = stringResource(R.string.statistics_label_attempts),
                )
            }
        }
    }
}

@Composable
private fun StatColumn(value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier.widthIn(min = 56.dp),
    ) {
        Text(
            text = value,
            style = NumberBody,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  Previews
// ═══════════════════════════════════════════════════════════════════════════

@Preview(name = "Statistics — Populated (light)", showBackground = true)
@Composable
private fun StatisticsContentPreview_Populated() {
    UnpostponeTheme(darkTheme = false) {
        StatisticsContent(
            uiState = StatisticsUiState(
                isLoading = false,
                recentStats = listOf(
                    Statistics(
                        date = "2024-09-03",
                        focusedMinutes = 180,
                        blockCount = 12,
                        unlockAttempts = 4
                    ),
                    Statistics(
                        date = "2024-09-02",
                        focusedMinutes = 145,
                        blockCount = 9,
                        unlockAttempts = 1
                    ),
                    Statistics(
                        date = "2024-09-01",
                        focusedMinutes = 90,
                        blockCount = 6,
                        unlockAttempts = 0
                    ),
                ),
            ),
            contentPadding = PaddingValues(0.dp),
        )
    }
}

@Preview(name = "Statistics — Empty (light)", showBackground = true)
@Composable
private fun StatisticsContentPreview_Empty() {
    UnpostponeTheme(darkTheme = false) {
        StatisticsContent(
            uiState = StatisticsUiState(isLoading = false),
            contentPadding = PaddingValues(0.dp),
        )
    }
}

@Preview(name = "Statistics — Populated (dark)", showBackground = true)
@Composable
private fun StatisticsContentPreview_Populated_Dark() {
    UnpostponeTheme(darkTheme = true) {
        StatisticsContent(
            uiState = StatisticsUiState(
                isLoading = false,
                recentStats = listOf(
                    Statistics(
                        date = "2024-09-03",
                        focusedMinutes = 180,
                        blockCount = 12,
                        unlockAttempts = 4
                    ),
                    Statistics(
                        date = "2024-09-02",
                        focusedMinutes = 145,
                        blockCount = 9,
                        unlockAttempts = 1
                    ),
                ),
            ),
            contentPadding = PaddingValues(0.dp),
        )
    }
}
