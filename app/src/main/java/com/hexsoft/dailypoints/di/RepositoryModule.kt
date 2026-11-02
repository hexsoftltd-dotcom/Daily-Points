package com.hexsoft.dailypoints.di

import com.hexsoft.dailypoints.data.repository.CompletionRepositoryImpl
import com.hexsoft.dailypoints.data.repository.GoalRepositoryImpl
import com.hexsoft.dailypoints.data.repository.PlannedTaskRepositoryImpl
import com.hexsoft.dailypoints.data.repository.ProfileRepositoryImpl
import com.hexsoft.dailypoints.data.repository.SettingsRepositoryImpl
import com.hexsoft.dailypoints.data.repository.TaskRepositoryImpl
import com.hexsoft.dailypoints.domain.repository.CompletionRepository
import com.hexsoft.dailypoints.domain.repository.GoalRepository
import com.hexsoft.dailypoints.domain.repository.PlannedTaskRepository
import com.hexsoft.dailypoints.domain.repository.ProfileRepository
import com.hexsoft.dailypoints.domain.repository.SettingsRepository
import com.hexsoft.dailypoints.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository

    @Binds
    @Singleton
    abstract fun bindCompletionRepository(impl: CompletionRepositoryImpl): CompletionRepository

    @Binds
    @Singleton
    abstract fun bindPlannedTaskRepository(impl: PlannedTaskRepositoryImpl): PlannedTaskRepository

    @Binds
    @Singleton
    abstract fun bindGoalRepository(impl: GoalRepositoryImpl): GoalRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository
}
