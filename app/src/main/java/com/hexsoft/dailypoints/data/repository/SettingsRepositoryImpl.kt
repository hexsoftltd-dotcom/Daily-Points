package com.hexsoft.dailypoints.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.hexsoft.dailypoints.domain.repository.AppSettings
import com.hexsoft.dailypoints.domain.repository.SettingsRepository
import com.hexsoft.dailypoints.domain.repository.ThemePreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalTime
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {

    override val settings: Flow<AppSettings> = dataStore.data.map { pref ->
        val storedTheme = pref[THEME_KEY] ?: ThemePreference.SYSTEM.name
        val theme = ThemePreference.entries.firstOrNull { it.name == storedTheme } ?: ThemePreference.SYSTEM
        val reminderTime = runCatching { LocalTime.parse(pref[REMINDER_TIME_KEY] ?: "20:00") }
            .getOrElse { LocalTime.of(20, 0) }
        AppSettings(
            theme = theme,
            notificationEnabled = pref[NOTIFICATION_ENABLED_KEY] ?: true,
            reminderTime = reminderTime,
            exportFormatPreference = pref[EXPORT_FORMAT_KEY] ?: "CSV",
            lastBackupDate = pref[LAST_BACKUP_DATE_KEY] ?: 0L,
            activeProfileId = pref[ACTIVE_PROFILE_ID_KEY],
        )
    }

    override suspend fun setThemePreference(themePreference: ThemePreference) {
        dataStore.edit { it[THEME_KEY] = themePreference.name }
    }

    override suspend fun setNotificationEnabled(enabled: Boolean) {
        dataStore.edit { it[NOTIFICATION_ENABLED_KEY] = enabled }
    }

    override suspend fun setReminderTime(reminderTime: LocalTime) {
        dataStore.edit { it[REMINDER_TIME_KEY] = reminderTime.toString() }
    }

    override suspend fun setExportFormatPreference(format: String) {
        dataStore.edit { it[EXPORT_FORMAT_KEY] = format }
    }

    override suspend fun setLastBackupDate(millis: Long) {
        dataStore.edit { it[LAST_BACKUP_DATE_KEY] = millis }
    }

    override suspend fun setActiveProfileId(profileId: Long) {
        dataStore.edit { it[ACTIVE_PROFILE_ID_KEY] = profileId }
    }

    private companion object {
        val THEME_KEY = stringPreferencesKey("theme_preference")
        val NOTIFICATION_ENABLED_KEY = booleanPreferencesKey("notification_enabled")
        val REMINDER_TIME_KEY = stringPreferencesKey("reminder_time")
        val EXPORT_FORMAT_KEY = stringPreferencesKey("export_format_preference")
        val LAST_BACKUP_DATE_KEY = longPreferencesKey("last_backup_date")
        val ACTIVE_PROFILE_ID_KEY = longPreferencesKey("active_profile_id")
    }
}
