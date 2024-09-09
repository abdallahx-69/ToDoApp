package com.androidapps.todoapp.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.androidapps.todoapp.R
import com.androidapps.todoapp.databinding.ActivityHomeBinding
import com.androidapps.todoapp.fragments.AddTaskBottomSheetFragment
import com.androidapps.todoapp.fragments.SettingsFragment
import com.androidapps.todoapp.fragments.TodoListFragment

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.idIconAdd.setOnClickListener {
            val bottomSheetFragment = AddTaskBottomSheetFragment()
            bottomSheetFragment.show(supportFragmentManager, null)
        }
        binding.idBottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.id_iconList -> pushFragment(TodoListFragment())
                R.id.id_iconSetting -> pushFragment(SettingsFragment())
            }
            return@setOnItemSelectedListener true
        }

        binding.idBottomNavigationView.selectedItemId = R.id.id_iconList
    }

    private fun pushFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(binding.idTodoFragmentContainer.id, fragment)
            .commit()
    }
}