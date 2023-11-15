package com.example.taskmaster.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.datastore.generated.model.Task;
import com.example.taskmaster.R;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


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
            DateFormat dateCreatedIso8061InputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            dateCreatedIso8061InputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            DateFormat dateCreatedOutputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateCreatedOutputFormat.setTimeZone(TimeZone.getDefault());
            String dateCreatedString = "";

            try {
                {
                    Date dateCreatedJavaDate = dateCreatedIso8061InputFormat.parse(task.getDateCreated().format());
                    if (dateCreatedJavaDate != null){
                        dateCreatedString = dateCreatedOutputFormat.format(dateCreatedJavaDate);
                    }
                }
            }catch (ParseException e){
                throw new RuntimeException(e);
            }
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


