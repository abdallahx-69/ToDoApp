package com.androidapps.todoapp.database.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity(tableName = "Task")
data class Task(
    @PrimaryKey(autoGenerate = true)
    var id: Int?,
    var title: String? = null,
    var description: String? = null,
    var date: Date? = null,
    var time: String? = null,
    var isDone: Boolean? = null
) : Parcelable