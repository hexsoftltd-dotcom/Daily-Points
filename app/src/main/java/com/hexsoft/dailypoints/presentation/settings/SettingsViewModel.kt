package com.hexsoft.dailypoints.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexsoft.dailypoints.data.export.ExportManager
import com.hexsoft.dailypoints.domain.model.Profile
import com.hexsoft.dailypoints.domain.repository.AppSettings
import com.hexsoft.dailypoints.domain.repository.ProfileRepository
import com.hexsoft.dailypoints.domain.repository.SettingsRepository
import com.hexsoft.dailypoints.domain.repository.ThemePreference
import com.hexsoft.dailypoints.domain.usecase.report.GenerateDateRangeReportUseCase
import com.hexsoft.dailypoints.domain.usecase.settings.ObserveSettingsUseCase
import com.hexsoft.dailypoints.domain.usecase.settings.UpdateExportPreferenceUseCase
import com.hexsoft.dailypoints.domain.usecase.settings.UpdateNotificationEnabledUseCase
import com.hexsoft.dailypoints.domain.usecase.settings.UpdateReminderTimeUseCase
import com.hexsoft.dailypoints.domain.usecase.settings.UpdateThemeUseCase
import com.hexsoft.dailypoints.domain.usecase.task.SeedDefaultTasksForActiveProfileUseCase
import com.hexsoft.dailypoints.notifications.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import javax.inject.Inject

data class SettingsUiState(
    val settings: AppSettings = AppSettings(
        theme = ThemePreference.SYSTEM,
        notificationEnabled = true,
        reminderTime = LocalTime.of(20, 0),
        exportFormatPreference = "CSV",
        lastBackupDate = 0L,
        activeProfileId = null,
    ),
    val profiles: List<Profile> = emptyList(),
    val newProfileName: String = "",
    val profileErrorMessage: String? = null,
    val exportStartDate: String = LocalDate.now().toString(),
    val exportEndDate: String = LocalDate.now().toString(),
    val shareFile: File? = null,
    val exportError: String? = null,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeSettingsUseCase: ObserveSettingsUseCase,
    private val profileRepository: ProfileRepository,
    private val settingsRepository: SettingsRepository,
    private val updateThemeUseCase: UpdateThemeUseCase,
    private val updateNotificationEnabledUseCase: UpdateNotificationEnabledUseCase,
    private val updateReminderTimeUseCase: UpdateReminderTimeUseCase,
    private val updateExportPreferenceUseCase: UpdateExportPreferenceUseCase,
    private val seedDefaultTasksForActiveProfileUseCase: SeedDefaultTasksForActiveProfileUseCase,
    private val generateDateRangeReportUseCase: GenerateDateRangeReportUseCase,
    private val exportManager: ExportManager,
    private val reminderScheduler: ReminderScheduler,
) : ViewModel() {
    private val formState = MutableStateFlow(SettingsUiState())

    val uiState: StateFlow<SettingsUiState> = combine(
        observeSettingsUseCase(),
        profileRepository.observeProfiles(),
        formState,
    ) { settings, profiles, form ->
        form.copy(
            settings = settings,
            profiles = profiles,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    fun setTheme(themePreference: ThemePreference) {
        viewModelScope.launch {
            updateThemeUseCase(themePreference)
        }
    }

    fun setNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            updateNotificationEnabledUseCase(enabled)
            if (enabled) {
                reminderScheduler.scheduleDailyReminders(uiState.value.settings.reminderTime)
            } else {
                reminderScheduler.cancelDailyReminders()
            }
        }
    }

    fun setReminderTime(localTime: LocalTime) {
        viewModelScope.launch {
            updateReminderTimeUseCase(localTime)
            if (uiState.value.settings.notificationEnabled) {
                reminderScheduler.scheduleDailyReminders(localTime)
            }
        }
    }

    fun setExportPreference(format: String) {
        viewModelScope.launch {
            updateExportPreferenceUseCase(format)
        }
    }

    fun setActiveProfile(profileId: Long) {
        viewModelScope.launch {
            settingsRepository.setActiveProfileId(profileId)
            formState.value = formState.value.copy(profileErrorMessage = null)
        }
    }

    fun updateNewProfileName(name: String) {
        formState.value = formState.value.copy(newProfileName = name, profileErrorMessage = null)
    }

    fun createProfile() {
        viewModelScope.launch {
            val profileName = formState.value.newProfileName.trim()
            if (profileName.isEmpty()) {
                formState.value = formState.value.copy(profileErrorMessage = "Profile name is required.")
                return@launch
            }
            runCatching {
                val profileId = profileRepository.createProfile(profileName)
                settingsRepository.setActiveProfileId(profileId)
                seedDefaultTasksForActiveProfileUseCase()
                formState.value = formState.value.copy(newProfileName = "", profileErrorMessage = null)
            }.onFailure { throwable ->
                formState.value = formState.value.copy(
                    profileErrorMessage = throwable.message ?: "Unable to create profile.",
                )
            }
        }
    }

    fun setExportStartDate(value: String) {
        formState.value = formState.value.copy(
            exportStartDate = value,
            shareFile = null,
            exportError = null,
        )
    }

    fun setExportEndDate(value: String) {
        formState.value = formState.value.copy(
            exportEndDate = value,
            shareFile = null,
            exportError = null,
        )
    }

    fun shareReport() {
        viewModelScope.launch {
            try {
                val state = uiState.value
                val startInput = state.exportStartDate.trim()
                val endInput = state.exportEndDate.trim()
                val (startDate, endDate) = when {
                    startInput.isEmpty() && endInput.isEmpty() -> {
                        val lastMonth = YearMonth.now().minusMonths(1)
                        lastMonth.atDay(1) to lastMonth.atEndOfMonth()
                    }

                    startInput.isNotEmpty() && endInput.isNotEmpty() -> {
                        LocalDate.parse(startInput) to LocalDate.parse(endInput)
                    }

                    else -> {
                        throw IllegalArgumentException("Enter both start and end dates, or leave both empty.")
                    }
                }
                val report = generateDateRangeReportUseCase(startDate, endDate)
                val filteredCompletions = report.completions.filter { completion ->
                    !completion.date.isBefore(startDate) && !completion.date.isAfter(endDate)
                }
                val profileName = state.profiles
                    .firstOrNull { it.id == state.settings.activeProfileId }
                    ?.name
                    ?.sanitizeForFileName()
                    ?: "profile"
                val file = exportManager.exportCsv(
                    baseName = "${profileName}_${report.startDate}_${report.endDate}",
                    completions = filteredCompletions,
                )
                formState.value = state.copy(shareFile = file, exportError = null)
            } catch (throwable: Throwable) {
                formState.value = formState.value.copy(
                    shareFile = null,
                    exportError = throwable.message ?: "Export failed.",
                )
            }
        }
    }

    fun onShareHandled() {
        formState.value = formState.value.copy(shareFile = null)
    }

    private fun String.sanitizeForFileName(): String {
        return trim()
            .replace(Regex("[^a-zA-Z0-9._-]"), "_")
            .replace(Regex("_+"), "_")
            .ifBlank { "profile" }
    }
}
