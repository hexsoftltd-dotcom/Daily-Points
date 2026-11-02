package com.hexsoft.dailypoints.domain.usecase.report

import com.hexsoft.dailypoints.domain.model.DailyReport
import com.hexsoft.dailypoints.domain.model.DateRangeReport
import com.hexsoft.dailypoints.domain.model.MonthlyReport
import com.hexsoft.dailypoints.domain.model.WeeklyReport
import com.hexsoft.dailypoints.domain.model.YearlyReport
import com.hexsoft.dailypoints.domain.repository.CompletionRepository
import com.hexsoft.dailypoints.utils.DateUtils
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

class GenerateDailyReportUseCase @Inject constructor(
    private val completionRepository: CompletionRepository,
) {
    suspend operator fun invoke(date: LocalDate): DailyReport {
        val completions = completionRepository.getCompletionsInRange(date, date)
        return DailyReport(
            date = date,
            totalPoints = completions.sumOf { if (it.completed) it.points else 0 },
            completions = completions,
        )
    }
}

class GenerateWeeklyReportUseCase @Inject constructor(
    private val completionRepository: CompletionRepository,
) {
    suspend operator fun invoke(date: LocalDate): WeeklyReport {
        val start = DateUtils.startOfWeek(date)
        val end = DateUtils.endOfWeek(date)
        val completions = completionRepository.getCompletionsInRange(start, end)
        val totalPoints = completions.sumOf { if (it.completed) it.points else 0 }
        return WeeklyReport(
            startDate = start,
            endDate = end,
            totalPoints = totalPoints,
            averagePoints = totalPoints / 7f,
            completions = completions,
        )
    }
}

class GenerateMonthlyReportUseCase @Inject constructor(
    private val completionRepository: CompletionRepository,
) {
    suspend operator fun invoke(month: YearMonth): MonthlyReport {
        val start = month.atDay(1)
        val end = month.atEndOfMonth()
        val completions = completionRepository.getCompletionsInRange(start, end)
        val totalPoints = completions.sumOf { if (it.completed) it.points else 0 }
        return MonthlyReport(
            month = month,
            totalPoints = totalPoints,
            averagePoints = totalPoints / month.lengthOfMonth().toFloat(),
            completions = completions,
        )
    }
}

class GenerateYearlyReportUseCase @Inject constructor(
    private val completionRepository: CompletionRepository,
) {
    suspend operator fun invoke(year: Int): YearlyReport {
        val start = LocalDate.of(year, 1, 1)
        val end = LocalDate.of(year, 12, 31)
        val completions = completionRepository.getCompletionsInRange(start, end)
        val totalPoints = completions.sumOf { if (it.completed) it.points else 0 }
        return YearlyReport(
            year = year,
            totalPoints = totalPoints,
            averagePoints = totalPoints / start.lengthOfYear().toFloat(),
            completions = completions,
        )
    }
}

class GenerateDateRangeReportUseCase @Inject constructor(
    private val completionRepository: CompletionRepository,
) {
    suspend operator fun invoke(startDate: LocalDate, endDate: LocalDate): DateRangeReport {
        require(!endDate.isBefore(startDate)) { "End date must not be before start date." }
        val completions = completionRepository.getCompletionsInRange(startDate, endDate)
        val totalPoints = completions.sumOf { if (it.completed) it.points else 0 }
        val dayCount = endDate.toEpochDay() - startDate.toEpochDay() + 1
        return DateRangeReport(
            startDate = startDate,
            endDate = endDate,
            totalPoints = totalPoints,
            averagePoints = totalPoints / dayCount.toFloat(),
            completions = completions,
        )
    }
}
