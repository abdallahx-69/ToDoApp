package com.androidapps.todoapp

import android.view.View
import android.widget.TextView
import com.kizitonwose.calendar.view.ViewContainer

class WeekDayViewContainer(private val itemWeekDayView: View) : ViewContainer(itemWeekDayView) {
    val weekDayTextView: TextView = itemWeekDayView.findViewById(R.id.id_weekDay)
    val dayInMonthTextView: TextView = itemWeekDayView.findViewById(R.id.id_dayInMonth)
}