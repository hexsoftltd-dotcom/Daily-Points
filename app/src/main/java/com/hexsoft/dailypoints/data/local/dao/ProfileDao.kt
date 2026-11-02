package com.hexsoft.dailypoints.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hexsoft.dailypoints.data.local.entities.ProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles ORDER BY createdAt ASC")
    fun observeProfiles(): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM profiles WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ProfileEntity?

    @Query("SELECT * FROM profiles ORDER BY createdAt ASC LIMIT 1")
    suspend fun getFirstProfile(): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(profile: ProfileEntity): Long

    @Query("UPDATE profiles SET extraPoints = extraPoints + :pointsToAdd WHERE id = :id")
    suspend fun addPoints(id: Long, pointsToAdd: Int)
}
