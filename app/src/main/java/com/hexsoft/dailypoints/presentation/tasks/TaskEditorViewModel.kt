package com.hexsoft.dailypoints.presentation.tasks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexsoft.dailypoints.domain.model.Task
import com.hexsoft.dailypoints.domain.usecase.task.AddTaskUseCase
import com.hexsoft.dailypoints.domain.usecase.task.GetTaskByIdUseCase
import com.hexsoft.dailypoints.domain.usecase.task.UpdateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskEditorUiState(
    val id: Long = 0L,
    val profileId: Long = 0L,
    val createdAt: Long = 0L,
    val active: Boolean = true,
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val points: String = "",
    val isEditMode: Boolean = false,
    val errorMessage: String? = null,
    val savedSuccessfully: Boolean = false,
)

@HiltViewModel
class TaskEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val addTaskUseCase: AddTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TaskEditorUiState())
    val uiState: StateFlow<TaskEditorUiState> = _uiState.asStateFlow()

    init {
        val taskId = savedStateHandle.get<String>("taskId")?.toLongOrNull()
        if (taskId != null) {
            loadTask(taskId)
        }
    }

    private fun loadTask(taskId: Long) {
        viewModelScope.launch {
            val task = getTaskByIdUseCase(taskId) ?: return@launch
            _uiState.value = TaskEditorUiState(
                id = task.id,
                profileId = task.profileId,
                createdAt = task.createdAt,
                active = task.active,
                name = task.name,
                description = task.description,
                category = task.category,
                points = task.points.toString(),
                isEditMode = true,
            )
        }
    }

    fun updateName(value: String) {
        _uiState.value = _uiState.value.copy(name = value, errorMessage = null)
    }

    fun updateDescription(value: String) {
        _uiState.value = _uiState.value.copy(description = value, errorMessage = null)
    }

    fun updateCategory(value: String) {
        _uiState.value = _uiState.value.copy(category = value, errorMessage = null)
    }

    fun updatePoints(value: String) {
        _uiState.value = _uiState.value.copy(points = value, errorMessage = null)
    }

    fun save() {
        viewModelScope.launch {
            val state = _uiState.value
            val points = state.points.toIntOrNull()
            if (points == null || points <= 0) {
                _uiState.value = state.copy(errorMessage = "Points must be greater than zero.")
                return@launch
            }
            if (state.name.isBlank()) {
                _uiState.value = state.copy(errorMessage = "Task name is required.")
                return@launch
            }

            try {
                if (state.isEditMode) {
                    updateTaskUseCase(
                        Task(
                            id = state.id,
                            profileId = state.profileId,
                            name = state.name,
                            description = state.description,
                            category = state.category,
                            points = points,
                            active = state.active,
                            createdAt = state.createdAt,
                        ),
                    )
                } else {
                    addTaskUseCase(
                        name = state.name,
                        description = state.description,
                        category = state.category,
                        points = points,
                    )
                }
                _uiState.value = state.copy(savedSuccessfully = true)
            } catch (throwable: Throwable) {
                _uiState.value = state.copy(errorMessage = throwable.message ?: "Unable to save task.")
            }
        }
    }
}
