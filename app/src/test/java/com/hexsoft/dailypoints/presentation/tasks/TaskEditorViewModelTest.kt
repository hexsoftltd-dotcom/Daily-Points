package com.hexsoft.dailypoints.presentation.tasks

import androidx.lifecycle.SavedStateHandle
import com.hexsoft.dailypoints.domain.usecase.task.AddTaskUseCase
import com.hexsoft.dailypoints.domain.usecase.task.GetTaskByIdUseCase
import com.hexsoft.dailypoints.domain.usecase.task.UpdateTaskUseCase
import com.hexsoft.dailypoints.testdoubles.FakeTaskRepository
import com.hexsoft.dailypoints.testdoubles.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskEditorViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `saves task successfully`() = runTest {
        val repository = FakeTaskRepository()
        val viewModel = TaskEditorViewModel(
            savedStateHandle = SavedStateHandle(),
            addTaskUseCase = AddTaskUseCase(repository),
            updateTaskUseCase = UpdateTaskUseCase(repository),
            getTaskByIdUseCase = GetTaskByIdUseCase(repository),
        )

        viewModel.updateName("Exercise")
        viewModel.updateDescription("Morning routine")
        viewModel.updateCategory("Health")
        viewModel.updatePoints("20")
        viewModel.save()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.savedSuccessfully)
    }
}
