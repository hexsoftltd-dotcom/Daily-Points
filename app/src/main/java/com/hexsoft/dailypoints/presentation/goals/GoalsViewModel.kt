package com.hexsoft.dailypoints.presentation.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexsoft.dailypoints.domain.usecase.goals.GetGoalsUseCase
import com.hexsoft.dailypoints.domain.usecase.goals.SaveGoalsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GoalsUiState(
    val dailyGoal: String = "",
    val monthlyGoal: String = "",
    val error: String? = null,
    val saved: Boolean = false,
)

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val getGoalsUseCase: GetGoalsUseCase,
    private val saveGoalsUseCase: SaveGoalsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalsUiState())
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getGoalsUseCase().collect { goal ->
                _uiState.value = _uiState.value.copy(
                    dailyGoal = goal.dailyGoal.toString(),
                    monthlyGoal = goal.monthlyGoal.toString(),
                )
            }
        }
    }

    fun updateDailyGoal(value: String) {
        _uiState.value = _uiState.value.copy(dailyGoal = value, error = null, saved = false)
    }

    fun updateMonthlyGoal(value: String) {
        _uiState.value = _uiState.value.copy(monthlyGoal = value, error = null, saved = false)
    }

    fun save() {
        viewModelScope.launch {
            val daily = _uiState.value.dailyGoal.toIntOrNull()
            val monthly = _uiState.value.monthlyGoal.toIntOrNull()
            if (daily == null || monthly == null || daily <= 0 || monthly <= 0) {
                _uiState.value = _uiState.value.copy(error = "Both goals must be greater than zero.")
                return@launch
            }
            try {
                saveGoalsUseCase(daily, monthly)
                _uiState.value = _uiState.value.copy(saved = true)
            } catch (throwable: Throwable) {
                _uiState.value = _uiState.value.copy(error = throwable.message ?: "Unable to save goals.")
            }
        }
    }
}
