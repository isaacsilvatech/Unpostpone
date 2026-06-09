package com.unpostpone.app.presentation.navigation

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")

    data object Dashboard : Screen("dashboard")
    data object Goals : Screen("goals")
    data object Statistics : Screen("statistics")
    data object Settings : Screen("settings")

    data object Blocker : Screen("blocker/{packageName}") {
        fun createRoute(packageName: String) = "blocker/$packageName"
    }

    data object FocusReminder : Screen("focus-reminder/{packageName}") {
        fun createRoute(packageName: String) = "focus-reminder/$packageName"
    }
}
