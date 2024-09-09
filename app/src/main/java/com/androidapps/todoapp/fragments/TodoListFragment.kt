package com.androidapps.todoapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.androidapps.todoapp.WeekDayViewContainer
import com.androidapps.todoapp.databinding.FragmentTodoListBinding
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.WeekDayBinder
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

class TodoListFragment : Fragment() {
    private lateinit var binding: FragmentTodoListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTodoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWeekCalendarView()
    }

    private fun initWeekCalendarView() {
        bindWeekCalendarView()
        val currentDate = LocalDate.now()
        val currentMonth = YearMonth.now()
        val startDate = currentMonth.minusMonths(100).atStartOfMonth()
        val endDate = currentMonth.plusMonths(100).atEndOfMonth()
        val firstDayOfWeek = firstDayOfWeekFromLocale(Locale.forLanguageTag("ar"))
        binding.idWeekCalendarView.setup(startDate, endDate, firstDayOfWeek)
        binding.idWeekCalendarView.scrollToWeek(currentDate)
    }

    private fun bindWeekCalendarView() {
        binding.idWeekCalendarView.dayBinder = object : WeekDayBinder<WeekDayViewContainer> {

            @SuppressLint("SetTextI18n")
            override fun bind(container: WeekDayViewContainer, data: WeekDay) {
                container.weekDayTextView.text = data.date.dayOfWeek.getDisplayName(
                    TextStyle.SHORT,
                    Locale.getDefault()
                )
                container.dayInMonthTextView.text = "${data.date.dayOfMonth}"
            }

            override fun create(view: View): WeekDayViewContainer {
                return WeekDayViewContainer(view)
            }
        }
    }
}