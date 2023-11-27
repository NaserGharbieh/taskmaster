package com.example.taskmaster.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.TaskStateEnum;
import com.amplifyframework.datastore.generated.model.Team;
import com.example.taskmaster.R;
import com.example.taskmaster.adapter.RecyclerItemClickListener;
import com.example.taskmaster.adapter.TaskAdapter;

import java.util.ArrayList;
import java.util.List;

public  class MainActivity extends AppCompatActivity {
    public static final String TAG="MainActivity";
    public static final String TASK_ID_TAG="TASK Id TAG";
    SharedPreferences sp;
    private List<Task> taskList = new ArrayList<>();
    public static final String TASK_TAG="taskName";
    public static final String DATABASE_NAME="task_master";

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
                        Intent detailIntent = new Intent(MainActivity.this, EditTaskActivity.class);
                        detailIntent.putExtra(TASK_ID_TAG, task.getId());
                        detailIntent.putExtra("taskDescription", task.getBody());  // Pass description
                        detailIntent.putExtra("taskState", task.getState().name()); // Pass state
                        detailIntent.putExtra("taskTeam", task.getTaskOwnedByTeam().getName()); // Pass Team
                        startActivity(detailIntent);
                    }
                })
        );






        goToEditTask();
}

    @Override
    protected void onResume() {
        super.onResume();
        TextView userTasks;
        userTasks = findViewById(R.id.userTasks);

        String name = sp.getString(UserSettingsActivity.USERNAME_TAG, "no name");
        userTasks.setText(name.isEmpty() ? "tasks" : name + "'s tasks ");
        Amplify.API.query(
                ModelQuery.list(Task.class),
                success ->
                {
                    Log.i(TAG, "Read Tasks successfully");
                    //products = new ArrayList<>();
                    taskList.clear();
                    for (Task databaseTask : success.getData()){
                        taskList.add(databaseTask);
                    }
                    //adapter.notifyDataSetChanged();
                    runOnUiThread(() ->{
                        taskAdapter.notifyDataSetChanged();
                    });
                },
                failure -> Log.i(TAG, "Did not read Tasks successfully")
        );


    }
    private void init() {
        sp= PreferenceManager.getDefaultSharedPreferences(this);
        taskList = new ArrayList<>();
    }
    private void goToEditTask() {
        ImageButton editTasksImageButton = (ImageButton) findViewById(R.id.editTasksImageButton);
        editTasksImageButton.setOnClickListener(view -> {
            Intent goToEditTask = new Intent(MainActivity.this, EditTaskActivity.class);
            startActivity(goToEditTask);
        });

    }

}

