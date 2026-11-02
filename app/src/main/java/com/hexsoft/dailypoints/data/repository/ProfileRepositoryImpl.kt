package com.hexsoft.dailypoints.data.repository

import com.hexsoft.dailypoints.data.local.dao.ProfileDao
import com.hexsoft.dailypoints.data.local.entities.ProfileEntity
import com.hexsoft.dailypoints.domain.model.Profile
import com.hexsoft.dailypoints.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileDao: ProfileDao,
) : ProfileRepository {
    override fun observeProfiles(): Flow<List<Profile>> {
        return profileDao.observeProfiles().map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getById(id: Long): Profile? = profileDao.getById(id)?.toDomain()

    override suspend fun getFirstProfile(): Profile? = profileDao.getFirstProfile()?.toDomain()

    override suspend fun createProfile(name: String): Long {
        val normalizedName = name.trim()
        require(normalizedName.isNotEmpty()) { "Profile name is required." }
        return profileDao.insert(
            ProfileEntity(
                name = normalizedName,
                extraPoints = 0,
                createdAt = System.currentTimeMillis(),
            ),
        )
    }

    override suspend fun addPoints(profileId: Long, pointsToAdd: Int) {
        require(pointsToAdd > 0) { "Points to add must be greater than zero." }
        profileDao.addPoints(profileId, pointsToAdd)
    }
}
