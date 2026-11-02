package com.hexsoft.dailypoints.data.repository

import com.hexsoft.dailypoints.data.local.dao.PlannedTaskDao
import com.hexsoft.dailypoints.data.local.entities.PlannedTaskEntity
import com.hexsoft.dailypoints.domain.model.PlannedTask
import com.hexsoft.dailypoints.domain.repository.PlannedTaskRepository
import com.hexsoft.dailypoints.domain.repository.SettingsRepository
import com.hexsoft.dailypoints.utils.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class PlannedTaskRepositoryImpl @Inject constructor(
    private val plannedTaskDao: PlannedTaskDao,
    private val settingsRepository: SettingsRepository,
) : PlannedTaskRepository {
    override fun observePlannedTasks(date: LocalDate): Flow<List<PlannedTask>> {
        val dateKey = DateUtils.toDbString(date)
        return settingsRepository.settings.flatMapLatest { settings ->
            val profileId = settings.activeProfileId
            if (profileId == null) {
                flowOf(emptyList())
            } else {
                plannedTaskDao.observePlannedTasks(dateKey, profileId)
                    .map { entities -> entities.map { it.toDomain() } }
            }
        }
    }

    override suspend fun addPlannedTask(taskId: Long, date: LocalDate): Long {
        requireActiveProfileId()
        return plannedTaskDao.insert(
            PlannedTaskEntity(
                taskId = taskId,
                plannedDate = DateUtils.toDbString(date),
            ),
        )
    }

    override suspend fun removePlannedTask(id: Long) {
        plannedTaskDao.deleteById(id)
    }

    private suspend fun requireActiveProfileId(): Long {
        return settingsRepository.settings.first().activeProfileId
            ?: throw IllegalStateException("Please create and select a profile first.")
    }
}
