package com.hexsoft.dailypoints.domain.usecase.points

import com.hexsoft.dailypoints.domain.repository.CompletionRepository
import com.hexsoft.dailypoints.utils.DateUtils
import java.time.LocalDate
import javax.inject.Inject

class GetDailyPointsUseCase @Inject constructor(
    private val completionRepository: CompletionRepository,
) {
    suspend operator fun invoke(date: LocalDate): Int {
        return completionRepository.getDailyPoints(date)
    }
}

class GetWeeklyPointsUseCase @Inject constructor(
    private val completionRepository: CompletionRepository,
) {
    suspend operator fun invoke(date: LocalDate): Int {
        val start = DateUtils.startOfWeek(date)
        val end = DateUtils.endOfWeek(date)
        return completionRepository.getPointsInRange(start, end)
    }
}

class GetMonthlyPointsUseCase @Inject constructor(
    private val completionRepository: CompletionRepository,
) {
    suspend operator fun invoke(date: LocalDate): Int {
        val start = DateUtils.startOfMonth(date)
        val end = DateUtils.endOfMonth(date)
        return completionRepository.getPointsInRange(start, end)
    }
}

class GetYearlyPointsUseCase @Inject constructor(
    private val completionRepository: CompletionRepository,
) {
    suspend operator fun invoke(date: LocalDate): Int {
        val start = LocalDate.of(date.year, 1, 1)
        val end = LocalDate.of(date.year, 12, 31)
        return completionRepository.getPointsInRange(start, end)
    }
}
