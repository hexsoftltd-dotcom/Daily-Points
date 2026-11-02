package com.hexsoft.dailypoints.domain.model

import java.time.LocalDate
import java.time.YearMonth

data class ChartPoint(
    val label: String,
    val value: Float,
)

data class CategoryBreakdown(
    val category: String,
    val points: Int,
)

data class TaskStat(
    val taskId: Long,
    val taskName: String,
    val completionCount: Int,
)

data class AnalyticsSummary(
    val dailyTrend: List<ChartPoint>,
    val weeklyTrend: List<ChartPoint>,
    val monthlyTrend: List<ChartPoint>,
    val categoryDistribution: List<CategoryBreakdown>,
    val topTasks: List<TaskStat>,
    val lowestCompletedTasks: List<TaskStat>,
    val heatmap: Map<LocalDate, Int>,
    val bestDay: LocalDate?,
    val bestWeekLabel: String?,
    val bestMonth: YearMonth?,
)
