package com.hexsoft.dailypoints.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hexsoft.dailypoints.data.local.AppDatabase
import com.hexsoft.dailypoints.domain.model.Task
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TaskRepositoryImplTest {
    private lateinit var db: AppDatabase
    private lateinit var repository: TaskRepositoryImpl

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
        repository = TaskRepositoryImpl(db.taskDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun addTaskAndReadBack() = runBlocking {
        repository.addTask(
            Task(
                name = "Reading",
                description = "Read 20 pages",
                category = "Learning",
                points = 10,
                active = true,
                createdAt = 1L,
            ),
        )

        val tasks = repository.observeTasks().first()
        assertEquals(1, tasks.size)
        assertEquals("Reading", tasks.first().name)
    }
}
