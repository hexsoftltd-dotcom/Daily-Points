package com.hexsoft.dailypoints.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "task_completions",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["taskId"]),
        Index(value = ["taskId", "date"], unique = true),
        Index(value = ["date"]),
    ],
)
data class TaskCompletionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val taskId: Long,
    val date: String,
    val completed: Boolean,
    val completedAt: Long,
)
