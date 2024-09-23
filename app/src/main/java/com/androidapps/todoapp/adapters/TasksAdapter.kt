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
import com.androidapps.todoapp.home.EditTaskActivity
import com.androidapps.todoapp.utils.Constance
import java.text.SimpleDateFormat
import java.util.Locale

class TasksAdapter(private var tasksList: List<Task>) :
    RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {

    class TaskViewHolder(val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
        fun bind(task: Task) {
            binding.idTaskTime.text = task.time
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