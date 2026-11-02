package com.hexsoft.dailypoints.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexsoft.dailypoints.domain.repository.AppSettings
import com.hexsoft.dailypoints.domain.repository.ThemePreference
import com.hexsoft.dailypoints.domain.usecase.settings.ObserveSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalTime
import javax.inject.Inject

data class MainUiState(
    val settings: AppSettings = AppSettings(
        theme = ThemePreference.SYSTEM,
        notificationEnabled = true,
        reminderTime = LocalTime.of(20, 0),
        exportFormatPreference = "CSV",
        lastBackupDate = 0L,
        activeProfileId = null,
    ),
)

@HiltViewModel
class MainViewModel @Inject constructor(
    observeSettingsUseCase: ObserveSettingsUseCase,
) : ViewModel() {
    val uiState: StateFlow<MainUiState> = observeSettingsUseCase()
        .map { settings -> MainUiState(settings = settings) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MainUiState())
}
