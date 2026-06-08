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
import com.unpostpone.app.presentation.settings.SettingsScreen
import com.unpostpone.app.presentation.statistics.StatisticsScreen

/**
 * The single source of truth for navigation in the app.
 *
 * Onboarding is the start destination. The OnboardingViewModel reads the
 * persisted completion flag on entry; if the user has already finished
 * onboarding, it fires the completion callback and the nav graph replaces
 * Onboarding with the Dashboard. First-launch users see the 4-page pager.
 *
 * Permission requests from the Onboarding pager launch the platform Settings
 * intent — Android does not let you grant Accessibility / Usage Access from
 * a normal in-app dialog.
 */
@Composable
fun NavGraph(
    navController: NavHostController,
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = Screen.Onboarding.route,
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
                    // Posted from the MainActivity at request time; for now we open app
                    // notification settings. The actual request is a POST_NOTIFICATIONS
                    // runtime permission on Android 13+ — handled in the host activity.
                    context.startActivity(
                        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                },
            )
        }

        // ── Main app ──────────────────────────────────────────────────
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

        // ── System-driven surfaces ────────────────────────────────────
        composable(
            route = Screen.Blocker.route,
            arguments = listOf(navArgument("packageName") { type = NavType.StringType }),
        ) { backStackEntry ->
            val packageName = backStackEntry.arguments?.getString("packageName").orEmpty()
            BlockerScreen(packageName = packageName, navController = navController)
        }

        composable(
            route = Screen.FocusReminder.route,
            arguments = listOf(navArgument("packageName") { type = NavType.StringType }),
        ) { backStackEntry ->
            val packageName = backStackEntry.arguments?.getString("packageName").orEmpty()
            FocusReminderScreen(
                onStayFocused = {
                    // The accessibility service listens for the home key — we exit
                    // back to the previous app by finishing this activity.
                    navController.popBackStack()
                },
                onContinue = {
                    // The user chose to continue; close the reminder and let the
                    // service bring the original app to the foreground.
                    navController.popBackStack()
                },
            )
        }
    }
}
