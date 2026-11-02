package com.hexsoft.dailypoints.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey val id: Int = 1,
    val dailyGoal: Int,
    val monthlyGoal: Int,
)
