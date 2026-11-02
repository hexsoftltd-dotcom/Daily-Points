package com.hexsoft.dailypoints.domain.model

data class Profile(
    val id: Long = 0L,
    val name: String,
    val extraPoints: Int,
    val createdAt: Long,
)
