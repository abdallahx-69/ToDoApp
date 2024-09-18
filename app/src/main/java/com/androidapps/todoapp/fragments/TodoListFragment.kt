package com.androidapps.todoapp.fragments

import android.annotation.SuppressLint
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.androidapps.todoapp.R
import com.androidapps.todoapp.adapters.TasksAdapter
import com.androidapps.todoapp.database.database.TaskDataBase
import com.androidapps.todoapp.databinding.FragmentTodoListBinding
import com.androidapps.todoapp.home.WeekDayViewContainer
import com.kizitonwose.calendar.core.Week
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.WeekDayBinder
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale

class TodoListFragment : Fragment() {

    private lateinit var binding: FragmentTodoListBinding
    private lateinit var adapter: TasksAdapter
    var selectedDate: LocalDate? = null
    lateinit var calendar: Calendar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentTodoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calendar = Calendar.getInstance()
        getTasksFromDatabase()
        initWeekCalendarView()
    }

    fun getTasksFromDatabase() {
        if (!isAdded) {
            return
        }
        val context = requireContext()
        val tasks = TaskDataBase.getInstance(context).getTaskDao().getAllTasks()
        adapter = TasksAdapter(tasks)
        binding.idTaskRecyclerView.adapter = adapter
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
                    TextStyle.SHORT, Locale.getDefault()
                )
                container.dayInMonthTextView.text = "${data.date.dayOfMonth}"
                val context = container.view.context
                val selectedTextColor =
                    ResourcesCompat.getColor(context.resources, R.color.blue, null)
                val defaultTextColor = if (context.resources.configuration.uiMode and
                    android.content.res.Configuration.UI_MODE_NIGHT_MASK == android.content.res.Configuration.UI_MODE_NIGHT_YES
                ) {
                    ResourcesCompat.getColor(context.resources, R.color.white, null)
                } else {
                    ResourcesCompat.getColor(context.resources, R.color.dark_gray, null)
                }
                if (data.date == selectedDate) {
                    container.dayInMonthTextView.setTextColor(selectedTextColor)
                    container.weekDayTextView.setTextColor(selectedTextColor)
                } else {
                    container.dayInMonthTextView.setTextColor(defaultTextColor)
                    container.weekDayTextView.setTextColor(defaultTextColor)
                }
                container.view.setOnClickListener {
                    selectedDate = data.date
                    binding.idWeekCalendarView.notifyWeekChanged(data)
                    val date = data.date
                    calendar.set(Calendar.YEAR, date.year)
                    calendar.set(Calendar.MONTH, date.month.value - 1)
                    calendar.set(Calendar.DAY_OF_MONTH, date.dayOfMonth)
                    calendar.set(Calendar.HOUR, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                    getTasksByDate(calendar.time)
                    updateUI()
                }
            }

            override fun create(view: View): WeekDayViewContainer {
                return WeekDayViewContainer(view)
            }
        }
        binding.idWeekCalendarView.weekScrollListener = { week: Week ->
            val month = week.days[0].date.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
            binding.idMonthNameText.text = month
        }
    }


    fun getTasksByDate(date: Date) {
        val tasks = TaskDataBase.getInstance(requireContext()).getTaskDao().getTaskByDate(date)
        adapter.updateList(tasks)
    }

    fun clearDateSelection() {
        selectedDate = null
        binding.idWeekCalendarView.notifyCalendarChanged()
    }

    override fun onResume() {
        super.onResume()
        clearDateSelection()
        updateUI()
    }

    fun updateUI() {
        if (selectedDate != null) {
            getTasksByDate(
                Date.from(
                    selectedDate!!.atStartOfDay(ZoneId.systemDefault()).toInstant()
                )
            )
        } else {
            getTasksFromDatabase()
        }
        binding.idWeekCalendarView.notifyCalendarChanged()
    }

}