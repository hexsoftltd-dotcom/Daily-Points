package com.hexsoft.dailypoints.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "profiles",
    indices = [Index(value = ["name"], unique = true)],
)
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val extraPoints: Int,
    val createdAt: Long,
)
