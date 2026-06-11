package com.unpostpone.app.presentation.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.unpostpone.app.R
import com.unpostpone.app.core.util.AppLabelResolver
import com.unpostpone.app.domain.model.BlockedApp
import com.unpostpone.app.presentation.dashboard.format.FocusTimeFormatter
import com.unpostpone.app.ui.theme.*

@Composable
fun HeroFocusCard(
    isActive: Boolean,
    enabledBlockedAppCount: Int,
    enabledBlockedApps: List<BlockedApp>,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(HeroRadius),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        border = BorderStroke(
            width = 1.dp,
            color = if (isActive) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outlineVariant,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.SpacingXXL, vertical = Dimens.SpacingXXL),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LockMedallion(active = isActive)
            Spacer(Modifier.width(Dimens.SpacingXXL))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.dashboard_focus_protection_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(Dimens.SpacingXS))
                Text(
                    text = stringResource(R.string.dashboard_focus_protection_apps, enabledBlockedAppCount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (enabledBlockedApps.isNotEmpty()) {
                    Spacer(Modifier.height(Dimens.SpacingS))
                    BlockedAppsIconRow(apps = enabledBlockedApps)
                }
                Spacer(Modifier.height(Dimens.SpacingM))
                FocusStatusRow(
                    isActive = isActive,
                    onToggle = onToggle,
                )
            }
        }
    }
}

@Composable
private fun BlockedAppsIconRow(apps: List<BlockedApp>) {
    val context = LocalContext.current
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingS),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(apps, key = { it.packageName }) { app ->
            val drawable = AppLabelResolver.resolveIcon(context, app.packageName)
            val displayName = app.displayName.ifBlank { app.packageName }
            if (drawable != null) {
                val bitmap = remember(drawable) { drawable.toBitmap().asImageBitmap() }
                androidx.compose.foundation.Image(
                    bitmap = bitmap,
                    contentDescription = displayName,
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape),
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Smartphone,
                        contentDescription = displayName,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(12.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun LockMedallion(active: Boolean) {
    val bg = if (active) MaterialTheme.colorScheme.primaryContainer
             else MaterialTheme.colorScheme.surfaceContainerHigh
    val icon = if (active) MaterialTheme.colorScheme.primary
               else MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(bg),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = icon,
            modifier = Modifier.size(Dimens.IconL),
        )
    }
}

@Composable
private fun FocusStatusRow(
    isActive: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(
                if (isActive) R.string.dashboard_focus_status_active
                else R.string.dashboard_focus_status_inactive
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = if (isActive) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = isActive,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                checkedBorderColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                uncheckedBorderColor = MaterialTheme.colorScheme.outline,
            ),
        )
    }
}

@Composable
fun TodaysFocusCard(
    focusedMinutes: Int,
    dailyTargetMinutes: Int,
    progressPercent: Float,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(HeroRadius),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.SpacingXXL, vertical = Dimens.SpacingXXL),
        ) {
            Text(
                text = stringResource(R.string.dashboard_todays_focus_title).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(Dimens.SpacingS))
            Text(
                text = FocusTimeFormatter.formatFocusTime(focusedMinutes),
                style = NumberDisplayMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(Dimens.SpacingS))
            Text(
                text = stringResource(R.string.dashboard_focused_today),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(Dimens.SpacingL))
            LinearProgressIndicator(
                progress = { progressPercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(Dimens.SpacingS)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
            )
            Spacer(Modifier.height(Dimens.SpacingS))
            Text(
                text = stringResource(
                    R.string.dashboard_daily_goal_percent,
                    (progressPercent * 100).toInt(),
                    dailyTargetMinutes / 60,
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

data class ToolEntry(
    val labelRes: Int,
    val icon: ImageVector,
    val onClick: () -> Unit,
)

@Composable
fun ToolsRow(
    tools: List<ToolEntry>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionLabel(text = stringResource(R.string.dashboard_tools_label))
        Spacer(Modifier.height(Dimens.SpacingM))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingM),
            contentPadding = PaddingValues(horizontal = 0.dp),
        ) {
            items(items = tools, key = null) { tool -> ToolTile(tool = tool) }
        }
    }
}

@Composable
private fun ToolTile(tool: ToolEntry) {
    val shape = RoundedCornerShape(Dimens.SpacingL)
    Surface(
        modifier = Modifier
            .width(96.dp)
            .height(104.dp)
            .clip(shape),
        shape = shape,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        onClick = tool.onClick,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.SpacingM),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Icon(
                imageVector = tool.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(Dimens.IconM),
            )
            Text(
                text = stringResource(tool.labelRes),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun InsightsRow(
    blocked: Int,
    attempts: Int,
    goals: Int,
    focusedMinutes: Int,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionLabel(text = stringResource(R.string.dashboard_insights_label))
        Spacer(Modifier.height(Dimens.SpacingM))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingM),
        ) {
            InsightTile(
                value = blocked.toString(),
                label = stringResource(R.string.dashboard_insight_blocked),
                modifier = Modifier.weight(1f),
            )
            InsightTile(
                value = attempts.toString(),
                label = stringResource(R.string.dashboard_insight_attempts),
                modifier = Modifier.weight(1f),
            )
            InsightTile(
                value = goals.toString(),
                label = stringResource(R.string.dashboard_insight_goals),
                modifier = Modifier.weight(1f),
            )
            InsightTile(
                value = FocusTimeFormatter.formatFocusTime(focusedMinutes),
                label = stringResource(R.string.dashboard_insight_focused),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun InsightTile(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(MediumRadius),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.SpacingS, vertical = Dimens.SpacingM),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value,
                style = NumberHeadline,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(Dimens.SpacingXS))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun MomentumCard(
    streakDays: Int,
    quote: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(HeroRadius),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.SpacingXXL, vertical = Dimens.SpacingXXL),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Dimens.IconM),
                )
                Spacer(Modifier.width(Dimens.SpacingS))
                Text(
                    text = stringResource(R.string.dashboard_streak_days, streakDays),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(Modifier.height(Dimens.SpacingM))
            Text(
                text = "\u201C$quote\u201D",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

fun defaultDashboardTools(
    onPomodoro: () -> Unit,
    onTasks: () -> Unit,
    onNotes: () -> Unit,
    onGoals: () -> Unit,
    onStatistics: () -> Unit,
): List<ToolEntry> = listOf(
    ToolEntry(R.string.dashboard_tool_pomodoro,   Icons.Default.Timer,       onPomodoro),
    ToolEntry(R.string.dashboard_tool_tasks,      Icons.Default.CheckCircle, onTasks),
    ToolEntry(R.string.dashboard_tool_notes,      Icons.Default.NoteAlt,     onNotes),
    ToolEntry(R.string.dashboard_tool_goals,      Icons.Default.Flag,        onGoals),
    ToolEntry(R.string.dashboard_tool_statistics, Icons.Default.BarChart,    onStatistics),
)
