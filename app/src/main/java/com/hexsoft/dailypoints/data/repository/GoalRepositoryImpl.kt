package com.hexsoft.dailypoints.data.repository

import com.hexsoft.dailypoints.data.local.dao.GoalDao
import com.hexsoft.dailypoints.domain.model.Goal
import com.hexsoft.dailypoints.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao,
) : GoalRepository {
    override fun observeGoal(): Flow<Goal> {
        return goalDao.observeGoal().map { entity ->
            entity?.toDomain() ?: Goal(dailyGoal = 50, monthlyGoal = 1000)
        }
    }

    override suspend fun getGoal(): Goal {
        return goalDao.getGoal()?.toDomain() ?: Goal(dailyGoal = 50, monthlyGoal = 1000)
    }

    override suspend fun saveGoal(goal: Goal) {
        goalDao.upsert(goal.toEntity())
    }
}
