package com.example.taskmaster.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.taskmaster.R;

public class TaskDetailsActivity extends AppCompatActivity {
    TextView taskTitle;
    String taskTitleStr;
    TextView taskDescription;
    TextView taskState;
    TextView taskTeam;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent callingIntent = getIntent();
        String taskTitleStr = callingIntent.getStringExtra(MainActivity.TASK_TAG);
        String taskDescriptionStr = callingIntent.getStringExtra("taskDescription");
        String taskStateStr = callingIntent.getStringExtra("taskState");
        String taskTeamStr =callingIntent.getStringExtra("taskTeam");
        taskTitle = findViewById(R.id.textViewTitle);
        taskDescription = findViewById(R.id.textViewDescription);
        taskState = findViewById(R.id.textViewState);
         taskTeam = findViewById(R.id.textViewTeam);

        if (taskTitleStr != null) {
            taskTitle.setText(taskTitleStr);
        } else {
            taskTitle.setText("Not Specified");
        }

        if (taskDescriptionStr != null) {
            taskDescription.setText("Description: " + taskDescriptionStr);
        } else {
            taskDescription.setText("Description: Not Specified");
        }

        if (taskStateStr != null) {
            taskState.setText("State: " + taskStateStr);
        } else {
            taskState.setText("State: Not Specified");
        }
        if (taskTeamStr != null) {
            taskTeam.setText("Team: " + taskTeamStr);
        } else {
            taskTeam.setText("Team : Not Specified");
        }
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }



}
