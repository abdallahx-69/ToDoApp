package com.androidapps.todoapp.ui

import android.view.View
import android.widget.TextView
import com.androidapps.todoapp.R
import com.kizitonwose.calendar.view.ViewContainer

class WeekDayViewContainer(itemWeekDayView: View) : ViewContainer(itemWeekDayView) {
    val weekDayTextView: TextView = itemWeekDayView.findViewById(R.id.id_weekDay)
    val dayInMonthTextView: TextView = itemWeekDayView.findViewById(R.id.id_dayInMonth)
}