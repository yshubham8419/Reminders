package com.example.reminders.Drive;

import android.app.Activity;

import com.example.reminders.Data.MyData;
import com.example.reminders.Data.SyncedArray;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.util.Collections;

public class Syncer {
    Drive drive;
    Activity activity;

    public Syncer(Activity activity){
        GoogleAccountCredential credential=
                GoogleAccountCredential
                        .usingOAuth2(activity, Collections.singleton(DriveScopes.DRIVE_FILE));
        credential.setSelectedAccount(GoogleSignIn.getLastSignedInAccount(activity).getAccount());
        Drive drive = new Drive.Builder(new NetHttpTransport(),new GsonFactory(),credential)
                .setApplicationName("Reminders")
                .build();
        this.activity=activity;
        this.drive = drive;
    }

    public void uploadDatabase(){
        File fileMetadata = new File();
        fileMetadata.setName("MyDatabase.db");
        fileMetadata.setParents(Collections.singletonList("appDataFolder"));
        String dbpath=activity.getDatabasePath("MyDatabase.db").toString();
        java.io.File filePath = new java.io.File(dbpath);
        FileContent mediaContent = new FileContent("application/db", filePath);
        Thread thread= new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileList files = drive.files().list()
                            .setSpaces("appDataFolder")
                            .setFields("nextPageToken, files(id, name)")
                            .setPageSize(10)
                            .execute();
                    for (File file : files.getFiles()) {
                        drive.files().delete(file.getId()).execute();
                    }
                    File file = drive.files().create(fileMetadata, mediaContent)
                            .setFields("id")
                            .execute();
                }catch(UserRecoverableAuthIOException e){
                    activity.startActivity(e.getIntent());
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
    private boolean downloadDatabase(){

        return false;
    }
    public void restoreDatabase(SyncedArray arr){
        for(MyData myData: arr){
            arr.remove(0);
        }

    }
}