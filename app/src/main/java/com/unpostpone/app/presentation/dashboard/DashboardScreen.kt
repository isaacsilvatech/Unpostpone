package com.unpostpone.app.presentation.dashboard

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.unpostpone.app.domain.model.Goal
import com.unpostpone.app.presentation.navigation.Screen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Unpostpone") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Configurações")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.Goals.route) }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Meta")
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { DateHeader() }
                item {
                    BlockingToggleCard(
                        isActive = uiState.isBlockingActive,
                        onToggle = viewModel::toggleBlocking
                    )
                }
                item {
                    ProgressSummaryCard(
                        completed = uiState.completedGoalsCount,
                        total = uiState.totalGoalsCount,
                        progress = uiState.overallProgress
                    )
                }
                item {
                    StatisticsCard(
                        focusedMinutes = uiState.todayStatistics?.focusedMinutes ?: 0,
                        blockCount = uiState.todayStatistics?.blockCount ?: 0,
                        unlockAttempts = uiState.todayStatistics?.unlockAttempts ?: 0
                    )
                }
                if (uiState.todayGoals.isEmpty()) {
                    item { EmptyGoalsCard(onAddGoal = { navController.navigate(Screen.Goals.route) }) }
                } else {
                    item {
                        Text(
                            text = "Metas de Hoje",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(uiState.todayGoals) { goal -> GoalProgressCard(goal = goal) }
                }
            }
        }

        uiState.error?.let {
            LaunchedEffect(it) { viewModel.dismissError() }
        }
    }
}

@Composable
private fun DateHeader() {
    val today = remember {
        SimpleDateFormat("EEEE, dd 'de' MMMM", Locale.forLanguageTag("pt-BR"))
            .format(Date())
            .replaceFirstChar { it.uppercase() }
    }
    Text(
        text = today,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun BlockingToggleCard(isActive: Boolean, onToggle: () -> Unit) {
    val containerColor = if (isActive)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isActive)
        MaterialTheme.colorScheme.onPrimaryContainer
    else
        MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isActive) "Bloqueio Ativo" else "Bloqueio Inativo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Text(
                    text = if (isActive) "Distrações estão bloqueadas" else "Toque para ativar",
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor
                )
            }
            Switch(checked = isActive, onCheckedChange = { onToggle() })
        }
    }
}

@Composable
private fun ProgressSummaryCard(completed: Int, total: Int, progress: Float) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Progresso Geral",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$completed/$total metas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${(progress * 100).toInt()}% concluído",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatisticsCard(focusedMinutes: Int, blockCount: Int, unlockAttempts: Int) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                text = "Estatísticas de Hoje",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatItem(Icons.Default.Timer, "${focusedMinutes}min", "Focado")
                StatItem(Icons.Default.Block, "$blockCount", "Bloqueios")
                StatItem(Icons.Default.LockOpen, "$unlockAttempts", "Tentativas")
            }
        }
    }
}

@Composable
private fun StatItem(icon: ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun GoalProgressCard(goal: Goal) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(goal.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                if (goal.isCompleted) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Concluído",
                        tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { goal.progressPercent },
                modifier = Modifier.fillMaxWidth(),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${goal.progressMinutes}/${goal.targetMinutes} min",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyGoalsCard(onAddGoal: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Flag, contentDescription = null, modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Text("Nenhuma meta para hoje", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onAddGoal) { Text("Adicionar Meta") }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == Screen.Dashboard.route,
            onClick = { navController.navigate(Screen.Dashboard.route) { launchSingleTop = true } },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Dashboard") }
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Goals.route,
            onClick = { navController.navigate(Screen.Goals.route) { launchSingleTop = true } },
            icon = { Icon(Icons.Default.Flag, contentDescription = null) },
            label = { Text("Metas") }
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Statistics.route,
            onClick = { navController.navigate(Screen.Statistics.route) { launchSingleTop = true } },
            icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
            label = { Text("Stats") }
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Settings.route,
            onClick = { navController.navigate(Screen.Settings.route) { launchSingleTop = true } },
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            label = { Text("Config") }
        )
    }
}
