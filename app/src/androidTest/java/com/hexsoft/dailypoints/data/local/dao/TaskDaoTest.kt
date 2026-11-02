package com.hexsoft.dailypoints.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hexsoft.dailypoints.data.local.AppDatabase
import com.hexsoft.dailypoints.data.local.entities.TaskCompletionEntity
import com.hexsoft.dailypoints.data.local.entities.TaskEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TaskDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var taskDao: TaskDao
    private lateinit var completionDao: TaskCompletionDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
        taskDao = database.taskDao()
        completionDao = database.taskCompletionDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertTaskAndSingleCompletionPerDay() = runBlocking {
        val taskId = taskDao.insert(
            TaskEntity(
                name = "Exercise",
                description = "Workout",
                category = "Health",
                points = 20,
                active = true,
                createdAt = System.currentTimeMillis(),
            ),
        )

        completionDao.upsertCompletion(
            TaskCompletionEntity(
                taskId = taskId,
                date = "2026-10-29",
                completed = true,
                completedAt = 1L,
            ),
        )
        completionDao.upsertCompletion(
            TaskCompletionEntity(
                taskId = taskId,
                date = "2026-10-29",
                completed = true,
                completedAt = 2L,
            ),
        )

        val completion = completionDao.getCompletion(taskId, "2026-10-29")
        val points = completionDao.getPointsForDate("2026-10-29")
        assertEquals(true, completion?.completed)
        assertEquals(20, points)
    }
}
