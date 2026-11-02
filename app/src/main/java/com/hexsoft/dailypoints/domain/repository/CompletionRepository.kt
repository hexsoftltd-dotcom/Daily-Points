package com.hexsoft.dailypoints.domain.repository

import com.hexsoft.dailypoints.domain.model.TaskCompletion
import com.hexsoft.dailypoints.domain.model.TaskCompletionDetail
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface CompletionRepository {
    fun observeDailyCompletions(date: LocalDate): Flow<List<TaskCompletion>>
    suspend fun completeTask(taskId: Long, date: LocalDate)
    suspend fun uncompleteTask(taskId: Long, date: LocalDate)
    suspend fun isTaskCompleted(taskId: Long, date: LocalDate): Boolean
    suspend fun getDailyPoints(date: LocalDate): Int
    fun observeDailyPoints(date: LocalDate): Flow<Int>
    suspend fun getPointsInRange(startDate: LocalDate, endDate: LocalDate): Int
    suspend fun getCompletionsInRange(startDate: LocalDate, endDate: LocalDate): List<TaskCompletionDetail>
    suspend fun getCompletedDates(): List<LocalDate>
    suspend fun getAllTimeCompletedPoints(): Int
}
