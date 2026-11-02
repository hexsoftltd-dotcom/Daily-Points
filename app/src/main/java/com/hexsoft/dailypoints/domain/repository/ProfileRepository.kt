package com.hexsoft.dailypoints.domain.repository

import com.hexsoft.dailypoints.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun observeProfiles(): Flow<List<Profile>>
    suspend fun getById(id: Long): Profile?
    suspend fun getFirstProfile(): Profile?
    suspend fun createProfile(name: String): Long
    suspend fun addPoints(profileId: Long, pointsToAdd: Int)
}
