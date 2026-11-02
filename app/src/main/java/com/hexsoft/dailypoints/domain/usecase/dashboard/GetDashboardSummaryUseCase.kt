package com.hexsoft.dailypoints.domain.usecase.dashboard

import com.hexsoft.dailypoints.domain.model.DashboardSummary
import com.hexsoft.dailypoints.domain.repository.CompletionRepository
import com.hexsoft.dailypoints.domain.repository.ProfileRepository
import com.hexsoft.dailypoints.domain.repository.SettingsRepository
import com.hexsoft.dailypoints.domain.usecase.goals.GetGoalsUseCase
import com.hexsoft.dailypoints.domain.usecase.points.GetDailyPointsUseCase
import com.hexsoft.dailypoints.domain.usecase.points.GetMonthlyPointsUseCase
import com.hexsoft.dailypoints.domain.usecase.points.GetWeeklyPointsUseCase
import com.hexsoft.dailypoints.domain.usecase.streak.GetCurrentStreakUseCase
import com.hexsoft.dailypoints.domain.usecase.streak.GetLongestStreakUseCase
import com.hexsoft.dailypoints.utils.DateUtils
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

class GetDashboardSummaryUseCase @Inject constructor(
    private val completionRepository: CompletionRepository,
    private val profileRepository: ProfileRepository,
    private val settingsRepository: SettingsRepository,
    private val getGoalsUseCase: GetGoalsUseCase,
    private val getDailyPointsUseCase: GetDailyPointsUseCase,
    private val getWeeklyPointsUseCase: GetWeeklyPointsUseCase,
    private val getMonthlyPointsUseCase: GetMonthlyPointsUseCase,
    private val getCurrentStreakUseCase: GetCurrentStreakUseCase,
    private val getLongestStreakUseCase: GetLongestStreakUseCase,
) {
    suspend operator fun invoke(date: LocalDate = DateUtils.today()): DashboardSummary {
        val activeProfileId = settingsRepository.settings.first().activeProfileId
            ?: throw IllegalStateException("Please create and select a profile first.")
        val activeProfile = profileRepository.getById(activeProfileId)
            ?: throw IllegalStateException("Selected profile no longer exists.")
        val goals = getGoalsUseCase().first()
        val dailyPoints = getDailyPointsUseCase(date)
        val weeklyPoints = getWeeklyPointsUseCase(date)
        val monthlyPoints = getMonthlyPointsUseCase(date)
        val progress = if (goals.dailyGoal == 0) 0f else (dailyPoints.toFloat() / goals.dailyGoal.toFloat() * 100f)
        val yearlyCompletions = completionRepository.getCompletionsInRange(
            LocalDate.of(date.year, 1, 1),
            LocalDate.of(date.year, 12, 31),
        ).filter { it.completed }
        val bestDay = yearlyCompletions
            .groupBy { it.date }
            .maxByOrNull { (_, values) -> values.sumOf { it.points } }
            ?.key
        val bestMonth = yearlyCompletions
            .groupBy { "${it.date.year}-${it.date.monthValue.toString().padStart(2, '0')}" }
            .maxByOrNull { (_, values) -> values.sumOf { it.points } }
            ?.key
        val profilePoints = completionRepository.getAllTimeCompletedPoints() + activeProfile.extraPoints
        return DashboardSummary(
            profileName = activeProfile.name,
            profilePoints = profilePoints,
            todayPoints = dailyPoints,
            todayGoal = goals.dailyGoal,
            todayProgressPercentage = progress.coerceAtMost(100f),
            remainingPoints = (goals.dailyGoal - dailyPoints).coerceAtLeast(0),
            weeklyTotal = weeklyPoints,
            weeklyAverage = weeklyPoints / 7f,
            monthlyTotal = monthlyPoints,
            monthlyAverage = monthlyPoints / date.lengthOfMonth().toFloat(),
            currentStreak = getCurrentStreakUseCase(date),
            longestStreak = getLongestStreakUseCase(),
            bestDay = bestDay,
            bestMonth = bestMonth,
        )
    }
}
