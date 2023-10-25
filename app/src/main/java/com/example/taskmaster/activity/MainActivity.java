package com.example.taskmaster.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmaster.R;
import com.example.taskmaster.adapter.RecyclerItemClickListener;
import com.example.taskmaster.adapter.TaskAdapter;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.model.TaskState;

import java.util.ArrayList;
import java.util.List;

public  class MainActivity extends AppCompatActivity {
    SharedPreferences sp;
    public static final String TASK_TAG="taskName";
    private List<Task> taskList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        Button allTasksButton =(Button)  findViewById(R.id.allTasksButton);
        allTasksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToallTasksIntent = new Intent(MainActivity.this,AllTasksActivity.class);
            startActivity(goToallTasksIntent);
            }
        });

        Button addNewTaskButton =(Button)  findViewById(R.id.addNewTaskButton);
        addNewTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToAddNewTaskIntent = new Intent(MainActivity.this,AddNewTaskActivity.class);
            startActivity(goToAddNewTaskIntent);
            }
        });
        ImageButton userSettingsImageButton = (ImageButton) findViewById(R.id.userSettingsImageButton);
        userSettingsImageButton.setOnClickListener(view -> {
            Intent goToUserSettings = new Intent(MainActivity.this, UserSettingsActivity.class);
            startActivity(goToUserSettings);
        });


        taskList.add(new Task("JAVA Tasks", "Task 1 description", TaskState.NEW));
        taskList.add(new Task("Re design", "Try to make the main activity more appealing", TaskState.ASSIGNED));
        taskList.add(new Task("Study DSA", "Task 3 description", TaskState.IN_PROGRESS));
   taskList.add(new Task("do cv ", "Task 4description", TaskState.NEW));
        taskList.add(new Task("do the presntaion", " Leadership about comunication", TaskState.ASSIGNED));
        taskList.add(new Task("do the interview ", "Task 3 description", TaskState.IN_PROGRESS));

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        TaskAdapter taskAdapter = new TaskAdapter(taskList);
        recyclerView.setAdapter(taskAdapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Task task = taskList.get(position);
                        Intent detailIntent = new Intent(MainActivity.this, TaskDetailsActivity.class);
                        detailIntent.putExtra(TASK_TAG, task.getTitle());
                        startActivity(detailIntent);
                    }
                })
        );






}

    @Override
    protected void onResume() {
        super.onResume();
        TextView userTasks;
        userTasks = findViewById(R.id.userTasks);
        //SharedPreferences sp = getApplicationContext().getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        String name = sp.getString(UserSettingsActivity.USERNAME_TAG, "no name");
        userTasks.setText(name.isEmpty() ? "tasks" : name + "'s tasks ");
    }

}

