package com.hexsoft.dailypoints.domain.usecase.planning

import com.hexsoft.dailypoints.domain.model.PlannedTask
import com.hexsoft.dailypoints.domain.repository.PlannedTaskRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class GetPlannedTasksUseCase @Inject constructor(
    private val plannedTaskRepository: PlannedTaskRepository,
) {
    operator fun invoke(date: LocalDate): Flow<List<PlannedTask>> {
        return plannedTaskRepository.observePlannedTasks(date)
    }
}

class AddPlannedTaskUseCase @Inject constructor(
    private val plannedTaskRepository: PlannedTaskRepository,
) {
    suspend operator fun invoke(taskId: Long, date: LocalDate): Long {
        require(date.isAfter(LocalDate.now()) || date == LocalDate.now()) {
            "Planning date cannot be in the past."
        }
        return plannedTaskRepository.addPlannedTask(taskId, date)
    }
}

class DeletePlannedTaskUseCase @Inject constructor(
    private val plannedTaskRepository: PlannedTaskRepository,
) {
    suspend operator fun invoke(id: Long) = plannedTaskRepository.removePlannedTask(id)
}
