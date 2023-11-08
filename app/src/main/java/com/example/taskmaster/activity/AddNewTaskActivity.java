package com.example.taskmaster.activity;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.taskmaster.R;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.model.TaskStateEnum;

public class AddNewTaskActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_task);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Toast taskAddedToast =Toast.makeText(this,"Task Added",Toast.LENGTH_SHORT);




      //  taskMasterDatabase.taskDao().findAll();
        Spinner taskStateSpinner=(Spinner) findViewById(R.id.addTaskStateSpinner);
        taskStateSpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                TaskStateEnum.values()));

        Button addtaskbutton = (Button) findViewById(R.id.addTaskButton);
        addtaskbutton.setOnClickListener(view ->{
            Task newtask =new Task(
                    (  (EditText)  findViewById(R.id.taskNameEditText)).getText().toString(),
                    (  (EditText)  findViewById(R.id.taskDescriptionTextView)).getText().toString(),
                    TaskStateEnum.formString(taskStateSpinner.getSelectedItem().toString())   );
            // TODo: Change to graph ql
        //    taskMasterDatabase.taskDao().insertATask(newtask);


            taskAddedToast.show();
                });



    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }











}