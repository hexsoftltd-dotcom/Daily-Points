package com.hexsoft.dailypoints.presentation.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexsoft.dailypoints.domain.model.Task
import com.hexsoft.dailypoints.domain.usecase.task.DeleteTaskUseCase
import com.hexsoft.dailypoints.domain.usecase.task.GetTasksUseCase
import com.hexsoft.dailypoints.domain.usecase.task.SetTaskEnabledUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskListUiState(
    val searchQuery: String = "",
    val categoryFilter: String = "All",
    val tasks: List<Task> = emptyList(),
)

@HiltViewModel
class TaskListViewModel @Inject constructor(
    getTasksUseCase: GetTasksUseCase,
    private val setTaskEnabledUseCase: SetTaskEnabledUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val categoryFilter = MutableStateFlow("All")

    val uiState: StateFlow<TaskListUiState> = combine(
        getTasksUseCase(),
        searchQuery,
        categoryFilter,
    ) { tasks, query, filter ->
        val filtered = tasks.filter { task ->
            val matchesQuery = query.isBlank() ||
                task.name.contains(query, ignoreCase = true) ||
                task.description.contains(query, ignoreCase = true)
            val matchesCategory = filter == "All" || task.category.equals(filter, ignoreCase = true)
            matchesQuery && matchesCategory
        }
        TaskListUiState(
            searchQuery = query,
            categoryFilter = filter,
            tasks = filtered,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TaskListUiState())

    fun onSearchQueryChanged(value: String) {
        searchQuery.value = value
    }

    fun onCategoryFilterChanged(value: String) {
        categoryFilter.value = value
    }

    fun toggleTaskStatus(task: Task) {
        viewModelScope.launch {
            setTaskEnabledUseCase(task.id, !task.active)
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            deleteTaskUseCase(taskId)
        }
    }
}
