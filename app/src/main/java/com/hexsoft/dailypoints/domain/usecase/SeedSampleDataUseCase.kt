package com.hexsoft.dailypoints.domain.usecase

import com.hexsoft.dailypoints.domain.model.Goal
import com.hexsoft.dailypoints.domain.repository.GoalRepository
import com.hexsoft.dailypoints.domain.repository.ProfileRepository
import com.hexsoft.dailypoints.domain.repository.SettingsRepository
import com.hexsoft.dailypoints.domain.usecase.task.SeedDefaultTasksForActiveProfileUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SeedSampleDataUseCase @Inject constructor(
    private val goalRepository: GoalRepository,
    private val profileRepository: ProfileRepository,
    private val settingsRepository: SettingsRepository,
    private val seedDefaultTasksForActiveProfileUseCase: SeedDefaultTasksForActiveProfileUseCase,
) {
    suspend operator fun invoke() {
        ensureDefaultProfile()
        seedDefaultTasksForActiveProfileUseCase()

        goalRepository.saveGoal(Goal(dailyGoal = 50, monthlyGoal = 1200))
    }

    private suspend fun ensureDefaultProfile() {
        val activeProfileId = settingsRepository.settings.first().activeProfileId
        if (activeProfileId != null) return
        val firstProfile = profileRepository.getFirstProfile()
        val profileId = firstProfile?.id ?: profileRepository.createProfile("Default Profile")
        settingsRepository.setActiveProfileId(profileId)
    }
}
