package com.hexsoft.dailypoints.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hexsoft.dailypoints.data.local.entities.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(goal: GoalEntity)

    @Query("SELECT * FROM goals WHERE id = 1")
    fun observeGoal(): Flow<GoalEntity?>

    @Query("SELECT * FROM goals WHERE id = 1")
    suspend fun getGoal(): GoalEntity?
}
