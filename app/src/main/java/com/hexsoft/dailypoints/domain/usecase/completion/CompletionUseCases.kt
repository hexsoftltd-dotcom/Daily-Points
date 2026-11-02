package com.hexsoft.dailypoints.domain.usecase.completion

import com.hexsoft.dailypoints.domain.model.TaskCompletion
import com.hexsoft.dailypoints.domain.repository.CompletionRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class CompleteTaskUseCase @Inject constructor(
    private val completionRepository: CompletionRepository,
) {
    suspend operator fun invoke(taskId: Long, date: LocalDate) {
        completionRepository.completeTask(taskId, date)
    }
}

class UncompleteTaskUseCase @Inject constructor(
    private val completionRepository: CompletionRepository,
) {
    suspend operator fun invoke(taskId: Long, date: LocalDate) {
        completionRepository.uncompleteTask(taskId, date)
    }
}

class GetDailyCompletionUseCase @Inject constructor(
    private val completionRepository: CompletionRepository,
) {
    operator fun invoke(date: LocalDate): Flow<List<TaskCompletion>> {
        return completionRepository.observeDailyCompletions(date)
    }
}
