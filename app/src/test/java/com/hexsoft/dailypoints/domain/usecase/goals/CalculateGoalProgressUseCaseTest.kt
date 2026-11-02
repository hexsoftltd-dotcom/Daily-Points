package com.hexsoft.dailypoints.domain.usecase.goals

import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateGoalProgressUseCaseTest {
    @Test
    fun `calculates progress and remaining`() {
        val useCase = CalculateGoalProgressUseCase()
        val progress = useCase(target = 100, achieved = 35)
        assertEquals(65, progress.remaining)
        assertEquals(35f, progress.percentage)
    }
}
