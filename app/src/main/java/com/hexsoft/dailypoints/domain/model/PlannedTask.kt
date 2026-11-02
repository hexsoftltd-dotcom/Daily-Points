package com.hexsoft.dailypoints.domain.model

import java.time.LocalDate

data class PlannedTask(
    val id: Long = 0L,
    val taskId: Long,
    val plannedDate: LocalDate,
)
