package com.unpostpone.app.presentation.navigation

/**
 * The full set of routes the Unpostpone app can navigate to.
 *
 *  Onboarding     — 4-page pager (welcome, features, privacy, permissions);
 *                   only shown on first launch. The OnboardingViewModel reads
 *                   the persisted completion flag on entry and, if the user
 *                   has already finished onboarding, immediately routes to
 *                   the Dashboard so returning users never see this screen.
 *  Dashboard      — home: today's focus, active goal, streak, blocked apps
 *  Goals          — manage daily goals
 *  Statistics     — focus time, streaks, weekly chart
 *  Settings       — account, theme, blocked apps
 *  Blocker        — full-screen reminder for blocked apps (legacy route)
 *  FocusReminder  — the new gentle pause-before-proceeding screen
 */
sealed class Screen(val route: String) {
    data object Onboarding    : Screen("onboarding")

    data object Dashboard     : Screen("dashboard")
    data object Goals         : Screen("goals")
    data object Statistics    : Screen("statistics")
    data object Settings      : Screen("settings")

    data object Blocker       : Screen("blocker/{packageName}") {
        fun createRoute(packageName: String) = "blocker/$packageName"
    }

    data object FocusReminder : Screen("focus-reminder/{packageName}") {
        fun createRoute(packageName: String) = "focus-reminder/$packageName"
    }
}
