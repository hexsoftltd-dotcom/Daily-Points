package com.hexsoft.dailypoints.navigation

object AppRoutes {
    const val SPLASH = "splash"
    const val DASHBOARD = "dashboard"
    const val TASK_LIST = "task_list"
    const val ADD_TASK = "add_task"
    const val EDIT_TASK = "edit_task/{taskId}"
    const val DAILY_TASKS = "daily_tasks"
    const val CALENDAR = "calendar"
    const val REPORTS = "reports"
    const val ANALYTICS = "analytics"
    const val GOALS = "goals"
    const val SETTINGS = "settings"

    fun editTask(taskId: Long): String = "edit_task/$taskId"
}
