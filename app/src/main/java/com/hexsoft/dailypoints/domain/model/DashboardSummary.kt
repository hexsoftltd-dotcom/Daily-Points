package com.hexsoft.dailypoints.domain.model

import java.time.LocalDate

data class DashboardSummary(
    val profileName: String,
    val profilePoints: Int,
    val todayPoints: Int,
    val todayGoal: Int,
    val todayProgressPercentage: Float,
    val remainingPoints: Int,
    val weeklyTotal: Int,
    val weeklyAverage: Float,
    val monthlyTotal: Int,
    val monthlyAverage: Float,
    val currentStreak: Int,
    val longestStreak: Int,
    val bestDay: LocalDate?,
    val bestMonth: String?,
)
