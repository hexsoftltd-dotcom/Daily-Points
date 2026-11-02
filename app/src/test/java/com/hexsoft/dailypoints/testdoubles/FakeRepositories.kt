package com.hexsoft.dailypoints.testdoubles

import com.hexsoft.dailypoints.domain.model.Goal
import com.hexsoft.dailypoints.domain.model.PlannedTask
import com.hexsoft.dailypoints.domain.model.Task
import com.hexsoft.dailypoints.domain.model.TaskCompletion
import com.hexsoft.dailypoints.domain.model.TaskCompletionDetail
import com.hexsoft.dailypoints.domain.repository.CompletionRepository
import com.hexsoft.dailypoints.domain.repository.GoalRepository
import com.hexsoft.dailypoints.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate

class FakeTaskRepository : TaskRepository {
    private val tasks = mutableListOf<Task>()
    private val flow = MutableStateFlow<List<Task>>(emptyList())

    override fun observeTasks(): Flow<List<Task>> = flow

    override fun observeActiveTasks(): Flow<List<Task>> = flowOf(tasks.filter { it.active })

    override suspend fun getTaskById(id: Long): Task? = tasks.firstOrNull { it.id == id }

    override suspend fun addTask(task: Task): Long {
        val newTask = task.copy(id = (tasks.maxOfOrNull { it.id } ?: 0L) + 1)
        tasks.add(newTask)
        flow.value = tasks.toList()
        return newTask.id
    }

    override suspend fun updateTask(task: Task) {
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index >= 0) {
            tasks[index] = task
            flow.value = tasks.toList()
        }
    }

    override suspend fun deleteTask(id: Long) {
        tasks.removeAll { it.id == id }
        flow.value = tasks.toList()
    }

    override suspend fun setTaskActive(id: Long, active: Boolean) {
        val index = tasks.indexOfFirst { it.id == id }
        if (index >= 0) {
            tasks[index] = tasks[index].copy(active = active)
            flow.value = tasks.toList()
        }
    }

    override suspend fun getTaskCount(): Int = tasks.size
}

class FakeCompletionRepository : CompletionRepository {
    val completions = mutableListOf<TaskCompletionDetail>()

    override fun observeDailyCompletions(date: LocalDate): Flow<List<TaskCompletion>> {
        return flowOf(emptyList())
    }

    override suspend fun completeTask(taskId: Long, date: LocalDate) = Unit

    override suspend fun uncompleteTask(taskId: Long, date: LocalDate) = Unit

    override suspend fun isTaskCompleted(taskId: Long, date: LocalDate): Boolean = false

    override suspend fun getDailyPoints(date: LocalDate): Int {
        return completions.filter { it.date == date && it.completed }.sumOf { it.points }
    }

    override fun observeDailyPoints(date: LocalDate): Flow<Int> = flowOf(0)

    override suspend fun getPointsInRange(startDate: LocalDate, endDate: LocalDate): Int {
        return completions.filter { it.date >= startDate && it.date <= endDate && it.completed }
            .sumOf { it.points }
    }

    override suspend fun getCompletionsInRange(startDate: LocalDate, endDate: LocalDate): List<TaskCompletionDetail> {
        return completions.filter { it.date >= startDate && it.date <= endDate }
    }

    override suspend fun getCompletedDates(): List<LocalDate> {
        return completions.filter { it.completed }.map { it.date }.distinct().sorted()
    }
}

class FakeGoalRepository : GoalRepository {
    private val goal = MutableStateFlow(Goal(dailyGoal = 50, monthlyGoal = 1000))
    override fun observeGoal(): Flow<Goal> = goal

    override suspend fun getGoal(): Goal = goal.value

    override suspend fun saveGoal(goal: Goal) {
        this.goal.value = goal
    }
}
