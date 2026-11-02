package com.hexsoft.dailypoints.domain.model

import java.time.LocalDate

data class TaskCompletion(
    val id: Long = 0L,
    val taskId: Long,
    val date: LocalDate,
    val completed: Boolean,
    val completedAt: Long,
)

data class TaskCompletionDetail(
    val completionId: Long,
    val taskId: Long,
    val taskName: String,
    val category: String,
    val points: Int,
    val date: LocalDate,
    val completed: Boolean,
    val completedAt: Long,
)
