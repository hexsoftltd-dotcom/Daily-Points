package com.hexsoft.dailypoints.domain.repository

import com.hexsoft.dailypoints.domain.model.PlannedTask
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface PlannedTaskRepository {
    fun observePlannedTasks(date: LocalDate): Flow<List<PlannedTask>>
    suspend fun addPlannedTask(taskId: Long, date: LocalDate): Long
    suspend fun removePlannedTask(id: Long)
}
