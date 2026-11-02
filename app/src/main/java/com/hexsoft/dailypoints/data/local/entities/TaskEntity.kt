package com.hexsoft.dailypoints.data.local.entities

import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["profileId"])],
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val profileId: Long,
    val name: String,
    val description: String,
    val category: String,
    val points: Int,
    val active: Boolean,
    val createdAt: Long,
)
