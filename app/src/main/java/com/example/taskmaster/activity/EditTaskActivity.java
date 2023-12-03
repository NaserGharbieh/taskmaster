package com.example.taskmaster.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.TaskStateEnum;
import com.amplifyframework.datastore.generated.model.Team;
import com.example.taskmaster.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
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
    ActivityResultLauncher<Intent> activityResultLauncher;
    private String s3ImageKey="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        taskCompletableFuture=new CompletableFuture<>();
        teamFuture =new CompletableFuture<>();

        activityResultLauncher = getImagePickingActivityResultLauncher();  // You MUST set this up in onCreate() in the lifecycle
        setUpEditableUIElement();
        setUpUpdatebutton();
        setUpDeleteButton();
        setUpAddImageButton();
        setUpDeleteImageButton();
        updateImageButtons();



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
        s3ImageKey = taskToEdit.getTaskImageS3Key();
        if (s3ImageKey != null && !s3ImageKey.isEmpty())
        {
            Amplify.Storage.downloadFile(
                    s3ImageKey,
                    new File(getApplication().getFilesDir(), s3ImageKey),
                    success ->
                    {
                        ImageView taskImageView = findViewById(R.id.editTaskImageImageView);
                        taskImageView.setImageBitmap(BitmapFactory.decodeFile(success.getFile().getPath()));
                    },
                    failure ->
                    {
                        Log.e(TAG, "Unable to get image from S3 for the Task for S3 key: " + s3ImageKey + " for reason: " + failure.getMessage());
                    }
            );
        }
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
        updateTaskButton.setOnClickListener(view -> {
            saveTask(s3ImageKey);

        });

    }

    private void saveTask(String imageS3Key) {
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
                .taskImageS3Key(imageS3Key)
                .build();

        Amplify.API.mutate(
                ModelMutation.update(taskToSave),
                successResponse -> {
                    Log.i(TAG, "EditTaskActivity.onCreate(): edited a Task successfully");
                    runOnUiThread(() ->
                    {
                        Toast.makeText(this, "Task Updated", Toast.LENGTH_SHORT).show();
                    });

                },//success response
                failureResponse -> Log.e(TAG, "EditTaskActivity.onCreate(): failed with this response" + failureResponse)// in case we have a failed response
        );

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



    private void setUpAddImageButton()
    {
        ImageButton addImageButton = (ImageButton) findViewById(R.id.editTasksImageImageButton);
        addImageButton.setOnClickListener(b ->
        {
            launchImageSelectionIntent();
        });

    }
    private void launchImageSelectionIntent()
    {
        // Part 1: Launch activity to pick file

        Intent imageFilePickingIntent = new Intent(Intent.ACTION_GET_CONTENT);
        imageFilePickingIntent.setType("*/*");
        imageFilePickingIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/jpeg", "image/png"});
        // Below is simple version for testing
        //startActivity(imageFilePickingIntent);

        // Part 2: Create an image picking activity result launcher
        activityResultLauncher.launch(imageFilePickingIntent);

    }

    private ActivityResultLauncher<Intent> getImagePickingActivityResultLauncher()
    {
        // Part 2: Create an image picking activity result launcher
        ActivityResultLauncher<Intent> imagePickingActivityResultLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>()
                        {
                            @Override
                            public void onActivityResult(ActivityResult result)
                            {
                                ImageButton addImageButton =  findViewById(R.id.editTasksImageImageButton);
                                if (result.getResultCode() == Activity.RESULT_OK)
                                {
                                    if (result.getData() != null)
                                    {
                                        Uri pickedImageFileUri = result.getData().getData();
                                        try
                                        {
                                            InputStream pickedImageInputStream = getContentResolver().openInputStream(pickedImageFileUri);
                                            String pickedImageFilename = getFileNameFromUri(pickedImageFileUri);
                                            Log.i(TAG, "Succeeded in getting input stream from file on phone! Filename is: " + pickedImageFilename);
                                            // Part 3: Use our InputStream to upload file to S3
                                            switchFromAddButtonToDeleteButton(addImageButton);
                                            uploadInputStreamToS3(pickedImageInputStream, pickedImageFilename,pickedImageFileUri);

                                        } catch (FileNotFoundException fnfe)
                                        {
                                            Log.e(TAG, "Could not get file from file picker! " + fnfe.getMessage(), fnfe);
                                        }
                                    }
                                }
                                else
                                {
                                    Log.e(TAG, "Activity result error in ActivityResultLauncher.onActivityResult");
                                }
                            }
                        }
                );

        return imagePickingActivityResultLauncher;
    }

    private void uploadInputStreamToS3(InputStream pickedImageInputStream, String pickedImageFilename, Uri pickedImageFileUri)
    {
        Amplify.Storage.uploadInputStream(
                pickedImageFilename,  // S3 key
                pickedImageInputStream,
                success ->
                {
                    Log.i(TAG, "Succeeded in getting file uploaded to S3! Key is: " + success.getKey());
                    // Part 4: Update/save our Task object to have an image key
                   saveTask(success.getKey());
                  //  updateImageButtons();
                   ImageView taskImageView = findViewById(R.id.editTaskImageImageView);
                    InputStream pickedImageInputStreamCopy = null;  // need to make a copy because InputStreams cannot be reused!
                    try
                    {
                        pickedImageInputStreamCopy = getContentResolver().openInputStream(pickedImageFileUri);
                    }
                    catch (FileNotFoundException fnfe)
                    {
                        Log.e(TAG, "Could not get file stream from URI! " + fnfe.getMessage(), fnfe);
                    }
                    taskImageView.setImageBitmap(BitmapFactory.decodeStream(pickedImageInputStreamCopy));

                },
                failure ->
                {
                    Log.e(TAG, "Failure in uploading file to S3 with filename: " + pickedImageFilename + " with error: " + failure.getMessage());
                }
        );
    }
    private void setUpDeleteImageButton()
    {
        ImageButton deleteImageButton = findViewById(R.id.editTaskDeleteImageImageButton);
        String s3ImageKey = this.s3ImageKey;
        deleteImageButton.setOnClickListener(v ->
        {
            Amplify.Storage.remove(
                    s3ImageKey,
                    success ->
                    {
                        Log.i(TAG, "Succeeded in deleting file on S3! Key is: " + success.getKey());

                    },
                    failure ->
                    {
                        Log.e(TAG, "Failure in deleting file on S3 with key: " + s3ImageKey + " with error: " + failure.getMessage());
                    }
            );
            ImageView TaskImageView = findViewById(R.id.editTaskImageImageView);
            TaskImageView.setImageResource(android.R.color.transparent);

            saveTask("");
            switchFromDeleteButtonToAddButton(deleteImageButton);
        });
    }


    private void updateImageButtons() {
        ImageButton addImageButton = findViewById(R.id.editTasksImageImageButton);
        TextView addImageTextView =findViewById(R.id.addTaskTeamTextView2);
        TextView deleteImageTextView =findViewById(R.id.addTaskTeamTextView4);
        ImageButton deleteImageButton = findViewById(R.id.editTaskDeleteImageImageButton);
        runOnUiThread(() -> {
            if (this.s3ImageKey==""||this.s3ImageKey==null||this.s3ImageKey.isEmpty()) {
                deleteImageButton.setVisibility(View.INVISIBLE);
                deleteImageTextView.setVisibility(View.INVISIBLE);
                addImageButton.setVisibility(View.VISIBLE);
                addImageTextView.setVisibility(View.VISIBLE);
            } else {
                deleteImageButton.setVisibility(View.VISIBLE);
                deleteImageTextView.setVisibility(View.VISIBLE);
                addImageButton.setVisibility(View.INVISIBLE);
                addImageTextView.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void switchFromAddButtonToDeleteButton(ImageButton addImageButton) {
       ImageButton deleteImageButton = findViewById(R.id.editTaskDeleteImageImageButton);
        TextView addImageTextView =findViewById(R.id.addTaskTeamTextView2);
        TextView deleteImageTextView =findViewById(R.id.addTaskTeamTextView4);

        deleteImageButton.setVisibility(View.VISIBLE);
        deleteImageTextView.setVisibility(View.VISIBLE);
        addImageButton.setVisibility(View.INVISIBLE);
        addImageTextView.setVisibility(View.INVISIBLE);
    }
    private void switchFromDeleteButtonToAddButton(ImageButton deleteImageButton) {
        ImageButton addImageButton = findViewById(R.id.editTasksImageImageButton);
        TextView addImageTextView =findViewById(R.id.addTaskTeamTextView2);
        TextView deleteImageTextView =findViewById(R.id.addTaskTeamTextView4);
        deleteImageButton.setVisibility(View.INVISIBLE);
        deleteImageTextView.setVisibility(View.INVISIBLE);
        addImageButton.setVisibility(View.VISIBLE);
        addImageTextView.setVisibility(View.VISIBLE);
    }
    // Taken from https://stackoverflow.com/a/25005243/16889809
    @SuppressLint("Range")
    public String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }






}


