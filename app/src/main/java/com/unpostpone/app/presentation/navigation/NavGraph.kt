package com.unpostpone.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.unpostpone.app.presentation.blocker.BlockerScreen
import com.unpostpone.app.presentation.dashboard.DashboardScreen
import com.unpostpone.app.presentation.goals.GoalsScreen
import com.unpostpone.app.presentation.settings.SettingsScreen
import com.unpostpone.app.presentation.statistics.StatisticsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Dashboard.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
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
            arguments = listOf(navArgument("packageName") { type = NavType.StringType })
        ) { backStackEntry ->
            val packageName = backStackEntry.arguments?.getString("packageName").orEmpty()
            BlockerScreen(packageName = packageName, navController = navController)
        }
    }
}
