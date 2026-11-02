package com.hexsoft.dailypoints.domain.repository

import com.hexsoft.dailypoints.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeTasks(): Flow<List<Task>>
    fun observeActiveTasks(): Flow<List<Task>>
    suspend fun getTaskById(id: Long): Task?
    suspend fun addTask(task: Task): Long
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(id: Long)
    suspend fun setTaskActive(id: Long, active: Boolean)
    suspend fun getTaskCount(): Int
}
