package com.hexsoft.dailypoints.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hexsoft.dailypoints.data.local.entities.PlannedTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlannedTaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plan: PlannedTaskEntity): Long

    @Query("DELETE FROM planned_tasks WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query(
        """
        SELECT p.*
        FROM planned_tasks p
        INNER JOIN tasks t ON t.id = p.taskId
        WHERE p.plannedDate = :date AND t.profileId = :profileId
        """,
    )
    fun observePlannedTasks(date: String, profileId: Long): Flow<List<PlannedTaskEntity>>
}
