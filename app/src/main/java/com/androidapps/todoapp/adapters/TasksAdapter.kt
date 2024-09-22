package com.androidapps.todoapp.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androidapps.todoapp.database.model.Task
import com.androidapps.todoapp.databinding.ItemTaskBinding
import com.androidapps.todoapp.home.EditTaskActivity
import com.androidapps.todoapp.utils.Constance
import java.text.SimpleDateFormat
import java.util.Locale

class TasksAdapter(private var tasksList: List<Task>) :
    RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {

    class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
        fun bind(task: Task) {
            binding.idTaskTime.text = task.time
            binding.idTaskTitle.text = task.title
            val formattedDate = task.date?.let {
                dateFormat.format(it)
            }
            binding.idTaskDate.text = formattedDate ?: "No Date"
            binding.idEditTaskButton.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, EditTaskActivity::class.java)
                intent.putExtra(Constance.TASK_KEY, task)
                context.startActivity(intent)
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
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(tasks: List<Task>) {
        tasksList = tasks
        notifyDataSetChanged()
    }
}