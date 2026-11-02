package com.hexsoft.dailypoints.presentation.daily

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexsoft.dailypoints.domain.model.Task
import com.hexsoft.dailypoints.domain.usecase.completion.CompleteTaskUseCase
import com.hexsoft.dailypoints.domain.usecase.completion.GetDailyCompletionUseCase
import com.hexsoft.dailypoints.domain.usecase.completion.UncompleteTaskUseCase
import com.hexsoft.dailypoints.domain.usecase.points.GetDailyPointsUseCase
import com.hexsoft.dailypoints.domain.usecase.task.GetTasksUseCase
import com.hexsoft.dailypoints.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class DailyTaskItem(
    val task: Task,
    val completed: Boolean,
)

data class DailyTasksUiState(
    val selectedDate: LocalDate = DateUtils.today(),
    val points: Int = 0,
    val items: List<DailyTaskItem> = emptyList(),
)

@HiltViewModel
class DailyTasksViewModel @Inject constructor(
    getTasksUseCase: GetTasksUseCase,
    private val getDailyCompletionUseCase: GetDailyCompletionUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val uncompleteTaskUseCase: UncompleteTaskUseCase,
    private val getDailyPointsUseCase: GetDailyPointsUseCase,
) : ViewModel() {

    private val selectedDate = MutableStateFlow(DateUtils.today())
    private val points = MutableStateFlow(0)

    private val itemsFlow = combine(
        getTasksUseCase(activeOnly = true),
        selectedDate.flatMapLatest { date -> getDailyCompletionUseCase(date) },
        selectedDate,
    ) { tasks, completions, date ->
        val completedTaskIds = completions.filter { it.completed && it.date == date }.map { it.taskId }.toSet()
        tasks.map { task -> DailyTaskItem(task = task, completed = completedTaskIds.contains(task.id)) }
    }

    val uiState: StateFlow<DailyTasksUiState> = combine(
        selectedDate,
        points,
        itemsFlow,
    ) { date, total, items ->
        DailyTasksUiState(
            selectedDate = date,
            points = total,
            items = items,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DailyTasksUiState())

    init {
        refreshPoints()
    }

    fun selectDate(date: LocalDate) {
        selectedDate.value = date
        refreshPoints()
    }

    fun toggleTask(item: DailyTaskItem) {
        viewModelScope.launch {
            val date = selectedDate.value
            if (item.completed) {
                uncompleteTaskUseCase(item.task.id, date)
            } else {
                completeTaskUseCase(item.task.id, date)
            }
            refreshPoints()
        }
    }

    private fun refreshPoints() {
        viewModelScope.launch {
            points.value = getDailyPointsUseCase(selectedDate.value)
        }
    }
}
