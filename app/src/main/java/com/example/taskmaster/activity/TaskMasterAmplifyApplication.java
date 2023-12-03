package com.example.taskmaster.activity;

import android.app.Application;
import android.util.Log;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;

public class TaskMasterAmplifyApplication extends Application {
    public static final String TAG="Task Master Amplify Application";

    @Override
    public void onCreate() {


        super.onCreate();
        try {
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.configure(getApplicationContext());
            Log.e(TAG, " initializing Amplify pass" );
        }catch (AmplifyException ae){
            Log.e(TAG, "Error initializing Amplify" + ae.getMessage(), ae);
        }
    }
}
