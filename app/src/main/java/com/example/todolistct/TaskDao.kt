package com.example.todolistct

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long // Correct return type for insert

    @Delete
    suspend fun deleteTask(task: Task): Int // Correct return type for delete
}
