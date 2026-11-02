package com.hexsoft.dailypoints.domain.model

data class Goal(
    val id: Int = 1,
    val dailyGoal: Int,
    val monthlyGoal: Int,
)

data class GoalProgress(
    val target: Int,
    val achieved: Int,
    val remaining: Int,
    val percentage: Float,
)
