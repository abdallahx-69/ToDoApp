package com.androidapps.todoapp.home

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.androidapps.todoapp.R
import com.androidapps.todoapp.database.database.TaskDataBase
import com.androidapps.todoapp.database.model.Task
import com.androidapps.todoapp.databinding.ActivityEditTaskBinding
import com.androidapps.todoapp.utils.Constance
import java.util.Calendar
import java.util.Calendar.HOUR_OF_DAY
import java.util.Calendar.MILLISECOND
import java.util.Calendar.MINUTE
import java.util.Calendar.SECOND
import java.util.Date

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
        initViews()
        setUpToolbar()
        onSelectTime()
        onSelectDate()
        onSaveClick()
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
            val dialog = DatePickerDialog(
                this,
                R.style.CustomDatePickerDialog,
                { _, year, month, day ->
                    binding.idContent.idSelectDate.text = "$day/${month + 1}/$year"
                    dateCalendar.clearTime()
                    dateCalendar.set(Calendar.YEAR, year)
                    dateCalendar.set(Calendar.MONTH, month)
                    dateCalendar.set(Calendar.DAY_OF_MONTH, day)
                    editedTask.date = Date(dateCalendar.timeInMillis)
                },
                dateCalendar.get(Calendar.YEAR),
                dateCalendar.get(Calendar.MONTH),
                dateCalendar.get(Calendar.DAY_OF_MONTH)
            )
            dialog.show()
        }
    }

    private fun onSelectTime() {
        binding.idContent.idSelectTimeTextInputLayout.setOnClickListener {
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
        val dateCalendar = Calendar.getInstance()
        intentTask.date?.let { date ->
            dateCalendar.timeInMillis = date.time
            val year = dateCalendar.get(Calendar.YEAR)
            val month = dateCalendar.get(Calendar.MONTH)
            val day = dateCalendar.get(Calendar.DAY_OF_MONTH)
            binding.idContent.idSelectDate.text = "$day/${month + 1}/$year"
        }
        intentTask.time?.let {
            binding.idContent.idSelectTime.text = it
        }
    }

    private fun formattedTime(hour: Int, minutes: Int): String {
        val hourIn12 = if (hour > 12) hour - 12 else if (hour == 0) 12 else hour
        val AM_PM = if (hour < 12) "AM" else "PM"
        val minuteString = if (minutes < 10) "0$minutes" else minutes.toString()
        return "$hourIn12:$minuteString $AM_PM"
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
