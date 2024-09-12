package com.androidapps.todoapp.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.androidapps.todoapp.database.model.Task

@Dao
interface TaskDao {
    @Insert
    fun insertTask(task: Task)

    @Delete
    fun deleteTask(task: Task)

    @Update
    fun updateTask(task: Task)

    @Query("SELECT * FROM Task")
    fun getAllTasks(): List<Task>

    //
    @Query("SELECT * FROM Task WHERE date = :date")
    fun getTaskByDate(date: java.util.Date): List<Task>
}