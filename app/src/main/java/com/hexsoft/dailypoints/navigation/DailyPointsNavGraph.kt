package com.hexsoft.dailypoints.navigation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hexsoft.dailypoints.presentation.analytics.AnalyticsScreen
import com.hexsoft.dailypoints.presentation.calendar.CalendarScreen
import com.hexsoft.dailypoints.presentation.daily.DailyTasksScreen
import com.hexsoft.dailypoints.presentation.dashboard.DashboardScreen
import com.hexsoft.dailypoints.presentation.goals.GoalsScreen
import com.hexsoft.dailypoints.presentation.reports.ReportsScreen
import com.hexsoft.dailypoints.presentation.settings.SettingsScreen
import com.hexsoft.dailypoints.presentation.splash.SplashScreen
import com.hexsoft.dailypoints.presentation.tasks.TaskEditorScreen
import com.hexsoft.dailypoints.presentation.tasks.TaskListScreen

private data class BottomDestination(
    val route: String,
    val label: String,
)

private val bottomDestinations = listOf(
    BottomDestination(AppRoutes.DASHBOARD, "Dashboard"),
    BottomDestination(AppRoutes.TASK_LIST, "Tasks"),
    BottomDestination(AppRoutes.DAILY_TASKS, "Daily"),
    BottomDestination(AppRoutes.CALENDAR, "Calendar"),
    BottomDestination(AppRoutes.SETTINGS, "Settings"),
)

@Composable
fun DailyPointsNavGraph(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = currentDestination?.route != AppRoutes.SPLASH &&
        currentDestination?.route != AppRoutes.ADD_TASK &&
        currentDestination?.route?.startsWith("edit_task/") != true

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomDestinations.forEach { destination ->
                        NavigationBarItem(
                            selected = currentDestination.isRouteSelected(destination.route),
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {},
                            label = { Text(destination.label) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = AppRoutes.SPLASH,
            modifier = Modifier
                .padding(padding)
                .then(modifier),
        ) {
            composable(AppRoutes.SPLASH) {
                SplashScreen(
                    onReady = {
                        navController.navigate(AppRoutes.DAILY_TASKS) {
                            popUpTo(AppRoutes.SPLASH) { inclusive = true }
                        }
                    },
                )
            }
            composable(AppRoutes.DASHBOARD) {
                DashboardScreen()
            }
            composable(AppRoutes.TASK_LIST) {
                TaskListScreen(
                    onAddTaskClick = { navController.navigate(AppRoutes.ADD_TASK) },
                    onEditTaskClick = { taskId -> navController.navigate(AppRoutes.editTask(taskId)) },
                )
            }
            composable(AppRoutes.ADD_TASK) {
                TaskEditorScreen(onSaved = { navController.popBackStack() })
            }
            composable(AppRoutes.EDIT_TASK) {
                TaskEditorScreen(onSaved = { navController.popBackStack() })
            }
            composable(AppRoutes.DAILY_TASKS) { DailyTasksScreen() }
            composable(AppRoutes.CALENDAR) { CalendarScreen() }
            composable(AppRoutes.REPORTS) { ReportsScreen() }
            composable(AppRoutes.ANALYTICS) { AnalyticsScreen() }
            composable(AppRoutes.GOALS) { GoalsScreen() }
            composable(AppRoutes.SETTINGS) { SettingsScreen() }
        }
    }
}

private fun NavDestination?.isRouteSelected(route: String): Boolean {
    return this?.hierarchy?.any { destination -> destination.route == route } == true
}
