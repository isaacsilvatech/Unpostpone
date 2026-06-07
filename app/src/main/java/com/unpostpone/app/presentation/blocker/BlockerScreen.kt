package com.unpostpone.app.presentation.blocker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.unpostpone.app.domain.model.Goal

@Composable
fun BlockerScreen(
    packageName: String,
    navController: NavController,
    viewModel: BlockerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.height(48.dp))

            // Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(Icons.Default.Lock, null, modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.onErrorContainer)
                Text(
                    text = "App Bloqueado",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Conclua suas metas antes de acessar este app.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center
                )
            }

            // Active goals
            if (uiState.activeGoals.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Metas Pendentes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    uiState.activeGoals.take(3).forEach { goal -> BlockerGoalItem(goal) }
                }
            }

            // Actions
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isTemporarilyUnlocked) {
                    Card(colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.error)) {
                        Text(
                            text = "Desbloqueado por ${uiState.unlockCountdown}s",
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onError,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = viewModel::requestTemporaryUnlock,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onErrorContainer)
                    ) {
                        Icon(Icons.Default.LockOpen, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Desbloquear por 5 minutos")
                    }
                }

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onErrorContainer,
                        contentColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Voltar e Focar")
                }
            }
        }
    }
}

@Composable
private fun BlockerGoalItem(goal: Goal) {
    Card(colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.2f))) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Text(goal.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onErrorContainer)
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { goal.progressPercent },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onErrorContainer,
                trackColor = MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
            )
            Text("${goal.progressMinutes}/${goal.targetMinutes} min",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f))
        }
    }
}
