package com.unpostpone.app.presentation.goals

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.unpostpone.app.R
import com.unpostpone.app.domain.model.Goal
import com.unpostpone.app.presentation.dashboard.BottomNavigationBar
import com.unpostpone.app.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    navController: NavController,
    viewModel: GoalsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.goals_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.goals_back_cd),
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::showAddDialog,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = androidx.compose.foundation.shape.CircleShape,
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.goals_add_cd),
                )
            }
        },
    ) { paddingValues ->
        when {
            uiState.isLoading -> Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }

            uiState.goals.isEmpty() -> Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Flag,
                        null,
                        modifier = Modifier.size(Dimens.IconXL),
                        tint = MaterialTheme.colorScheme.outline,
                    )
                    Spacer(Modifier.height(Dimens.SpacingL))
                    Text(
                        text = stringResource(R.string.goals_empty_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(Dimens.SpacingS))
                    Text(
                        text = stringResource(R.string.goals_empty_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(
                    horizontal = Dimens.SpacingL,
                    vertical = Dimens.SpacingL,
                ),
                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingM),
            ) {
                items(uiState.goals, key = { it.id }) { goal ->
                    GoalItem(
                        goal = goal,
                        onDelete = { viewModel.deleteGoal(goal) },
                        onProgressUpdate = { viewModel.updateProgress(goal, it) },
                    )
                }
            }
        }
    }

    if (uiState.isAddDialogVisible) {
        AddGoalDialog(
            onDismiss = viewModel::hideAddDialog,
            onConfirm = viewModel::addGoal,
        )
    }
}

@Composable
private fun GoalItem(
    goal: Goal,
    onDelete: () -> Unit,
    onProgressUpdate: (Int) -> Unit,
) {
    var showSlider by remember { mutableStateOf(false) }
    var sliderValue by remember(goal.progressMinutes, goal.targetMinutes) {
        mutableFloatStateOf(goal.progressMinutes.toFloat())
    }

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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingS),
                ) {
                    if (goal.isCompleted) {
                        Icon(
                            Icons.Default.CheckCircle,
                            null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(Dimens.IconS),
                        )
                    }
                    Text(
                        text = goal.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                    )
                }
                Row {
                    IconButton(
                        onClick = { showSlider = !showSlider },
                        modifier = Modifier.size(36.dp),
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            null,
                            modifier = Modifier.size(Dimens.IconS),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp),
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(Dimens.IconS),
                        )
                    }
                }
            }

            Spacer(Modifier.height(Dimens.SpacingM))
            LinearProgressIndicator(
                progress = { goal.progressPercent },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Spacer(Modifier.height(Dimens.SpacingS))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.goals_progress_minutes_done, goal.progressMinutes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = stringResource(R.string.goals_target_minutes, goal.targetMinutes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (showSlider) {
                Spacer(Modifier.height(Dimens.SpacingM))
                Text(
                    text = stringResource(R.string.goals_progress_label, sliderValue.toInt()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Slider(
                    value = sliderValue,
                    onValueChange = { sliderValue = it },
                    onValueChangeFinished = { onProgressUpdate(sliderValue.toInt()) },
                    valueRange = 0f..goal.targetMinutes.toFloat(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                        thumbColor = MaterialTheme.colorScheme.primary,
                    ),
                )
            }
        }
    }
}

@Composable
private fun AddGoalDialog(onDismiss: () -> Unit, onConfirm: (String, Int) -> Unit) {
    var name by remember { mutableStateOf("") }
    var targetMinutesText by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf<String?>(null) }
    var minutesError by remember { mutableStateOf<String?>(null) }

    val nameErrorText = stringResource(R.string.goals_add_name_error)
    val minutesErrorText = stringResource(R.string.goals_add_minutes_error)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.goals_add_dialog_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingM)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; nameError = null },
                    label = { Text(stringResource(R.string.goals_add_name_label)) },
                    placeholder = { Text(stringResource(R.string.goals_add_name_placeholder)) },
                    isError = nameError != null,
                    supportingText = nameError?.let { { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = targetMinutesText,
                    onValueChange = { targetMinutesText = it; minutesError = null },
                    label = { Text(stringResource(R.string.goals_add_minutes_label)) },
                    placeholder = { Text(stringResource(R.string.goals_add_minutes_placeholder)) },
                    isError = minutesError != null,
                    supportingText = minutesError?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val targetMinutes = targetMinutesText.toIntOrNull()
                    var hasError = false
                    if (name.isBlank()) { nameError = nameErrorText; hasError = true }
                    if (targetMinutes == null || targetMinutes <= 0) { minutesError = minutesErrorText; hasError = true }
                    if (!hasError && targetMinutes != null) onConfirm(name.trim(), targetMinutes)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text(
                    text = stringResource(R.string.goals_add_create),
                    fontWeight = FontWeight.SemiBold,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.action_cancel),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
    )
}
