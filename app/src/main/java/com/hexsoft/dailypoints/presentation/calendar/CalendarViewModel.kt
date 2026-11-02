package com.hexsoft.dailypoints.presentation.calendar

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexsoft.dailypoints.domain.model.PlannedTask
import com.hexsoft.dailypoints.domain.model.Task
import com.hexsoft.dailypoints.notifications.ReminderScheduler
import com.hexsoft.dailypoints.domain.usecase.goals.GetGoalsUseCase
import com.hexsoft.dailypoints.domain.usecase.planning.AddPlannedTaskUseCase
import com.hexsoft.dailypoints.domain.usecase.planning.DeletePlannedTaskUseCase
import com.hexsoft.dailypoints.domain.usecase.planning.GetPlannedTasksUseCase
import com.hexsoft.dailypoints.domain.usecase.report.GenerateMonthlyReportUseCase
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
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject

data class CalendarDay(
    val date: LocalDate,
    val points: Int,
    val color: Color,
)

data class CalendarUiState(
    val month: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate = DateUtils.today(),
    val days: List<CalendarDay> = emptyList(),
    val completedTaskNames: List<String> = emptyList(),
    val plannedTasks: List<PlannedTask> = emptyList(),
    val activeTasks: List<Task> = emptyList(),
    val reminderEnabled: Boolean = false,
    val reminderTime: String = "20:00",
    val reminderError: String? = null,
)

private data class CalendarBaseData(
    val month: YearMonth,
    val selectedDate: LocalDate,
    val pointsByDate: Map<LocalDate, Int>,
    val completedByDate: Map<LocalDate, List<String>>,
)

private data class CalendarReminderData(
    val enabled: Boolean,
    val time: String,
    val error: String?,
)

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val generateMonthlyReportUseCase: GenerateMonthlyReportUseCase,
    private val getGoalsUseCase: GetGoalsUseCase,
    getPlannedTasksUseCase: GetPlannedTasksUseCase,
    getTasksUseCase: GetTasksUseCase,
    private val addPlannedTaskUseCase: AddPlannedTaskUseCase,
    private val deletePlannedTaskUseCase: DeletePlannedTaskUseCase,
    private val reminderScheduler: ReminderScheduler,
) : ViewModel() {
    private val month = MutableStateFlow(YearMonth.now())
    private val selectedDate = MutableStateFlow(DateUtils.today())
    private val dayPoints = MutableStateFlow<Map<LocalDate, Int>>(emptyMap())
    private val completedByDate = MutableStateFlow<Map<LocalDate, List<String>>>(emptyMap())
    private val reminderEnabled = MutableStateFlow(false)
    private val reminderTime = MutableStateFlow("20:00")
    private val reminderError = MutableStateFlow<String?>(null)
    private val goalFlow = getGoalsUseCase()

    private val baseData = combine(
        month,
        selectedDate,
        dayPoints,
        completedByDate,
    ) { currentMonth, selected, pointsByDate, completedMap ->
        CalendarBaseData(
            month = currentMonth,
            selectedDate = selected,
            pointsByDate = pointsByDate,
            completedByDate = completedMap,
        )
    }

    private val reminderData = combine(
        reminderEnabled,
        reminderTime,
        reminderError,
    ) { enabled, time, error ->
        CalendarReminderData(
            enabled = enabled,
            time = time,
            error = error,
        )
    }

    val uiState: StateFlow<CalendarUiState> = combine(
        baseData,
        selectedDate.flatMapLatest { getPlannedTasksUseCase(it) },
        getTasksUseCase(activeOnly = true),
        goalFlow,
        reminderData,
    ) { base, plans, activeTasks, goal, reminder ->
        val dailyGoal = goal.dailyGoal
        val days = (1..base.month.lengthOfMonth()).map { day ->
            val date = base.month.atDay(day)
            val points = base.pointsByDate[date] ?: 0
            val color = when {
                date.isAfter(LocalDate.now()) -> Color.Gray
                points == 0 -> Color.Red
                points < dailyGoal -> Color(0xFFFFC107)
                else -> Color(0xFF4CAF50)
            }
            CalendarDay(date, points, color)
        }
        CalendarUiState(
            month = base.month,
            selectedDate = base.selectedDate,
            days = days,
            completedTaskNames = base.completedByDate[base.selectedDate].orEmpty(),
            plannedTasks = plans,
            activeTasks = activeTasks,
            reminderEnabled = reminder.enabled,
            reminderTime = reminder.time,
            reminderError = reminder.error,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CalendarUiState())

    init {
        refreshMonth()
    }

    fun previousMonth() {
        month.value = month.value.minusMonths(1)
        refreshMonth()
    }

    fun nextMonth() {
        month.value = month.value.plusMonths(1)
        refreshMonth()
    }

    fun selectDate(date: LocalDate) {
        selectedDate.value = date
    }

    fun addPlan(taskId: Long, taskName: String) {
        viewModelScope.launch {
            try {
                val planId = addPlannedTaskUseCase(taskId, selectedDate.value)
                if (reminderEnabled.value) {
                    val time = LocalTime.parse(reminderTime.value.trim())
                    val triggerDateTime = selectedDate.value.atTime(time)
                    require(triggerDateTime.isAfter(LocalDateTime.now())) {
                        "Reminder time must be in the future."
                    }
                    val triggerAtMillis = triggerDateTime
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                    reminderScheduler.schedulePlannedTaskReminder(planId, taskName, triggerAtMillis)
                }
                reminderError.value = null
            } catch (throwable: Throwable) {
                reminderError.value = throwable.message ?: "Unable to schedule task reminder."
            }
        }
    }

    fun removePlan(planId: Long) {
        viewModelScope.launch {
            deletePlannedTaskUseCase(planId)
            reminderScheduler.cancelPlannedTaskReminder(planId)
        }
    }

    fun setReminderEnabled(enabled: Boolean) {
        reminderEnabled.value = enabled
        reminderError.value = null
    }

    fun setReminderTime(value: String) {
        reminderTime.value = value
        reminderError.value = null
    }

    private fun refreshMonth() {
        viewModelScope.launch {
            val report = generateMonthlyReportUseCase(month.value)
            val points = report.completions.groupBy { it.date }.mapValues { (_, values) ->
                values.sumOf { if (it.completed) it.points else 0 }
            }
            val completedNames = report.completions
                .filter { it.completed }
                .groupBy { it.date }
                .mapValues { (_, values) -> values.map { it.taskName } }
            dayPoints.value = points
            completedByDate.value = completedNames
        }
    }
}
