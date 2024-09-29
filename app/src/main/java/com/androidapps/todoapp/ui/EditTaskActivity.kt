package com.androidapps.todoapp.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.androidapps.todoapp.R
import com.androidapps.todoapp.database.database.TaskDataBase
import com.androidapps.todoapp.database.model.Task
import com.androidapps.todoapp.databinding.ActivityEditTaskBinding
import com.androidapps.todoapp.utils.Constance
import com.androidapps.todoapp.utils.LocaleHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Calendar.HOUR_OF_DAY
import java.util.Calendar.MILLISECOND
import java.util.Calendar.MINUTE
import java.util.Calendar.SECOND
import java.util.Date
import java.util.Locale

class EditTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditTaskBinding
    private lateinit var intentTask: Task
    private lateinit var editedTask: Task
    private val dateCalendar = Calendar.getInstance()
    private val timeCalendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intentTask = intent.getParcelableExtra(Constance.TASK_KEY)!!
        editedTask = intentTask.copy()
        LocaleHelper.setLocale(this, LocaleHelper.getPersistedLanguage(this))
        initViews()
        setUpToolbar()
        onSelectTime()
        onSelectDate()
        onSaveClick()
    }

    override fun attachBaseContext(newBase: Context) {
        val localeUpdatedContext =
            LocaleHelper.setLocale(newBase, LocaleHelper.getPersistedLanguage(newBase))
        super.attachBaseContext(localeUpdatedContext)
    }

    private fun onSaveClick() {
        binding.idContent.idButtonSaveChanges.setOnClickListener {
            updateTask()
            if (isFieldsValid()) {
                finish()
            }
        }
    }

    private fun updateTask() {
        if (!isFieldsValid()) {
            return
        }
        editedTask.apply {
            title = binding.idContent.idTitle.text.toString()
            description = binding.idContent.idDescription.text.toString()
        }
        TaskDataBase.getInstance(this).getTaskDao().updateTask(editedTask)
    }

    private fun isFieldsValid(): Boolean {
        var isValid = true
        if (binding.idContent.idTitle.text.isNullOrBlank()) {
            isValid = false
            binding.idContent.idTitleTextInputLayout.error = getString(R.string.required)
        } else {
            binding.idContent.idTitleTextInputLayout.error = null
        }
        return isValid
    }

    @SuppressLint("SetTextI18n")
    private fun onSelectDate() {
        binding.idContent.idSelectDateTextInputLayout.setOnClickListener {
            intentTask.date?.let {
                dateCalendar.timeInMillis = it.time
            }
            val dialog = DatePickerDialog(
                this,
                R.style.CustomDatePickerDialog,
                { _, year, month, day ->
                    dateCalendar.clearTime()
                    dateCalendar.set(Calendar.YEAR, year)
                    dateCalendar.set(Calendar.MONTH, month)
                    dateCalendar.set(Calendar.DAY_OF_MONTH, day)
                    editedTask.date = Date(dateCalendar.timeInMillis)
                    updateDateText(dateCalendar)
                },
                dateCalendar.get(Calendar.YEAR),
                dateCalendar.get(Calendar.MONTH),
                dateCalendar.get(Calendar.DAY_OF_MONTH)
            )
            dialog.show()
        }
    }

    private fun updateDateText(calendar: Calendar) {
        val locale = Locale(LocaleHelper.getPersistedLanguage(this))
        val dateFormat = SimpleDateFormat("d/M/yyyy", locale)
        binding.idContent.idSelectDate.text = dateFormat.format(calendar.time)
    }

    private fun onSelectTime() {
        binding.idContent.idSelectTimeTextInputLayout.setOnClickListener {
            intentTask.time?.let { time ->
                val timeParts = time.split("[: ]".toRegex())
                val hour = timeParts[0].toInt()
                val minute = timeParts[1].toInt()
                val isPM = timeParts[2] == getString(R.string.pm)
                val hourIn24 =
                    if (isPM && hour < 12) hour + 12 else if (!isPM && hour == 12) 0 else hour
                timeCalendar.set(HOUR_OF_DAY, hourIn24)
                timeCalendar.set(MINUTE, minute)
            }
            val dialog = TimePickerDialog(
                this,
                R.style.CustomTimePickerDialog,
                { _, hour, minutes ->
                    val timeString = formattedTime(hour, minutes)
                    binding.idContent.idSelectTime.text = timeString
                    editedTask.time = timeString
                },
                timeCalendar.get(HOUR_OF_DAY),
                timeCalendar.get(MINUTE),
                false
            )
            dialog.show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        binding.idContent.idTitle.setText(intentTask.title)
        binding.idContent.idDescription.setText(intentTask.description)
        intentTask.date?.let { date ->
            dateCalendar.timeInMillis = date.time
            updateDateText(dateCalendar)
        }
        intentTask.time?.let {
            val formattedTime = formatTime(it)
            binding.idContent.idSelectTime.text = formattedTime
        }
    }

    private fun formatTime(time: String?): String {
        if (time.isNullOrEmpty()) return "No Time"
        val locale = LocaleHelper.getPersistedLanguage(this)
        val translatedTime: String = if (locale == "ar") {
            time.replace("AM", "ص").replace("PM", "م")
        } else {
            time.replace("ص", "AM").replace("م", "PM")
        }
        return translateNumbers(translatedTime, locale)
    }

    private fun formattedTime(hour: Int, minutes: Int): String {
        val hourIn12 = if (hour > 12) hour - 12 else if (hour == 0) 12 else hour
        val AM_PM = if (hour < 12) getString(R.string.am) else getString(R.string.pm)
        val minuteString = if (minutes < 10) "0$minutes" else minutes.toString()
        val locale = LocaleHelper.getPersistedLanguage(this)
        return translateNumbers("$hourIn12:$minuteString $AM_PM", locale)
    }

    private fun translateNumbers(time: String, locale: String): String {
        return if (locale == "ar") {
            time.replace("0", "٠")
                .replace("1", "١")
                .replace("2", "٢")
                .replace("3", "٣")
                .replace("4", "٤")
                .replace("5", "٥")
                .replace("6", "٦")
                .replace("7", "٧")
                .replace("8", "٨")
                .replace("9", "٩")
        } else {
            time.replace("٠", "0")
                .replace("١", "1")
                .replace("٢", "2")
                .replace("٣", "3")
                .replace("٤", "4")
                .replace("٥", "5")
                .replace("٦", "6")
                .replace("٧", "7")
                .replace("٨", "8")
                .replace("٩", "9")
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.idTodoToolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.idTodoToolBar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun Calendar.clearTime() {
        set(HOUR_OF_DAY, 0)
        set(MINUTE, 0)
        set(SECOND, 0)
        set(MILLISECOND, 0)
    }
}
