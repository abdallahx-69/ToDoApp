package com.androidapps.todoapp.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androidapps.todoapp.R
import com.androidapps.todoapp.database.database.TaskDataBase
import com.androidapps.todoapp.database.model.Task
import com.androidapps.todoapp.databinding.ItemTaskBinding
import com.androidapps.todoapp.ui.EditTaskActivity
import com.androidapps.todoapp.utils.Constance
import java.text.SimpleDateFormat
import java.util.Locale

class TasksAdapter(private var tasksList: List<Task>) :
    RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {
    class TaskViewHolder(val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())

        private fun formatTime(time: String?): String {
            if (time.isNullOrEmpty()) return "No Time"
            val locale = Locale.getDefault().language
            val translatedTime: String = if (locale == "ar") {
                time.replace("AM", "ص").replace("PM", "م")
            } else {
                time.replace("ص", "AM").replace("م", "PM")
            }
            return translateNumbers(translatedTime, locale)
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

        fun bind(task: Task) {
            val formattedTime = formatTime(task.time)
            binding.idTaskTime.text = formattedTime
            binding.idTaskTitle.text = task.title

            val formattedDate = task.date?.let {
                dateFormat.format(it)
            }
            binding.idTaskDate.text = formattedDate ?: "No Date"

            if (task.isDone == true) {
                binding.idTaskTitle.setTextColor(binding.root.context.getColor(R.color.green))
                binding.idTaskStatus.setBackgroundColor(binding.root.context.getColor(R.color.green))
                binding.idDoneTaskButton.setImageResource(R.drawable.icon_done)
            } else {
                binding.idTaskTitle.setTextColor(binding.root.context.getColor(R.color.blue))
                binding.idTaskStatus.setBackgroundColor(binding.root.context.getColor(R.color.blue))
                binding.idDoneTaskButton.setImageResource(R.drawable.icon_check)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return tasksList.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasksList[position]
        holder.bind(task)
        holder.binding.idDoneTaskButton.setOnClickListener {
            doneButton(holder, task)
        }
        editButton(holder, task)
        deleteButton(holder, task)
    }

    private fun doneButton(holder: TaskViewHolder, task: Task) {
        task.isDone = !task.isDone!!

        if (task.isDone == true) {
            holder.binding.idDoneTaskButton.setImageResource(R.drawable.icon_done)
            holder.binding.idTaskTitle.setTextColor(holder.binding.root.context.getColor(R.color.green))
            holder.binding.idTaskStatus.setBackgroundColor(holder.binding.root.context.getColor(R.color.green))
        } else {
            holder.binding.idDoneTaskButton.setImageResource(R.drawable.icon_check)
            holder.binding.idTaskTitle.setTextColor(holder.binding.root.context.getColor(R.color.blue))
            holder.binding.idTaskStatus.setBackgroundColor(holder.binding.root.context.getColor(R.color.blue))
        }

        val context = holder.binding.root.context
        val taskDao = TaskDataBase.getInstance(context).getTaskDao()
        taskDao.updateTask(task)
    }

    private fun editButton(holder: TaskViewHolder, task: Task) {
        holder.binding.idEditTaskButton.setOnClickListener {
            val context = holder.binding.root.context
            val intent = Intent(context, EditTaskActivity::class.java)
            intent.putExtra(Constance.TASK_KEY, task)
            context.startActivity(intent)
        }
    }

    private fun deleteButton(holder: TaskViewHolder, task: Task) {
        holder.binding.idDeleteIcon.setOnClickListener {
            deleteTask(task, holder)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(tasks: List<Task>) {
        tasksList = tasks
        notifyDataSetChanged()
    }

    private fun deleteTask(task: Task, holder: TaskViewHolder) {
        val context = holder.binding.root.context
        val taskDao = TaskDataBase.getInstance(context).getTaskDao()
        taskDao.deleteTask(task)
        updateList(taskDao.getAllTasks())
    }
}