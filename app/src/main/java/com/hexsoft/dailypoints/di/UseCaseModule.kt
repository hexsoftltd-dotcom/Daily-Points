package com.hexsoft.dailypoints.di

import com.hexsoft.dailypoints.domain.usecase.CompletionUseCases
import com.hexsoft.dailypoints.domain.usecase.TaskUseCases
import com.hexsoft.dailypoints.domain.usecase.completion.CompleteTaskUseCase
import com.hexsoft.dailypoints.domain.usecase.completion.GetDailyCompletionUseCase
import com.hexsoft.dailypoints.domain.usecase.completion.UncompleteTaskUseCase
import com.hexsoft.dailypoints.domain.usecase.task.AddTaskUseCase
import com.hexsoft.dailypoints.domain.usecase.task.DeleteTaskUseCase
import com.hexsoft.dailypoints.domain.usecase.task.GetTaskByIdUseCase
import com.hexsoft.dailypoints.domain.usecase.task.GetTasksUseCase
import com.hexsoft.dailypoints.domain.usecase.task.SetTaskEnabledUseCase
import com.hexsoft.dailypoints.domain.usecase.task.UpdateTaskUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    fun provideTaskUseCases(
        addTaskUseCase: AddTaskUseCase,
        updateTaskUseCase: UpdateTaskUseCase,
        deleteTaskUseCase: DeleteTaskUseCase,
        getTasksUseCase: GetTasksUseCase,
        getTaskByIdUseCase: GetTaskByIdUseCase,
        setTaskEnabledUseCase: SetTaskEnabledUseCase,
    ): TaskUseCases {
        return TaskUseCases(
            addTask = addTaskUseCase,
            updateTask = updateTaskUseCase,
            deleteTask = deleteTaskUseCase,
            getTasks = getTasksUseCase,
            getTaskById = getTaskByIdUseCase,
            setTaskEnabled = setTaskEnabledUseCase,
        )
    }

    @Provides
    fun provideCompletionUseCases(
        completeTaskUseCase: CompleteTaskUseCase,
        uncompleteTaskUseCase: UncompleteTaskUseCase,
        getDailyCompletionUseCase: GetDailyCompletionUseCase,
    ): CompletionUseCases {
        return CompletionUseCases(
            completeTask = completeTaskUseCase,
            uncompleteTask = uncompleteTaskUseCase,
            getDailyCompletion = getDailyCompletionUseCase,
        )
    }
}
