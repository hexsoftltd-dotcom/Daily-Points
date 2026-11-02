package com.hexsoft.dailypoints.domain.usecase.task

import com.hexsoft.dailypoints.domain.model.Task
import com.hexsoft.dailypoints.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
) {
    suspend operator fun invoke(
        name: String,
        description: String,
        category: String,
        points: Int,
    ): Long {
        val normalizedName = name.trim()
        require(normalizedName.isNotEmpty()) { "Task name is required." }
        require(points > 0) { "Points must be greater than zero." }

        return taskRepository.addTask(
            Task(
                profileId = 0L,
                name = normalizedName,
                description = description.trim(),
                category = category.trim(),
                points = points,
                active = true,
                createdAt = System.currentTimeMillis(),
            ),
        )
    }
}

class UpdateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
) {
    suspend operator fun invoke(task: Task) {
        require(task.name.trim().isNotEmpty()) { "Task name is required." }
        require(task.points > 0) { "Points must be greater than zero." }
        taskRepository.updateTask(task.copy(name = task.name.trim(), category = task.category.trim()))
    }
}

class DeleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
) {
    suspend operator fun invoke(taskId: Long) = taskRepository.deleteTask(taskId)
}

class GetTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
) {
    operator fun invoke(activeOnly: Boolean = false): Flow<List<Task>> {
        return if (activeOnly) taskRepository.observeActiveTasks() else taskRepository.observeTasks()
    }
}

class GetTaskByIdUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
) {
    suspend operator fun invoke(taskId: Long): Task? = taskRepository.getTaskById(taskId)
}

class SetTaskEnabledUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
) {
    suspend operator fun invoke(taskId: Long, enabled: Boolean) {
        taskRepository.setTaskActive(taskId, enabled)
    }
}
