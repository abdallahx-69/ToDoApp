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
import com.androidapps.todoapp.R
import com.androidapps.todoapp.callbacks.OnTaskAddedListener
import com.androidapps.todoapp.database.database.TaskDataBase
import com.androidapps.todoapp.database.model.Task
import com.androidapps.todoapp.databinding.FragmentAddTodoBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddTaskBottomSheetFragment : BottomSheetDialogFragment() {
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
        calendar = Calendar.getInstance()
        initViews()
    }

    @SuppressLint("SetTextI18n")
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
            val datePicker = DatePickerDialog(requireContext())
            datePicker.setOnDateSetListener { _, year, month, dayOfMonth ->
                isDateSelected = true
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                binding.idSelectTaskDate.text = "$dayOfMonth/${month + 1}/$year"
            }
            datePicker.show()
        }
        binding.idSelectTaskTime.setOnClickListener {
            val timePicker = TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    isTimeSelected = true
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    var hour = hourOfDay % 12
                    if (hour == 0) hour = 12
                    val AM_PM = if ((hourOfDay > 12)) "PM" else "Am"
                    binding.idSelectTaskTime.text = "${hour}:${minute} $AM_PM"
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            )
            timePicker.show()
        }

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