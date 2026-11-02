package com.hexsoft.dailypoints.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.hexsoft.dailypoints.data.local.entities.CompletionWithTaskEntity
import com.hexsoft.dailypoints.data.local.entities.TaskCompletionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskCompletionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCompletion(completion: TaskCompletionEntity): Long

    @Query("DELETE FROM task_completions WHERE taskId = :taskId AND date = :date")
    suspend fun deleteCompletion(taskId: Long, date: String)

    @Query(
        """
        SELECT c.*
        FROM task_completions c
        INNER JOIN tasks t ON t.id = c.taskId
        WHERE c.date = :date AND t.profileId = :profileId
        """,
    )
    fun observeCompletionsForDate(date: String, profileId: Long): Flow<List<TaskCompletionEntity>>

    @Query("SELECT * FROM task_completions WHERE taskId = :taskId AND date = :date LIMIT 1")
    suspend fun getCompletion(taskId: Long, date: String): TaskCompletionEntity?

    @Query(
        """
        SELECT COALESCE(SUM(t.points), 0)
        FROM task_completions c
        INNER JOIN tasks t ON t.id = c.taskId
        WHERE c.date = :date AND c.completed = 1 AND t.profileId = :profileId
        """,
    )
    fun observePointsForDate(date: String, profileId: Long): Flow<Int>

    @Query(
        """
        SELECT COALESCE(SUM(t.points), 0)
        FROM task_completions c
        INNER JOIN tasks t ON t.id = c.taskId
        WHERE c.date = :date AND c.completed = 1 AND t.profileId = :profileId
        """,
    )
    suspend fun getPointsForDate(date: String, profileId: Long): Int

    @Query(
        """
        SELECT COALESCE(SUM(t.points), 0)
        FROM task_completions c
        INNER JOIN tasks t ON t.id = c.taskId
        WHERE c.date BETWEEN :startDate AND :endDate AND c.completed = 1 AND t.profileId = :profileId
        """,
    )
    suspend fun getPointsInRange(startDate: String, endDate: String, profileId: Long): Int

    @Transaction
    @Query(
        """
        SELECT c.*
        FROM task_completions c
        INNER JOIN tasks t ON t.id = c.taskId
        WHERE c.date BETWEEN :startDate AND :endDate AND t.profileId = :profileId
        """,
    )
    suspend fun getCompletionsWithTasksInRange(
        startDate: String,
        endDate: String,
        profileId: Long,
    ): List<CompletionWithTaskEntity>

    @Query(
        """
        SELECT DISTINCT c.date
        FROM task_completions c
        INNER JOIN tasks t ON t.id = c.taskId
        WHERE c.completed = 1 AND t.profileId = :profileId
        ORDER BY c.date ASC
        """,
    )
    suspend fun getCompletedDates(profileId: Long): List<String>

    @Query(
        """
        SELECT COALESCE(SUM(t.points), 0)
        FROM task_completions c
        INNER JOIN tasks t ON t.id = c.taskId
        WHERE c.completed = 1 AND t.profileId = :profileId
        """,
    )
    suspend fun getAllTimeCompletedPoints(profileId: Long): Int
}
