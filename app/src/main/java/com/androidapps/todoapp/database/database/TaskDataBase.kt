package com.androidapps.todoapp.database.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.androidapps.todoapp.database.dao.TaskDao
import com.androidapps.todoapp.database.model.Task
import com.androidapps.todoapp.database.typeConverters.DataConverter

@Database([Task::class], version = 1, exportSchema = false)
@TypeConverters(DataConverter::class)
abstract class TaskDataBase : RoomDatabase() {
    abstract fun getTaskDao(): TaskDao

    companion object {
        private var INSTANCE: TaskDataBase? = null
        private const val DATABASE_NAME = "Tasks Database"
        fun getInstance(context: Context): TaskDataBase {
            if (INSTANCE == null) {
                INSTANCE =
                    Room.databaseBuilder(context, TaskDataBase::class.java, DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build()
            }
            return INSTANCE!!
        }
    }
}