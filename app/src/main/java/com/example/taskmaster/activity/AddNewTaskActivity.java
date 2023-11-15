package com.example.taskmaster.activity;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.model.temporal.Temporal;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.TaskStateEnum;
import com.example.taskmaster.R;



import java.util.Date;

public class AddNewTaskActivity extends AppCompatActivity {
    public static final String TAG="AddNewTaskActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_task);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Toast taskAddedToast =Toast.makeText(this,"Task Added To DynamoDB",Toast.LENGTH_SHORT);


        Spinner taskStateSpinner=(Spinner) findViewById(R.id.addTaskStateSpinner);
        taskStateSpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                TaskStateEnum.values()));

        Button addtaskbutton = (Button) findViewById(R.id.addTaskButton);
        addtaskbutton.setOnClickListener(view ->{

            String title= ((EditText)  findViewById(R.id.taskNameEditText)).getText().toString();
            String description = (  (EditText)  findViewById(R.id.taskDescriptionTextView)).getText().toString();
            String currentDateSting =com.amazonaws.util.DateUtils.formatISO8601Date(new Date());
            Task newTask =Task.builder()
                    .title(title)
                    .body(description)
                    .dateCreated(new Temporal.DateTime(new Date(), 0))
                    .state((TaskStateEnum) taskStateSpinner.getSelectedItem())
                    .build();

            Amplify.API.mutate(
                    ModelMutation.create(newTask),
                    successResponse ->{ Log.i(TAG, "AddNewTaskActivity.onCreate(): made a Task successfully");
                     taskAddedToast.show();},//success response
                            failureResponse -> Log.e(TAG, "AddNewTaskActivity.onCreate(): failed with this response" + failureResponse)// in case we have a failed response
            );

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