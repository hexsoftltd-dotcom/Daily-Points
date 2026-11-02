package com.hexsoft.dailypoints.data.repository

import com.hexsoft.dailypoints.data.local.dao.TaskDao
import com.hexsoft.dailypoints.domain.model.Task
import com.hexsoft.dailypoints.domain.repository.SettingsRepository
import com.hexsoft.dailypoints.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val settingsRepository: SettingsRepository,
) : TaskRepository {
    override fun observeTasks(): Flow<List<Task>> =
        settingsRepository.settings.flatMapLatest { settings ->
            val profileId = settings.activeProfileId
            if (profileId == null) {
                flowOf(emptyList())
            } else {
                taskDao.observeTasks(profileId).map { entities -> entities.map { it.toDomain() } }
            }
        }

    override fun observeActiveTasks(): Flow<List<Task>> =
        settingsRepository.settings.flatMapLatest { settings ->
            val profileId = settings.activeProfileId
            if (profileId == null) {
                flowOf(emptyList())
            } else {
                taskDao.observeActiveTasks(profileId).map { entities -> entities.map { it.toDomain() } }
            }
        }

    override suspend fun getTaskById(id: Long): Task? {
        val profileId = requireActiveProfileId()
        return taskDao.getTaskById(id, profileId)?.toDomain()
    }

    override suspend fun addTask(task: Task): Long {
        val profileId = requireActiveProfileId()
        return taskDao.insert(task.copy(profileId = profileId).toEntity())
    }

    override suspend fun updateTask(task: Task) {
        requireActiveProfileId()
        taskDao.update(task.toEntity())
    }

    override suspend fun deleteTask(id: Long) {
        val profileId = requireActiveProfileId()
        val existing = taskDao.getTaskById(id, profileId) ?: return
        taskDao.delete(existing)
    }

    override suspend fun setTaskActive(id: Long, active: Boolean) {
        val profileId = requireActiveProfileId()
        taskDao.setTaskActive(id, active, profileId)
    }

    override suspend fun getTaskCount(): Int {
        val profileId = requireActiveProfileId()
        return taskDao.getTaskCount(profileId)
    }

    private suspend fun requireActiveProfileId(): Long {
        return settingsRepository.settings.first().activeProfileId
            ?: throw IllegalStateException("Please create and select a profile first.")
    }
}
