package com.hexsoft.dailypoints.data.repository

import com.hexsoft.dailypoints.data.local.dao.TaskCompletionDao
import com.hexsoft.dailypoints.data.local.entities.TaskCompletionEntity
import com.hexsoft.dailypoints.domain.model.TaskCompletion
import com.hexsoft.dailypoints.domain.model.TaskCompletionDetail
import com.hexsoft.dailypoints.domain.repository.CompletionRepository
import com.hexsoft.dailypoints.domain.repository.SettingsRepository
import com.hexsoft.dailypoints.utils.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class CompletionRepositoryImpl @Inject constructor(
    private val completionDao: TaskCompletionDao,
    private val settingsRepository: SettingsRepository,
) : CompletionRepository {
    override fun observeDailyCompletions(date: LocalDate): Flow<List<TaskCompletion>> {
        val dateKey = DateUtils.toDbString(date)
        return settingsRepository.settings.flatMapLatest { settings ->
            val profileId = settings.activeProfileId
            if (profileId == null) {
                flowOf(emptyList())
            } else {
                completionDao.observeCompletionsForDate(dateKey, profileId)
                    .map { entities -> entities.map { it.toDomain() } }
            }
        }
    }

    override suspend fun completeTask(taskId: Long, date: LocalDate) {
        completionDao.upsertCompletion(
            TaskCompletionEntity(
                taskId = taskId,
                date = DateUtils.toDbString(date),
                completed = true,
                completedAt = System.currentTimeMillis(),
            ),
        )
    }

    override suspend fun uncompleteTask(taskId: Long, date: LocalDate) {
        completionDao.deleteCompletion(taskId, DateUtils.toDbString(date))
    }

    override suspend fun isTaskCompleted(taskId: Long, date: LocalDate): Boolean {
        return completionDao.getCompletion(taskId, DateUtils.toDbString(date))?.completed == true
    }

    override suspend fun getDailyPoints(date: LocalDate): Int {
        val profileId = requireActiveProfileId()
        return completionDao.getPointsForDate(DateUtils.toDbString(date), profileId)
    }

    override fun observeDailyPoints(date: LocalDate): Flow<Int> {
        val dateKey = DateUtils.toDbString(date)
        return settingsRepository.settings.flatMapLatest { settings ->
            val profileId = settings.activeProfileId
            if (profileId == null) {
                flowOf(0)
            } else {
                completionDao.observePointsForDate(dateKey, profileId)
            }
        }
    }

    override suspend fun getPointsInRange(startDate: LocalDate, endDate: LocalDate): Int {
        val profileId = requireActiveProfileId()
        return completionDao.getPointsInRange(
            DateUtils.toDbString(startDate),
            DateUtils.toDbString(endDate),
            profileId,
        )
    }

    override suspend fun getCompletionsInRange(
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<TaskCompletionDetail> {
        val profileId = requireActiveProfileId()
        return completionDao.getCompletionsWithTasksInRange(
            DateUtils.toDbString(startDate),
            DateUtils.toDbString(endDate),
            profileId,
        ).map { relation ->
            TaskCompletionDetail(
                completionId = relation.completion.id,
                taskId = relation.task.id,
                taskName = relation.task.name,
                category = relation.task.category,
                points = relation.task.points,
                date = DateUtils.fromDbString(relation.completion.date),
                completed = relation.completion.completed,
                completedAt = relation.completion.completedAt,
            )
        }
    }

    override suspend fun getCompletedDates(): List<LocalDate> {
        val profileId = requireActiveProfileId()
        return completionDao.getCompletedDates(profileId).map { DateUtils.fromDbString(it) }
    }

    override suspend fun getAllTimeCompletedPoints(): Int {
        val profileId = requireActiveProfileId()
        return completionDao.getAllTimeCompletedPoints(profileId)
    }

    private suspend fun requireActiveProfileId(): Long {
        return settingsRepository.settings.first().activeProfileId
            ?: throw IllegalStateException("Please create and select a profile first.")
    }
}
