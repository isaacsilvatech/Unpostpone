package com.unpostpone.app.presentation.statistics

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.unpostpone.app.R
import com.unpostpone.app.domain.model.Statistics
import com.unpostpone.app.presentation.dashboard.BottomNavigationBar
import com.unpostpone.app.ui.theme.Dimens
import com.unpostpone.app.ui.theme.NumberDisplayMedium

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
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(
                    horizontal = Dimens.SpacingL,
                    vertical = Dimens.SpacingL,
                ),
                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingL),
            ) {
                item {
                    SectionHeader(
                        text = stringResource(R.string.statistics_summary_30d),
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingM),
                    ) {
                        SummaryCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.statistics_total_focus),
                            value = uiState.totalFocusedMinutes.toString(),
                            icon = Icons.Default.Timer,
                        )
                        SummaryCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.statistics_total_blocks),
                            value = uiState.totalBlockCount.toString(),
                            icon = Icons.Default.Block,
                        )
                    }
                }
                item {
                    SummaryCard(
                        modifier = Modifier.fillMaxWidth(),
                        title = stringResource(R.string.statistics_unlock_attempts),
                        value = uiState.totalUnlockAttempts.toString(),
                        icon = Icons.Default.LockOpen,
                    )
                }
                if (uiState.recentStats.isNotEmpty()) {
                    item {
                        SectionHeader(text = stringResource(R.string.statistics_daily_history))
                    }
                    items(uiState.recentStats) { stat -> DailyStatItem(stat) }
                } else {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(Dimens.SpacingHuge),
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
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground,
    )
}

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
            modifier = Modifier.padding(Dimens.CardPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingM),
        ) {
            Icon(
                icon,
                null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(Dimens.IconS),
            )
            Text(
                text = value,
                style = NumberDisplayMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

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
            modifier = Modifier.fillMaxWidth().padding(Dimens.CardPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stat.date,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingL)) {
                StatColumn("${stat.focusedMinutes}m", stringResource(R.string.statistics_label_focus))
                StatColumn("${stat.blockCount}", stringResource(R.string.statistics_label_blocks))
                StatColumn(
                    "${stat.unlockAttempts}",
                    stringResource(R.string.statistics_label_attempts),
                )
            }
        }
    }
}

@Composable
private fun StatColumn(value: String, label: String) {
    Column(horizontalAlignment = Alignment.End) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
