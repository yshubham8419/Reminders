package com.example.reminders.Drive;

import android.app.ProgressDialog;
import android.os.Handler;
import android.view.View;

public class Syncer {
    public void onClick(View v) {
        // creating progress bar dialog  
        ProgressDialog progressBar = new ProgressDialog(v.getContext());
        Handler progressBarHandler = new Handler();
        progressBar.setCancelable(true);
        progressBar.setMessage("File downloading ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();
        //reset progress bar and filesize status  
        int progressBarStatus = 0;
        int fileSize = 0;

        new Thread(new Runnable() {
            public void run() {
                while (progressBarStatus < 100) {
                    // performing operation  

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Updating the progress bar  

                    progressBarHandler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressBarStatus);
                        }
                    });
                }
                // performing operation if file is downloaded,  
                if (progressBarStatus >= 100) {
                    // sleeping for 1 second after operation completed  
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // close the progress bar dialog  
                    progressBar.dismiss();
                }
            }
        }).start();
    }
}