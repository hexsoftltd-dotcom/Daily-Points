package com.hexsoft.dailypoints.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hexsoft.dailypoints.data.local.entities.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE profileId = :profileId ORDER BY createdAt DESC")
    fun observeTasks(profileId: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE profileId = :profileId AND active = 1 ORDER BY createdAt DESC")
    fun observeActiveTasks(profileId: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id AND profileId = :profileId")
    suspend fun getTaskById(id: Long, profileId: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("UPDATE tasks SET active = :active WHERE id = :id AND profileId = :profileId")
    suspend fun setTaskActive(id: Long, active: Boolean, profileId: Long)

    @Query("SELECT COUNT(*) FROM tasks WHERE profileId = :profileId")
    suspend fun getTaskCount(profileId: Long): Int
}
