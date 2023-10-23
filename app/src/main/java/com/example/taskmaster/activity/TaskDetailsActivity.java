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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent callingIntent = getIntent();
        if (callingIntent != null)
            taskTitleStr = callingIntent.getStringExtra(MainActivity.TASK_TAG);
        taskTitle = findViewById(R.id.textViewTitle);
        if ((taskTitleStr != null))
            taskTitle.setText(taskTitleStr);
        else
            taskTitle.setText("Not Specified");
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
