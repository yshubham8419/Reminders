package com.example.reminders;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.Objects;

public class AlarmActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_layout);
        TextView tv =findViewById(R.id.alarm_subject);
        Intent intent=getIntent();
        String subject=(String) intent.getStringExtra("subject");
        tv.setText(Objects.equals(subject, "") ?"reminder...":subject);
        final MediaPlayer mediaPlayer=new MediaPlayer();
        try {
            mediaPlayer.setDataSource(Environment.getExternalStorageDirectory().getPath()+"/alarm.m4a");
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            Toast.makeText(this, ""+ e, Toast.LENGTH_SHORT).show();
        }
        Button stop=findViewById(R.id.alarm_stop_button);
        stop.setOnClickListener(v -> {
            mediaPlayer.stop();
            finish();
        });

    }
}
