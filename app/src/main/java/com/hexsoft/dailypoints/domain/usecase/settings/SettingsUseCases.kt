package com.hexsoft.dailypoints.domain.usecase.settings

import com.hexsoft.dailypoints.domain.repository.AppSettings
import com.hexsoft.dailypoints.domain.repository.SettingsRepository
import com.hexsoft.dailypoints.domain.repository.ThemePreference
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime
import javax.inject.Inject

class ObserveSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(): Flow<AppSettings> = settingsRepository.settings
}

class UpdateThemeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(themePreference: ThemePreference) {
        settingsRepository.setThemePreference(themePreference)
    }
}

class UpdateNotificationEnabledUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(enabled: Boolean) {
        settingsRepository.setNotificationEnabled(enabled)
    }
}

class UpdateReminderTimeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(localTime: LocalTime) {
        settingsRepository.setReminderTime(localTime)
    }
}

class UpdateExportPreferenceUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(format: String) {
        settingsRepository.setExportFormatPreference(format)
    }
}
