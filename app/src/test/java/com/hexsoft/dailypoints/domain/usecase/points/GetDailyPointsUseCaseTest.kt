package com.hexsoft.dailypoints.domain.usecase.points

import com.hexsoft.dailypoints.domain.model.TaskCompletionDetail
import com.hexsoft.dailypoints.testdoubles.FakeCompletionRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class GetDailyPointsUseCaseTest {
    @Test
    fun `returns daily points from completed tasks`() = runTest {
        val repository = FakeCompletionRepository()
        val targetDate = LocalDate.of(2026, 10, 29)
        repository.completions += listOf(
            TaskCompletionDetail(1, 1, "Exercise", "Health", 20, targetDate, true, 0L),
            TaskCompletionDetail(2, 2, "Reading", "Learning", 10, targetDate, true, 0L),
            TaskCompletionDetail(3, 3, "Study", "Learning", 25, targetDate, false, 0L),
        )

        val useCase = GetDailyPointsUseCase(repository)
        val result = useCase(targetDate)

        assertEquals(30, result)
    }
}
