package com.hexsoft.dailypoints.presentation.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexsoft.dailypoints.domain.model.AnalyticsSummary
import com.hexsoft.dailypoints.domain.usecase.analytics.GetAnalyticsSummaryUseCase
import com.hexsoft.dailypoints.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class AnalyticsUiState(
    val from: LocalDate = DateUtils.today().minusDays(29),
    val to: LocalDate = DateUtils.today(),
    val summary: AnalyticsSummary? = null,
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val getAnalyticsSummaryUseCase: GetAnalyticsSummaryUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun setFrom(date: LocalDate) {
        _uiState.value = _uiState.value.copy(from = date)
        refresh()
    }

    fun setTo(date: LocalDate) {
        _uiState.value = _uiState.value.copy(to = date)
        refresh()
    }

    private fun refresh() {
        viewModelScope.launch {
            val current = _uiState.value
            val summary = getAnalyticsSummaryUseCase(current.from, current.to)
            _uiState.value = current.copy(summary = summary)
        }
    }
}
