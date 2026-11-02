package com.hexsoft.dailypoints.data.analytics

import com.hexsoft.dailypoints.domain.model.AnalyticsSummary
import com.hexsoft.dailypoints.domain.model.CategoryBreakdown
import com.hexsoft.dailypoints.domain.model.ChartPoint
import com.hexsoft.dailypoints.domain.model.TaskCompletionDetail
import com.hexsoft.dailypoints.domain.model.TaskStat
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject

class AnalyticsService @Inject constructor() {

    fun buildSummary(
        completions: List<TaskCompletionDetail>,
        from: LocalDate,
        to: LocalDate,
    ): AnalyticsSummary {
        val byDate = completions.groupBy { it.date }
        val dayPoints = byDate.mapValues { (_, values) -> values.sumOf { it.points } }
        val weekFields = WeekFields.of(Locale.getDefault())
        val byWeek = completions.groupBy {
            val year = it.date.get(weekFields.weekBasedYear())
            val week = it.date.get(weekFields.weekOfWeekBasedYear())
            "$year-W$week"
        }
        val byMonth = completions.groupBy { YearMonth.from(it.date) }
        val byCategory = completions.groupBy { it.category }
        val byTask = completions.groupBy { it.taskId to it.taskName }

        val dailyTrend = dayPoints.entries.sortedBy { it.key }.map {
            ChartPoint(it.key.toString(), it.value.toFloat())
        }
        val weeklyTrend = byWeek.entries.sortedBy { it.key }.map {
            ChartPoint(it.key, it.value.sumOf { detail -> detail.points }.toFloat())
        }
        val monthlyTrend = byMonth.entries.sortedBy { it.key }.map {
            ChartPoint(it.key.toString(), it.value.sumOf { detail -> detail.points }.toFloat())
        }
        val categoryDistribution = byCategory.entries
            .map { (category, values) -> CategoryBreakdown(category, values.sumOf { it.points }) }
            .sortedByDescending { it.points }

        val taskStats = byTask.entries.map { (entry, values) ->
            TaskStat(
                taskId = entry.first,
                taskName = entry.second,
                completionCount = values.size,
            )
        }.sortedByDescending { it.completionCount }

        val bestDay = dayPoints.maxByOrNull { it.value }?.key
        val bestWeek = byWeek.maxByOrNull { it.value.sumOf { detail -> detail.points } }?.key
        val bestMonth = byMonth.maxByOrNull { it.value.sumOf { detail -> detail.points } }?.key

        val heatmap = generateSequence(from) { if (it.isBefore(to)) it.plusDays(1) else null }
            .toList()
            .associateWith { date -> dayPoints[date] ?: 0 }

        return AnalyticsSummary(
            dailyTrend = dailyTrend,
            weeklyTrend = weeklyTrend,
            monthlyTrend = monthlyTrend,
            categoryDistribution = categoryDistribution,
            topTasks = taskStats.take(5),
            lowestCompletedTasks = taskStats.sortedBy { it.completionCount }.take(5),
            heatmap = heatmap,
            bestDay = bestDay,
            bestWeekLabel = bestWeek,
            bestMonth = bestMonth,
        )
    }
}
