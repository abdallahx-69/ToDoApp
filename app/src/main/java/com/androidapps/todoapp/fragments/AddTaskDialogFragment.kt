package com.androidapps.todoapp.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.androidapps.todoapp.R
import com.androidapps.todoapp.callbacks.OnTaskAddedListener
import com.androidapps.todoapp.database.database.TaskDataBase
import com.androidapps.todoapp.database.model.Task
import com.androidapps.todoapp.databinding.FragmentAddTodoBinding
import com.androidapps.todoapp.utils.LocaleHelper

class AddTaskDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentAddTodoBinding
    private lateinit var calendar: Calendar
    private var isDateSelected: Boolean = false
    private var isTimeSelected: Boolean = false
    var onTaskAddedListener: OnTaskAddedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddTodoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        calendar = Calendar.getInstance()
        initViews()
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun initViews() {
        binding.idAddButton.setOnClickListener {

            if (validateFields()) {
                calendar.set(Calendar.HOUR, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val task = Task(
                    null,
                    binding.idTitleEditText.text.toString(),
                    binding.idDescriptionEditText.text.toString(),
                    calendar.time,
                    binding.idSelectTaskTime.text.toString(),
                    false
                )
                TaskDataBase.getInstance(requireContext()).getTaskDao().insertTask(task)
                onTaskAddedListener?.onTaskAdded()
                dismiss()
            }
        }

        binding.idSelectTaskDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                R.style.CustomDatePickerDialog,
                { _, year, month, dayOfMonth ->
                    isDateSelected = true
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val locale = LocaleHelper.getPersistedLanguage(requireContext())
                    val formattedDate = formatDate(dayOfMonth, month + 1, year)
                    binding.idSelectTaskDate.text = translateNumbers(formattedDate, locale)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            LocaleHelper.setLocale(
                requireContext(),
                LocaleHelper.getPersistedLanguage(requireContext())
            )
            datePickerDialog.setOnShowListener {
            }
            datePickerDialog.show()
        }

        binding.idSelectTaskTime.setOnClickListener {
            val timePicker = TimePickerDialog(
                requireContext(),
                R.style.CustomTimePickerDialog,
                { _, hourOfDay, minute ->
                    isTimeSelected = true
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    val locale = LocaleHelper.getPersistedLanguage(requireContext())
                    val formattedTime = formatTime(hourOfDay, minute, locale)
                    binding.idSelectTaskTime.text = translateNumbers(formattedTime, locale)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            )
            LocaleHelper.setLocale(
                requireContext(),
                LocaleHelper.getPersistedLanguage(requireContext())
            )
            timePicker.show()
        }
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

    private fun formatDate(day: Int, month: Int, year: Int): String {
        return "$day/$month/$year"
    }

    private fun formatTime(hourOfDay: Int, minute: Int, locale: String): String {
        var hour = hourOfDay % 12
        if (hour == 0) hour = 12
        val formattedMinute = if (minute < 10) "0$minute" else "$minute"
        val amPm = if (hourOfDay >= 12) {
            if (locale == "ar") "م" else "PM"
        } else {
            if (locale == "ar") "ص" else "AM"
        }
        return "$hour:$formattedMinute $amPm"
    }

    private fun validateFields(): Boolean {

        if (binding.idTitleEditText.text.isEmpty() || binding.idTitleEditText.text.isBlank()) {
            binding.idTitleEditText.error = getString(R.string.required)
            return false
        } else {
            binding.idTitleEditText.error = null
        }

        if (binding.idDescriptionEditText.text.isEmpty() || binding.idDescriptionEditText.text.isBlank()) {
            binding.idDescriptionEditText.error = getString(R.string.required)
            return false
        } else {
            binding.idDescriptionEditText.error = null
        }

        if (!isDateSelected) {
            Toast.makeText(
                requireContext(),
                getString(R.string.select_date_because_it_s_required),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        if (!isTimeSelected) {
            Toast.makeText(
                requireContext(),
                getString(R.string.select_time_because_it_s_required),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }
}