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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.auth.AuthUser;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
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
//        Amplify.Auth.signUp("nas.qavideos@gmail.com",
//                "P@ssw0rd123",
//                AuthSignUpOptions.builder().userAttribute(AuthUserAttributeKey.email(),"nas.qavideos@gmail.com")
//                        .userAttribute(AuthUserAttributeKey.nickname(),"Naser Gh")
//                        .build(),  good ->
//                {
//                    Log.i(TAG, "Signup succeeded: "+ good.toString());
//                },
//                bad ->
//                {
//                    Log.i(TAG, "Signup failed with username: "+ "nas.qavideos@gmail.com"+ " with this message: "+ bad.toString());
//                }
//        );
//
//        // next step, we need to verify the user
//        Amplify.Auth.confirmSignUp("nas.qavideos@gmail.com",
//                "403038",
//                success ->
//                {
//                    Log.i(TAG,"verification succeeded: "+ success.toString());
//
//                },
//                failure ->
//                {
//                    Log.i(TAG,"verification failed: "+ failure.toString());
//                }
//        );
//
//        // next, we want to log in to our system
//        Amplify.Auth.signIn("nas.qavideos@gmail.com",
//                "P@ssw0rd123",
//                success ->
//                {
//                    Log.i(TAG, "Login succeeded: "+success.toString());
//                },
//                failure ->
//                {
//                    Log.i(TAG, "Login failed: "+failure.toString());
//                }
//        );
//
//        // next we want to log out from out system
//        Amplify.Auth.signOut(
//                () ->
//                {
//                    Log.i(TAG,"Logout succeeded");
//                },
//                failure ->
//                {
//                    Log.i(TAG, "Logout failed");
//                }
//        );

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
        setUpLoginAndLogoutButton();
}

    @Override
    protected void onResume() {
        super.onResume();
        TextView userTasks;
        userTasks = findViewById(R.id.userTasks);

//        String name = sp.getString(UserSettingsActivity.USERNAME_TAG, "no name");
//        userTasks.setText(name.isEmpty() ? "tasks" : name + "'s tasks ");
        AuthUser authUser = Amplify.Auth.getCurrentUser();
        String username="";
        if (authUser == null){
            Button loginButton = (Button) findViewById(R.id.mainActivityLoginButton);
            loginButton.setVisibility(View.VISIBLE);
            Button logoutButton = (Button) findViewById(R.id.mainActivityLogoutButton);
            logoutButton.setVisibility(View.INVISIBLE);
        }else{
            username = authUser.getUsername();
            Log.i(TAG, "Username is: "+ username);
            Button loginButton = (Button) findViewById(R.id.mainActivityLoginButton);
            loginButton.setVisibility(View.INVISIBLE);
            Button logoutButton = (Button) findViewById(R.id.mainActivityLogoutButton);
            logoutButton.setVisibility(View.VISIBLE);

            String username2 = username; // ugly way for lambda hack
            Amplify.Auth.fetchUserAttributes(
                    success ->
                    {
                        Log.i(TAG, "Fetch user attributes succeeded for username: "+username2);
                        for (AuthUserAttribute userAttribute: success){
                            if(userAttribute.getKey().getKeyString().equals("nickname")){
                                String usernickname = userAttribute.getValue();
                                runOnUiThread(() ->
                                {
                                    ((TextView)findViewById(R.id.userTasks)).setText(usernickname+ "'s tasks");
                                });
                            }
                        }
                    },
                    failure ->
                    {
                        Log.i(TAG, "Fetch user attributes failed: "+failure.toString());
                    }
            );
        }
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
    private void setUpLoginAndLogoutButton(){
        Button loginButton = (Button) findViewById(R.id.mainActivityLoginButton);
        loginButton.setOnClickListener(v ->
        {
            Intent goToLogInIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(goToLogInIntent);
        });

        Button logoutButton = (Button) findViewById(R.id.mainActivityLogoutButton);
        logoutButton.setOnClickListener(v->
        {
            Amplify.Auth.signOut(
                    () ->
                    {
                        Log.i(TAG,"Logout succeeded");
                        runOnUiThread(() ->
                        {
                            ((TextView)findViewById(R.id.userTasks)).setText("No Nick name");
                        });
                        Intent goToLogInIntent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(goToLogInIntent);
                    },
                    failure ->
                    {
                        Log.i(TAG, "Logout failed");
                        runOnUiThread(() ->
                        {
                            Toast.makeText(MainActivity.this, "Log out failed", Toast.LENGTH_LONG).show();
                        });
                    }
            );
        });
    }

}

