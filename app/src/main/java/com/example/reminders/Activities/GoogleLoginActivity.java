package com.example.reminders.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.reminders.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

public class GoogleLoginActivity extends AppCompatActivity {
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient mGoogleSignInClient;
    ActivityResultLauncher<Intent> somerl = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Task<GoogleSignInAccount> task =GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        task.getResult(ApiException.class);
                        SharedPreferences.Editor editor =
                                getSharedPreferences("MySharedPreference", Context.MODE_PRIVATE).edit();
                        editor.putBoolean("account_updated",true);
                        editor.apply();
                        finish();

                    } catch (ApiException e) {
                        e.printStackTrace();
                        Toast.makeText(GoogleLoginActivity.this, "didn't work", Toast.LENGTH_SHORT).show();
                    }

                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);
        SignInButton signInButton = findViewById(R.id.signInButton);
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))
                .requestProfile()
                .requestId()
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent);
    }

    private void startActivityForResult(Intent signInIntent) {
        somerl.launch(signInIntent);
    }
}