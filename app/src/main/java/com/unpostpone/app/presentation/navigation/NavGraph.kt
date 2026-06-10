package com.unpostpone.app.presentation.navigation

import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.unpostpone.app.presentation.blocker.BlockerScreen
import com.unpostpone.app.presentation.dashboard.DashboardScreen
import com.unpostpone.app.presentation.focusreminder.FocusReminderScreen
import com.unpostpone.app.presentation.goals.GoalsScreen
import com.unpostpone.app.presentation.onboarding.OnboardingScreen
import com.unpostpone.app.presentation.pomodoro.PomodoroScreen
import com.unpostpone.app.presentation.reblock.ReBlockScreen
import com.unpostpone.app.presentation.settings.SettingsScreen
import com.unpostpone.app.presentation.statistics.StatisticsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Onboarding.route,
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        // ── Onboarding ────────────────────────────────────────────────
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onOnboardingComplete = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                },
                onRequestAccessibilityPermission = {
                    context.startActivity(
                        Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                },
                onRequestUsageAccessPermission = {
                    context.startActivity(
                        Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                },
                onRequestNotificationPermission = {
                    context.startActivity(
                        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                },
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(navController = navController)
        }
        composable(Screen.Goals.route) {
            GoalsScreen(navController = navController)
        }
        composable(Screen.Statistics.route) {
            StatisticsScreen(navController = navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }

        composable(
            route = Screen.Blocker.route,
            arguments = listOf(navArgument("packageName") { type = NavType.StringType }),
        ) { backStackEntry ->
            val packageName = backStackEntry.arguments?.getString("packageName").orEmpty()
            BlockerScreen(packageName = packageName, navController = navController)
        }

        composable(
            route = Screen.ReBlock.route,
            arguments = listOf(navArgument("packageName") { type = NavType.StringType }),
        ) { backStackEntry ->
            val packageName = backStackEntry.arguments?.getString("packageName").orEmpty()
            ReBlockScreen(packageName = packageName, navController = navController)
        }

        composable(
            route = Screen.FocusReminder.route,
            arguments = listOf(navArgument("packageName") { type = NavType.StringType }),
        ) { backStackEntry ->
            val packageName = backStackEntry.arguments?.getString("packageName").orEmpty()
            FocusReminderScreen(
                onContinue = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }

        composable(Screen.Pomodoro.route) {
            PomodoroScreen(navController = navController)
        }
    }
}
