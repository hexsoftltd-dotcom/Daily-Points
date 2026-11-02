package com.hexsoft.dailypoints.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexsoft.dailypoints.domain.usecase.settings.ObserveSettingsUseCase
import com.hexsoft.dailypoints.notifications.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val observeSettingsUseCase: ObserveSettingsUseCase,
    private val reminderScheduler: ReminderScheduler,
) : ViewModel() {

    private val _ready = MutableStateFlow(false)
    val ready: StateFlow<Boolean> = _ready.asStateFlow()

    init {
        viewModelScope.launch {
            val settings = observeSettingsUseCase().first()
            if (settings.notificationEnabled) {
                reminderScheduler.scheduleDailyReminders(settings.reminderTime)
            } else {
                reminderScheduler.cancelDailyReminders()
            }
            delay(600)
            _ready.value = true
        }
    }
}
