package com.hexsoft.dailypoints.domain.usecase.task

import com.hexsoft.dailypoints.testdoubles.FakeTaskRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class AddTaskUseCaseTest {
    @Test
    fun `adds task when input is valid`() = runTest {
        val repository = FakeTaskRepository()
        val useCase = AddTaskUseCase(repository)

        val id = useCase(
            name = "Exercise",
            description = "Workout",
            category = "Health",
            points = 20,
        )

        assertEquals(1L, id)
        assertEquals(1, repository.getTaskCount())
    }

    @Test
    fun `throws when points are invalid`() {
        val repository = FakeTaskRepository()
        val useCase = AddTaskUseCase(repository)

        assertThrows(IllegalArgumentException::class.java) {
            kotlinx.coroutines.runBlocking {
                useCase("Exercise", "Workout", "Health", 0)
            }
        }
    }
}
