package com.hexsoft.dailypoints.domain.usecase

import com.hexsoft.dailypoints.domain.usecase.completion.CompleteTaskUseCase
import com.hexsoft.dailypoints.domain.usecase.completion.GetDailyCompletionUseCase
import com.hexsoft.dailypoints.domain.usecase.completion.UncompleteTaskUseCase
import com.hexsoft.dailypoints.domain.usecase.task.AddTaskUseCase
import com.hexsoft.dailypoints.domain.usecase.task.DeleteTaskUseCase
import com.hexsoft.dailypoints.domain.usecase.task.GetTaskByIdUseCase
import com.hexsoft.dailypoints.domain.usecase.task.GetTasksUseCase
import com.hexsoft.dailypoints.domain.usecase.task.SetTaskEnabledUseCase
import com.hexsoft.dailypoints.domain.usecase.task.UpdateTaskUseCase

data class TaskUseCases(
    val addTask: AddTaskUseCase,
    val updateTask: UpdateTaskUseCase,
    val deleteTask: DeleteTaskUseCase,
    val getTasks: GetTasksUseCase,
    val getTaskById: GetTaskByIdUseCase,
    val setTaskEnabled: SetTaskEnabledUseCase,
)

data class CompletionUseCases(
    val completeTask: CompleteTaskUseCase,
    val uncompleteTask: UncompleteTaskUseCase,
    val getDailyCompletion: GetDailyCompletionUseCase,
)
