package com.hexsoft.dailypoints.domain.usecase.analytics

import com.hexsoft.dailypoints.data.analytics.AnalyticsService
import com.hexsoft.dailypoints.domain.model.AnalyticsSummary
import com.hexsoft.dailypoints.domain.repository.CompletionRepository
import java.time.LocalDate
import javax.inject.Inject

class GetAnalyticsSummaryUseCase @Inject constructor(
    private val completionRepository: CompletionRepository,
    private val analyticsService: AnalyticsService,
) {
    suspend operator fun invoke(
        from: LocalDate,
        to: LocalDate,
    ): AnalyticsSummary {
        val completions = completionRepository.getCompletionsInRange(from, to)
        return analyticsService.buildSummary(completions, from, to)
    }
}
