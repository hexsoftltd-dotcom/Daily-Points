package com.hexsoft.dailypoints.domain.repository

import kotlinx.coroutines.flow.Flow
import java.time.LocalTime

enum class ThemePreference {
    SYSTEM,
    LIGHT,
    DARK,
}

data class AppSettings(
    val theme: ThemePreference,
    val notificationEnabled: Boolean,
    val reminderTime: LocalTime,
    val exportFormatPreference: String,
    val lastBackupDate: Long,
    val activeProfileId: Long?,
)

interface SettingsRepository {
    val settings: Flow<AppSettings>
    suspend fun setThemePreference(themePreference: ThemePreference)
    suspend fun setNotificationEnabled(enabled: Boolean)
    suspend fun setReminderTime(reminderTime: LocalTime)
    suspend fun setExportFormatPreference(format: String)
    suspend fun setLastBackupDate(millis: Long)
    suspend fun setActiveProfileId(profileId: Long)
}
