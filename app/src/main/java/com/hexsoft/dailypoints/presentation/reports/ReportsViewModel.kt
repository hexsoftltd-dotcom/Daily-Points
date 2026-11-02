package com.hexsoft.dailypoints.presentation.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexsoft.dailypoints.domain.model.DailyReport
import com.hexsoft.dailypoints.domain.model.MonthlyReport
import com.hexsoft.dailypoints.domain.model.WeeklyReport
import com.hexsoft.dailypoints.domain.model.YearlyReport
import com.hexsoft.dailypoints.domain.usecase.report.GenerateDailyReportUseCase
import com.hexsoft.dailypoints.domain.usecase.report.GenerateMonthlyReportUseCase
import com.hexsoft.dailypoints.domain.usecase.report.GenerateWeeklyReportUseCase
import com.hexsoft.dailypoints.domain.usecase.report.GenerateYearlyReportUseCase
import com.hexsoft.dailypoints.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

data class ReportsUiState(
    val selectedDate: LocalDate = DateUtils.today(),
    val dailyReport: DailyReport? = null,
    val weeklyReport: WeeklyReport? = null,
    val monthlyReport: MonthlyReport? = null,
    val yearlyReport: YearlyReport? = null,
)

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val generateDailyReportUseCase: GenerateDailyReportUseCase,
    private val generateWeeklyReportUseCase: GenerateWeeklyReportUseCase,
    private val generateMonthlyReportUseCase: GenerateMonthlyReportUseCase,
    private val generateYearlyReportUseCase: GenerateYearlyReportUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun setDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
        refresh()
    }

    private fun refresh() {
        viewModelScope.launch {
            val date = _uiState.value.selectedDate
            _uiState.value = _uiState.value.copy(
                dailyReport = generateDailyReportUseCase(date),
                weeklyReport = generateWeeklyReportUseCase(date),
                monthlyReport = generateMonthlyReportUseCase(YearMonth.from(date)),
                yearlyReport = generateYearlyReportUseCase(date.year),
            )
        }
    }
}
