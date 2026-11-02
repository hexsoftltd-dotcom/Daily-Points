package com.hexsoft.dailypoints.domain.usecase.streak

import com.hexsoft.dailypoints.domain.repository.CompletionRepository
import com.hexsoft.dailypoints.utils.DateUtils
import java.time.LocalDate
import javax.inject.Inject

class GetCurrentStreakUseCase @Inject constructor(
    private val completionRepository: CompletionRepository,
) {
    suspend operator fun invoke(referenceDate: LocalDate = DateUtils.today()): Int {
        val dates = completionRepository.getCompletedDates().toSet()
        var cursor = referenceDate
        var streak = 0
        while (dates.contains(cursor)) {
            streak++
            cursor = cursor.minusDays(1)
        }
        return streak
    }
}

class GetLongestStreakUseCase @Inject constructor(
    private val completionRepository: CompletionRepository,
) {
    suspend operator fun invoke(): Int {
        val dates = completionRepository.getCompletedDates().distinct().sorted()
        if (dates.isEmpty()) return 0
        var longest = 1
        var current = 1
        for (index in 1 until dates.size) {
            if (dates[index - 1].plusDays(1) == dates[index]) {
                current++
                if (current > longest) {
                    longest = current
                }
            } else {
                current = 1
            }
        }
        return longest
    }
}
