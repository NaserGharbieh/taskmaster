package com.example.taskmaster.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.taskmaster.R;
import com.example.taskmaster.adapter.RecyclerItemClickListener;
import com.example.taskmaster.adapter.TaskAdapter;
import com.example.taskmaster.database.TaskMasterDatabase;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.model.TaskStateEnum;

import java.util.ArrayList;
import java.util.List;

public  class MainActivity extends AppCompatActivity {
    SharedPreferences sp;
    private List<Task> taskList = new ArrayList<>();
    public static final String TASK_TAG="taskName";
    public static final String DATABASE_NAME="task_master";
    TaskMasterDatabase taskMasterDatabase;
    TaskAdapter taskAdapter;

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



        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(taskList);
        recyclerView.setAdapter(taskAdapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Task task = taskList.get(position);
                        Intent detailIntent = new Intent(MainActivity.this, TaskDetailsActivity.class);
                        detailIntent.putExtra(TASK_TAG, task.getTitle());
                        detailIntent.putExtra("taskDescription", task.getBody());  // Pass description
                        detailIntent.putExtra("taskState", task.getState().name()); // Pass state
                        startActivity(detailIntent);
                    }
                })
        );

        taskMasterDatabase= Room.databaseBuilder(
                getApplicationContext(),
                TaskMasterDatabase.class,
                 DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        taskMasterDatabase.taskDao().findAll();






}

    @Override
    protected void onResume() {
        super.onResume();
        TextView userTasks;
        userTasks = findViewById(R.id.userTasks);

        String name = sp.getString(UserSettingsActivity.USERNAME_TAG, "no name");
        userTasks.setText(name.isEmpty() ? "tasks" : name + "'s tasks ");
        taskList.clear();
        taskList.addAll(taskMasterDatabase.taskDao().findAll());
        taskAdapter.notifyDataSetChanged();

    }

}

