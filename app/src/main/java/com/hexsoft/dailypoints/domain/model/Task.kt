package com.hexsoft.dailypoints.domain.model

data class Task(
    val id: Long = 0L,
    val profileId: Long,
    val name: String,
    val description: String,
    val category: String,
    val points: Int,
    val active: Boolean,
    val createdAt: Long,
)
