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

public class AddNewTaskActivity extends AppCompatActivity {
    public static final String TAG = "AddNewTaskActivity";
    Spinner teamSpinner = null;
    Spinner taskStateSpinner = null;
  private   CompletableFuture<List<Team>> teamFuture = new CompletableFuture<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_task);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        teamFuture =new CompletableFuture<>();



//        Spinner taskStateSpinner = (Spinner) findViewById(R.id.addTaskStateSpinner);
//        taskStateSpinner.setAdapter(new ArrayAdapter<>(
//                this,
//                android.R.layout.simple_spinner_item,
//                TaskStateEnum.values()));


        setUpSpinners();
        setUpAddTaskButton();


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

    private void setUpSpinners() {
         taskStateSpinner = (Spinner) findViewById(R.id.addTaskStateSpinner);
           taskStateSpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                TaskStateEnum.values()));

        teamSpinner = (Spinner) findViewById(R.id.addTaskTeamSpinner);
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
            teamSpinner.setAdapter(new ArrayAdapter<>(
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

private void setUpAddTaskButton(){
    Toast taskAddedToast = Toast.makeText(this, "Task Added To DynamoDB", Toast.LENGTH_SHORT);

    Button addtaskbutton = (Button) findViewById(R.id.addTaskButton);
    addtaskbutton.setOnClickListener(view -> {

        String title = ((EditText) findViewById(R.id.taskNameEditText)).getText().toString();
        String description = ((EditText) findViewById(R.id.taskDescriptionTextView)).getText().toString();
        String currentDateSting = com.amazonaws.util.DateUtils.formatISO8601Date(new Date());
        String selectedTeamString=teamSpinner.getSelectedItem().toString();
        List<Team>teams=null;
        try {
            teams=teamFuture.get();
        }catch (InterruptedException ie){
            Log.e(TAG, " InterruptedException while getting teams");
        }catch (ExecutionException ee){
            Log.e(TAG," ExecutionException while getting teams");
        }
        Team selectedTeam =teams.stream().filter(t->t.getName().equals(selectedTeamString)).findAny().orElseThrow(RuntimeException::new);
        Task newTask = Task.builder()
                .title(title)
                .body(description)
                .dateCreated(new Temporal.DateTime(new Date(), 0))
                .state((TaskStateEnum) taskStateSpinner.getSelectedItem())
                .taskOwnedByTeam(selectedTeam)
                .build();

        Amplify.API.mutate(
                ModelMutation.create(newTask),
                successResponse -> {
                    Log.i(TAG, "AddNewTaskActivity.onCreate(): made a Task successfully");
                    taskAddedToast.show();
                },//success response
                failureResponse -> Log.e(TAG, "AddNewTaskActivity.onCreate(): failed with this response" + failureResponse)// in case we have a failed response
        );

    });

}











}