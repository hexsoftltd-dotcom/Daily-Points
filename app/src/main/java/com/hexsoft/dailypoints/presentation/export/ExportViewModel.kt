package com.hexsoft.dailypoints.presentation.export

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexsoft.dailypoints.data.export.ExportManager
import com.hexsoft.dailypoints.domain.usecase.report.GenerateDateRangeReportUseCase
import com.hexsoft.dailypoints.domain.usecase.report.GenerateMonthlyReportUseCase
import com.hexsoft.dailypoints.domain.usecase.report.GenerateWeeklyReportUseCase
import com.hexsoft.dailypoints.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

enum class ExportPeriod {
    MONTHLY,
    WEEKLY,
    CUSTOM,
}

data class ExportUiState(
    val period: ExportPeriod = ExportPeriod.MONTHLY,
    val selectedDate: String = DateUtils.today().toString(),
    val customStartDate: String = DateUtils.today().toString(),
    val customEndDate: String = DateUtils.today().toString(),
    val generatedFile: File? = null,
    val error: String? = null,
)

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val generateWeeklyReportUseCase: GenerateWeeklyReportUseCase,
    private val generateMonthlyReportUseCase: GenerateMonthlyReportUseCase,
    private val generateDateRangeReportUseCase: GenerateDateRangeReportUseCase,
    private val exportManager: ExportManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ExportUiState())
    val uiState: StateFlow<ExportUiState> = _uiState.asStateFlow()

    fun setPeriod(period: ExportPeriod) {
        _uiState.value = _uiState.value.copy(period = period, generatedFile = null, error = null)
    }

    fun setSelectedDate(value: String) {
        _uiState.value = _uiState.value.copy(selectedDate = value, generatedFile = null, error = null)
    }

    fun setCustomStartDate(value: String) {
        _uiState.value = _uiState.value.copy(customStartDate = value, generatedFile = null, error = null)
    }

    fun setCustomEndDate(value: String) {
        _uiState.value = _uiState.value.copy(customEndDate = value, generatedFile = null, error = null)
    }

    fun export() {
        viewModelScope.launch {
            try {
                val state = _uiState.value
                val file = when (state.period) {
                    ExportPeriod.MONTHLY -> {
                        val selectedDate = parseDate(state.selectedDate, "Enter a valid reference date.")
                        val month = YearMonth.from(selectedDate)
                        val report = generateMonthlyReportUseCase(month)
                        exportManager.exportCsv(
                            baseName = "monthly_report_$month",
                            completions = report.completions,
                        )
                    }

                    ExportPeriod.WEEKLY -> {
                        val selectedDate = parseDate(state.selectedDate, "Enter a valid reference date.")
                        val report = generateWeeklyReportUseCase(selectedDate)
                        exportManager.exportCsv(
                            baseName = "weekly_report_${report.startDate}_${report.endDate}",
                            completions = report.completions,
                        )
                    }

                    ExportPeriod.CUSTOM -> {
                        val startDate = parseDate(state.customStartDate, "Enter a valid custom start date.")
                        val endDate = parseDate(state.customEndDate, "Enter a valid custom end date.")
                        val report = generateDateRangeReportUseCase(startDate, endDate)
                        exportManager.exportCsv(
                            baseName = "custom_report_${report.startDate}_${report.endDate}",
                            completions = report.completions,
                        )
                    }
                }
                _uiState.value = state.copy(generatedFile = file, error = null)
            } catch (throwable: Throwable) {
                _uiState.value = _uiState.value.copy(error = throwable.message ?: "Export failed.")
            }
        }
    }

    private fun parseDate(value: String, errorMessage: String): LocalDate {
        return runCatching { LocalDate.parse(value) }.getOrElse {
            throw IllegalArgumentException(errorMessage)
        }
    }
}
