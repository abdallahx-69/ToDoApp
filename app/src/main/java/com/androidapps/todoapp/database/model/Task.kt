package com.androidapps.todoapp.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity("Todos")
data class Task(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    val title: String? = null,
    val description: String? = null,
    val date: Date? = null,
    val isDone: Boolean? = null
)
