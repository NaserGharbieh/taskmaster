package com.example.taskmaster.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.model.temporal.Temporal;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.TaskStateEnum;
import com.amplifyframework.datastore.generated.model.Team;
import com.example.taskmaster.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class EditTaskActivity extends AppCompatActivity {
    public static final String TAG= "editTaskActivity";
    private Spinner editTeamSpinner = null;
    private Spinner editTaskStateSpinner = null;
    private CompletableFuture<List<Team>> teamFuture = null;
    private CompletableFuture<Task> taskCompletableFuture = null;
    private Task taskToEdit=null;
    private EditText nameEditText;
    private EditText descriptionEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        taskCompletableFuture=new CompletableFuture<>();
        teamFuture =new CompletableFuture<>();
        setUpEditableUIElement();
        setUpUpdatebutton();
        setUpDeleteButton();



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

    private void setUpEditableUIElement() {
        Intent callingIntent = getIntent();
        String taskId = null;

        if(callingIntent != null){
            taskId = callingIntent.getStringExtra(MainActivity.TASK_ID_TAG);
        }

        String taskId2 = taskId; //ugly hack just to fix lambda processing

        Amplify.API.query(
                ModelQuery.list(Task.class),
                success ->
                {
                    Log.i(TAG,"Read Tasks Successfully");

                    for (Task databaseTask: success.getData()){
                        if(databaseTask.getId().equals(taskId2)){
                            taskCompletableFuture.complete(databaseTask);
                        }
                    }

                    runOnUiThread(() ->
                    {
                        //Update UI element
                    });
                },
                failure -> Log.i(TAG, "Read Tasks failed")
        );

        try {
            taskToEdit = taskCompletableFuture.get();
        }catch (InterruptedException ie){
            Log.e(TAG, "InterruptedException while getting task");
            Thread.currentThread().interrupt();
        }catch (ExecutionException ee){
            Log.e(TAG, "ExecutionException while getting task");
        }

        nameEditText = ((EditText) findViewById(R.id.editTaskNameEditText));
        nameEditText.setText(taskToEdit.getTitle());
        descriptionEditText = ((EditText) findViewById(R.id.editTaskDescriptionTextView));
        descriptionEditText.setText(taskToEdit.getBody());
        setUpSpinners();
    }
    private void setUpSpinners() {
       editTaskStateSpinner= (Spinner) findViewById(R.id.editTaskStateSpinner);
        editTaskStateSpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                TaskStateEnum.values()));
        editTaskStateSpinner.setSelection(getSpinnerIndex(editTaskStateSpinner,taskToEdit.getState().toString()));

        editTeamSpinner = (Spinner) findViewById(R.id.editTaskTeamSpinner);
        Amplify.API.query(
                ModelQuery.list(Team .class),
                success ->

                {
                    Log.i(TAG, "Read Teams Successfully");
                    ArrayList<String> TeamNames = new ArrayList<>();
                    ArrayList<Team> teams = new ArrayList<>();
                    for (Team team : success.getData()) {
                        teams.add(team);
                        TeamNames.add(team.getName());
                    }
                    teamFuture.complete(teams);

                    runOnUiThread(() ->
                    {
                        editTeamSpinner.setAdapter(new ArrayAdapter<>(
                                this,
                                (android.R.layout.simple_spinner_item),
                                TeamNames
                        ));
                    });
                },
                failure->

                {
                    teamFuture.complete(null);
                    Log.i(TAG, "read teams failed");
                }
        );
    }

    private int getSpinnerIndex(Spinner spinner, String stringValueToCheck){
        for (int i = 0;i < spinner.getCount(); i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(stringValueToCheck)){
                return i;
            }
        }

        return 0;
    }

    private void setUpUpdatebutton(){
        Button updateTaskButton=((Button) findViewById(R.id.updateTaskButton));
        Toast taskEditedToast = Toast.makeText(this, "Task Updated", Toast.LENGTH_SHORT);


        updateTaskButton.setOnClickListener(view -> {

            String title = ((EditText) findViewById(R.id.editTaskNameEditText)).getText().toString();
            String description = ((EditText) findViewById(R.id.editTaskDescriptionTextView)).getText().toString();
            String currentDateSting = com.amazonaws.util.DateUtils.formatISO8601Date(new Date());
            String selectedTeamString=editTeamSpinner.getSelectedItem().toString();
            List<Team>teams=null;
            try {
                teams=teamFuture.get();
            }catch (InterruptedException ie){
                Log.e(TAG, " InterruptedException while getting teams");
            }catch (ExecutionException ee){
                Log.e(TAG," ExecutionException while getting teams");
            }
            Team teamtosave =teams.stream().filter(t->t.getName().equals(selectedTeamString)).findAny().orElseThrow(RuntimeException::new);
            Task taskToSave = Task.builder()
                    .title(taskToEdit.getTitle().toString())
                    .id(taskToEdit.getId())
                    .body(description)
                    .dateCreated(taskToEdit.getDateCreated())
                    .taskOwnedByTeam(teamtosave)
                    .state(taskStateEnumFormString(editTaskStateSpinner.getSelectedItem().toString()))
                    .build();

            Amplify.API.mutate(
                    ModelMutation.update(taskToSave),
                    successResponse -> {
                        Log.i(TAG, "EditTaskActivity.onCreate(): edited a Task successfully");
                        taskEditedToast.show();
                    },//success response
                    failureResponse -> Log.e(TAG, "EditTaskActivity.onCreate(): failed with this response" + failureResponse)// in case we have a failed response
            );

        });

    }

    public static TaskStateEnum taskStateEnumFormString(String inputTaskStateText){
        for (TaskStateEnum taskState:TaskStateEnum.values()) {
            if(taskState.toString().equals(inputTaskStateText)){
                return taskState;
            }
        }
        return null;
    }

    private void setUpDeleteButton(){
        Button deleteButton = (Button) findViewById(R.id.deleteTaskButton);
        deleteButton.setOnClickListener(v ->{
            Amplify.API.mutate(
                    ModelMutation.delete(taskToEdit),
                    successResponse ->
                    {
                        Log.i(TAG, "EditTaskActivity.onCreate(): deleted a Task successfully");
                        Intent goToMainActivity= new Intent(EditTaskActivity.this, MainActivity.class);
                        startActivity(goToMainActivity);
                    },
                    failureResponse -> Log.i(TAG,"EditTaskActivity.onCreate(): failed with this response: "+ failureResponse)
            );
        });
    }






}


