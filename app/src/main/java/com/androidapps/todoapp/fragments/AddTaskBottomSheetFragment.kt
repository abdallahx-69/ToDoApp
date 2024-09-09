package com.androidapps.todoapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androidapps.todoapp.databinding.FragmentAddTodoBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddTaskBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAddTodoBinding
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
    }
}