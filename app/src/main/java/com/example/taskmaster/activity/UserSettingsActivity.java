package com.example.taskmaster.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taskmaster.R;

public class UserSettingsActivity extends AppCompatActivity {
    public static final String USERNAME_TAG="username";
    Button saveButton;
    EditText username;
    String usernameStr;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        saveButton= findViewById(R.id.addUsernameButton);
        username= findViewById(R.id.usernameEditText);

        sp= PreferenceManager.getDefaultSharedPreferences(this);
        saveButton.setOnClickListener(view -> {
            usernameStr=username.getText().toString();
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(USERNAME_TAG,usernameStr);
            editor.apply();
          Toast.makeText(UserSettingsActivity.this,"Username Saved",Toast.LENGTH_LONG).show();


        });




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



