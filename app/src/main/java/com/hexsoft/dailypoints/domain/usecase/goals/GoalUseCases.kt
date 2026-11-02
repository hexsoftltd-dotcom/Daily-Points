package com.hexsoft.dailypoints.domain.usecase.goals

import com.hexsoft.dailypoints.domain.model.Goal
import com.hexsoft.dailypoints.domain.model.GoalProgress
import com.hexsoft.dailypoints.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SaveGoalsUseCase @Inject constructor(
    private val goalRepository: GoalRepository,
) {
    suspend operator fun invoke(dailyGoal: Int, monthlyGoal: Int) {
        require(dailyGoal > 0) { "Daily goal must be greater than zero." }
        require(monthlyGoal > 0) { "Monthly goal must be greater than zero." }
        goalRepository.saveGoal(
            Goal(dailyGoal = dailyGoal, monthlyGoal = monthlyGoal),
        )
    }
}

class GetGoalsUseCase @Inject constructor(
    private val goalRepository: GoalRepository,
) {
    operator fun invoke(): Flow<Goal> = goalRepository.observeGoal()
}

class CalculateGoalProgressUseCase @Inject constructor() {
    operator fun invoke(target: Int, achieved: Int): GoalProgress {
        val remaining = (target - achieved).coerceAtLeast(0)
        val percentage = if (target == 0) 0f else (achieved.toFloat() / target.toFloat() * 100f).coerceAtMost(100f)
        return GoalProgress(
            target = target,
            achieved = achieved,
            remaining = remaining,
            percentage = percentage,
        )
    }
}
