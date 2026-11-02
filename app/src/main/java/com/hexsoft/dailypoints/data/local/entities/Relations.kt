package com.hexsoft.dailypoints.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

data class CompletionWithTaskEntity(
    @Embedded val completion: TaskCompletionEntity,
    @Relation(
        parentColumn = "taskId",
        entityColumn = "id",
    )
    val task: TaskEntity,
)
