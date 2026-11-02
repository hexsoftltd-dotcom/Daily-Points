package com.hexsoft.dailypoints.di

import android.content.Context
import androidx.room.Room
import com.hexsoft.dailypoints.data.local.AppDatabase
import com.hexsoft.dailypoints.data.local.dao.GoalDao
import com.hexsoft.dailypoints.data.local.dao.PlannedTaskDao
import com.hexsoft.dailypoints.data.local.dao.ProfileDao
import com.hexsoft.dailypoints.data.local.dao.TaskCompletionDao
import com.hexsoft.dailypoints.data.local.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "daily_points_tracker.db",
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideTaskDao(database: AppDatabase): TaskDao = database.taskDao()

    @Provides
    fun provideTaskCompletionDao(database: AppDatabase): TaskCompletionDao = database.taskCompletionDao()

    @Provides
    fun providePlannedTaskDao(database: AppDatabase): PlannedTaskDao = database.plannedTaskDao()

    @Provides
    fun provideGoalDao(database: AppDatabase): GoalDao = database.goalDao()

    @Provides
    fun provideProfileDao(database: AppDatabase): ProfileDao = database.profileDao()
}
