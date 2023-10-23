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

import com.example.taskmaster.R;

public  class MainActivity extends AppCompatActivity {
    SharedPreferences sp;
    public static final String TASK_TAG="taskName";

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

        Button task1 = findViewById(R.id.task1);
        task1.setOnClickListener(view -> {
            Intent goToTaskDetails = new Intent(MainActivity.this, TaskDetailsActivity.class);
            goToTaskDetails.putExtra(TASK_TAG,task1.getText().toString());
            startActivity(goToTaskDetails);
        });
        Button task2 = findViewById(R.id.task2);
        task2.setOnClickListener(view -> {
            Intent goToTaskDetails = new Intent(MainActivity.this, TaskDetailsActivity.class);
            goToTaskDetails.putExtra(TASK_TAG,task2.getText().toString());
            startActivity(goToTaskDetails);
        });
        Button task3 = findViewById(R.id.task3);
        task3.setOnClickListener(view -> {
            Intent goToTaskDetails = new Intent(MainActivity.this, TaskDetailsActivity.class);
            goToTaskDetails.putExtra(TASK_TAG,task3.getText().toString());
            startActivity(goToTaskDetails);
        });




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

