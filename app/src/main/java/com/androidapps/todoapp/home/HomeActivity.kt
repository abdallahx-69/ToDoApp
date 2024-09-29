package com.androidapps.todoapp.home

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.androidapps.todoapp.R
import com.androidapps.todoapp.callbacks.OnTaskAddedListener
import com.androidapps.todoapp.databinding.ActivityHomeBinding
import com.androidapps.todoapp.fragments.AddTaskDialogFragment
import com.androidapps.todoapp.fragments.TodoListFragment
import com.androidapps.todoapp.utils.LocaleHelper

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var todoListFragment: TodoListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        todoListFragment = TodoListFragment()

        binding.idIconAdd.setOnClickListener {
            val addTaskDialogFragment = AddTaskDialogFragment()
            addTaskDialogFragment.onTaskAddedListener = object : OnTaskAddedListener {
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
            addTaskDialogFragment.show(supportFragmentManager, "AddTaskDialogFragment")
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

                R.id.id_iconLanguage -> {
                    val fadeOut = AnimationUtils.loadAnimation(this, R.anim.anim_out)
                    val fadeIn = AnimationUtils.loadAnimation(this, R.anim.anim_in)
                    val rootView = window.decorView.findViewById<View>(android.R.id.content)
                    rootView.startAnimation(fadeOut)
                    fadeOut.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation) {}
                        override fun onAnimationEnd(animation: Animation) {
                            val currentLocale = LocaleHelper.getLocale(this@HomeActivity)
                            val newLocale = if (currentLocale.language == "en") "ar" else "en"
                            LocaleHelper.setLocale(this@HomeActivity, newLocale)
                            recreate()
                            rootView.startAnimation(fadeIn)
                            binding.idBottomNavigationView.selectedItemId = R.id.id_iconList
                        }

                        override fun onAnimationRepeat(animation: Animation) {}
                    })
                }

                R.id.id_iconMode -> {
                    val fadeOut = AnimationUtils.loadAnimation(this, R.anim.anim_out)
                    val fadeIn = AnimationUtils.loadAnimation(this, R.anim.anim_in)
                    val rootView = window.decorView.findViewById<View>(android.R.id.content)
                    rootView.startAnimation(fadeOut)
                    fadeOut.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation) {}
                        override fun onAnimationEnd(animation: Animation) {
                            val currentNightMode =
                                resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                            } else {
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                            }
                            rootView.startAnimation(fadeIn)
                            binding.idBottomNavigationView.selectedItemId = R.id.id_iconList
                        }

                        override fun onAnimationRepeat(animation: Animation) {}
                    })
                }
            }
            return@setOnItemSelectedListener true
        }
        binding.idBottomNavigationView.selectedItemId = R.id.id_iconList
    }

    override fun attachBaseContext(newBase: Context) {
        val localeUpdatedContext =
            LocaleHelper.setLocale(newBase, LocaleHelper.getPersistedLanguage(newBase))
        super.attachBaseContext(localeUpdatedContext)
    }

    private fun pushFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.idTodoFragmentContainer.id, fragment).commit()
    }
}