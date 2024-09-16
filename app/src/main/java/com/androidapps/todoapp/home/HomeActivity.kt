package com.androidapps.todoapp.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.androidapps.todoapp.R
import com.androidapps.todoapp.callbacks.OnTaskAddedListener
import com.androidapps.todoapp.databinding.ActivityHomeBinding
import com.androidapps.todoapp.fragments.AddTaskBottomSheetFragment
import com.androidapps.todoapp.fragments.SettingsFragment
import com.androidapps.todoapp.fragments.TodoListFragment

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var todoListFragment: TodoListFragment
    private lateinit var settingsFragment: SettingsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        todoListFragment = TodoListFragment()
        settingsFragment = SettingsFragment()
        binding.idIconAdd.setOnClickListener {
            val bottomSheetFragment = AddTaskBottomSheetFragment()
            bottomSheetFragment.onTaskAddedListener = object : OnTaskAddedListener {
                override fun onTaskAdded() {
                    if (todoListFragment.isVisible) {
                        if (todoListFragment.selectedDate == null) {
                            todoListFragment.getTasksFromDatabase()
                        } else {
                            todoListFragment.getTasksByDate(todoListFragment.calendar.time)
                        }
                    }
                }
            }
            bottomSheetFragment.show(supportFragmentManager, null)
        }
        binding.idBottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.id_iconList -> {
                    if (todoListFragment.isAdded) {
                        todoListFragment.clearDateSelection()
                        todoListFragment.updateUI()
                    }
                    pushFragment(todoListFragment)
                }
                R.id.id_iconSetting -> pushFragment(settingsFragment)
            }
            return@setOnItemSelectedListener true
        }
        binding.idBottomNavigationView.selectedItemId = R.id.id_iconList
    }

    private fun pushFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.idTodoFragmentContainer.id, fragment).commit()
    }

}