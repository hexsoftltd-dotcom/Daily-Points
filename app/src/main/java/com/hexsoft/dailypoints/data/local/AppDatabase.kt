package com.hexsoft.dailypoints.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hexsoft.dailypoints.data.local.dao.GoalDao
import com.hexsoft.dailypoints.data.local.dao.PlannedTaskDao
import com.hexsoft.dailypoints.data.local.dao.ProfileDao
import com.hexsoft.dailypoints.data.local.dao.TaskCompletionDao
import com.hexsoft.dailypoints.data.local.dao.TaskDao
import com.hexsoft.dailypoints.data.local.entities.GoalEntity
import com.hexsoft.dailypoints.data.local.entities.PlannedTaskEntity
import com.hexsoft.dailypoints.data.local.entities.ProfileEntity
import com.hexsoft.dailypoints.data.local.entities.TaskCompletionEntity
import com.hexsoft.dailypoints.data.local.entities.TaskEntity

@Database(
    entities = [
        TaskEntity::class,
        TaskCompletionEntity::class,
        PlannedTaskEntity::class,
        GoalEntity::class,
        ProfileEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun taskCompletionDao(): TaskCompletionDao
    abstract fun plannedTaskDao(): PlannedTaskDao
    abstract fun goalDao(): GoalDao
    abstract fun profileDao(): ProfileDao
}
