package com.example.taskmaster.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmaster.R;
import com.example.taskmaster.model.Task;

import java.util.List;


    public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
        private List<Task> taskList;

        public TaskAdapter(List<Task> tasks) {
            this.taskList = tasks;
        }

        @NonNull
        @Override
        public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
            return new TaskViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
            Task task = taskList.get(position);
            holder.taskTitle.setText(task.getTitle());

        }

        @Override
        public int getItemCount() {
            return taskList.size();
        }

        class TaskViewHolder extends RecyclerView.ViewHolder {
            TextView taskTitle;
            TextView taskBody;
            TextView taskState;

            public TaskViewHolder(@NonNull View itemView) {
                super(itemView);
                taskTitle = itemView.findViewById(R.id.taskTitle);
                taskBody = itemView.findViewById(R.id.taskBody);
                taskState = itemView.findViewById(R.id.taskState);
            }
        }
    }


