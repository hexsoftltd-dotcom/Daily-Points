package com.hexsoft.dailypoints.domain.usecase.streak

import com.hexsoft.dailypoints.domain.model.TaskCompletionDetail
import com.hexsoft.dailypoints.testdoubles.FakeCompletionRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class StreakUseCasesTest {
    @Test
    fun `computes current and longest streak`() = runTest {
        val repository = FakeCompletionRepository()
        val today = LocalDate.of(2026, 10, 29)
        repository.completions += listOf(
            TaskCompletionDetail(1, 1, "Exercise", "Health", 20, today.minusDays(3), true, 0),
            TaskCompletionDetail(2, 1, "Exercise", "Health", 20, today.minusDays(2), true, 0),
            TaskCompletionDetail(3, 1, "Exercise", "Health", 20, today.minusDays(1), true, 0),
            TaskCompletionDetail(4, 1, "Exercise", "Health", 20, today, true, 0),
        )
        val current = GetCurrentStreakUseCase(repository)(today)
        val longest = GetLongestStreakUseCase(repository)()
        assertEquals(4, current)
        assertEquals(4, longest)
    }
}
