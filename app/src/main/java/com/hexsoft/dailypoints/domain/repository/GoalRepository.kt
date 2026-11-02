package com.hexsoft.dailypoints.domain.repository

import com.hexsoft.dailypoints.domain.model.Goal
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    fun observeGoal(): Flow<Goal>
    suspend fun getGoal(): Goal
    suspend fun saveGoal(goal: Goal)
}
