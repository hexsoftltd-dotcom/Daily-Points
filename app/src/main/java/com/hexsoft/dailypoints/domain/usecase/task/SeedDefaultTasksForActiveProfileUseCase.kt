package com.hexsoft.dailypoints.domain.usecase.task

import com.hexsoft.dailypoints.domain.model.Task
import com.hexsoft.dailypoints.domain.repository.TaskRepository
import javax.inject.Inject

class SeedDefaultTasksForActiveProfileUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
) {
    suspend operator fun invoke() {
        if (taskRepository.getTaskCount() > 0) return

        val now = System.currentTimeMillis()
        val seedTasks = listOf(
            Task(profileId = 0L, name = "9 Navkar Sleeping / Waking up", description = "Spiritual practice", category = "Jain", points = 5, active = true, createdAt = now),
            Task(profileId = 0L, name = "Touching Parents Feet Every Day", description = "Respect practice", category = "Jain", points = 5, active = true, createdAt = now - 1),
            Task(profileId = 0L, name = "5 * No of Gatha", description = "Recitation practice", category = "Jain", points = 5, active = true, createdAt = now - 2),
            Task(profileId = 0L, name = "Samaik / Pratikraman", description = "Spiritual discipline", category = "Jain", points = 50, active = true, createdAt = now - 3),
            Task(profileId = 0L, name = "Skipping TV / Phone Watching Full Day", description = "Digital fasting", category = "Jain", points = 50, active = true, createdAt = now - 4),
            Task(profileId = 0L, name = "Less than 2 Hour Screen Time", description = "Screen-time discipline", category = "Jain", points = 20, active = true, createdAt = now - 5),
            Task(profileId = 0L, name = "Reading Jain Story / Books", description = "Religious reading", category = "Jain", points = 10, active = true, createdAt = now - 6),
            Task(profileId = 0L, name = "Stavan", description = "Prayer practice", category = "Jain", points = 10, active = true, createdAt = now - 7),
            Task(profileId = 0L, name = "Choviyaro Upvas", description = "Fasting practice", category = "Jain", points = 75, active = true, createdAt = now - 8),
            Task(profileId = 0L, name = "Upvas / Ayambil", description = "Fasting practice", category = "Jain", points = 50, active = true, createdAt = now - 9),
            Task(profileId = 0L, name = "Eksana / Biyasana", description = "Meal-discipline practice", category = "Jain", points = 30, active = true, createdAt = now - 10),
            Task(profileId = 0L, name = "No Ratri Bhojan (No Food Post Sunset) / Choviyar", description = "Food-timing discipline", category = "Jain", points = 10, active = true, createdAt = now - 11),
            Task(profileId = 0L, name = "12 Tikhuto Vandan", description = "Devotional practice", category = "Jain", points = 10, active = true, createdAt = now - 12),
            Task(profileId = 0L, name = "Thali Dhokar Pina / Pooja", description = "Ritual practice", category = "Jain", points = 25, active = true, createdAt = now - 13),
            Task(profileId = 0L, name = "Saying Story / Stavan in Jaap", description = "Recitation practice", category = "Jain", points = 15, active = true, createdAt = now - 14),
            Task(profileId = 0L, name = "Drink boiled water / dhon pani", description = "Health discipline", category = "Jain", points = 25, active = true, createdAt = now - 15),
        )

        seedTasks.forEach { task ->
            taskRepository.addTask(task)
        }
    }
}
