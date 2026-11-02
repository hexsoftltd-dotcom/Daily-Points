package com.hexsoft.dailypoints.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexsoft.dailypoints.domain.model.DashboardSummary
import com.hexsoft.dailypoints.domain.model.Goal
import com.hexsoft.dailypoints.domain.repository.SettingsRepository
import com.hexsoft.dailypoints.domain.usecase.dashboard.GetDashboardSummaryUseCase
import com.hexsoft.dailypoints.domain.usecase.goals.GetGoalsUseCase
import com.hexsoft.dailypoints.domain.usecase.goals.SaveGoalsUseCase
import com.hexsoft.dailypoints.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class DashboardUiState(
    val summary: DashboardSummary? = null,
    val activeProfileId: Long? = null,
    val dailyGoalDraft: Float = 50f,
    val dailyGoalDirty: Boolean = false,
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardSummaryUseCase: GetDashboardSummaryUseCase,
    private val getGoalsUseCase: GetGoalsUseCase,
    private val saveGoalsUseCase: SaveGoalsUseCase,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val summary = MutableStateFlow<DashboardSummary?>(null)
    private val goal = MutableStateFlow(Goal(dailyGoal = 50, monthlyGoal = 1200))
    private val formState = MutableStateFlow(DashboardUiState())

    val uiState: StateFlow<DashboardUiState> = combine(
        summary,
        goal,
        settingsRepository.settings,
        formState,
    ) { summaryData, goalData, settings, form ->
        form.copy(
            summary = summaryData,
            activeProfileId = settings.activeProfileId,
            dailyGoalDraft = if (form.dailyGoalDirty) {
                form.dailyGoalDraft
            } else {
                summaryData?.todayGoal?.toFloat() ?: goalData.dailyGoal.toFloat()
            },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState())

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect {
                refresh(DateUtils.today())
            }
        }
        viewModelScope.launch {
            getGoalsUseCase().collect {
                goal.value = it
            }
        }
    }

    fun refresh(date: LocalDate = DateUtils.today()) {
        viewModelScope.launch {
            summary.value = runCatching { getDashboardSummaryUseCase(date) }.getOrNull()
        }
    }

    fun setDailyGoalDraft(value: Float) {
        val rounded = value.toInt().coerceAtLeast(1).toFloat()
        formState.value = formState.value.copy(
            dailyGoalDraft = rounded,
            dailyGoalDirty = true,
        )
    }

    fun saveDailyGoalFromDraft() {
        viewModelScope.launch {
            val updatedDailyGoal = formState.value.dailyGoalDraft.toInt().coerceAtLeast(1)
            val existingGoal = goal.value
            runCatching {
                saveGoalsUseCase(updatedDailyGoal, existingGoal.monthlyGoal)
                formState.value = formState.value.copy(dailyGoalDirty = false)
                refresh(DateUtils.today())
            }
        }
    }
}
