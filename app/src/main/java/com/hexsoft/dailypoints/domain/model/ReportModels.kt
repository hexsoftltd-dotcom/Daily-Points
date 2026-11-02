package com.hexsoft.dailypoints.domain.model

import java.time.LocalDate
import java.time.YearMonth

data class DailyReport(
    val date: LocalDate,
    val totalPoints: Int,
    val completions: List<TaskCompletionDetail>,
)

data class WeeklyReport(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val totalPoints: Int,
    val averagePoints: Float,
    val completions: List<TaskCompletionDetail>,
)

data class MonthlyReport(
    val month: YearMonth,
    val totalPoints: Int,
    val averagePoints: Float,
    val completions: List<TaskCompletionDetail>,
)

data class YearlyReport(
    val year: Int,
    val totalPoints: Int,
    val averagePoints: Float,
    val completions: List<TaskCompletionDetail>,
)

data class DateRangeReport(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val totalPoints: Int,
    val averagePoints: Float,
    val completions: List<TaskCompletionDetail>,
)
