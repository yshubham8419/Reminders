package com.example.reminders.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.reminders.R;

import java.io.IOException;
import java.time.Duration;
import java.util.Calendar;
import java.util.Objects;

public class AlarmActivity extends AppCompatActivity {
    private BroadcastReceiver mReceiver;
    final MediaPlayer mediaPlayer=new MediaPlayer();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_layout);
        TextView tv = findViewById(R.id.alarm_subject);
        Intent intent = getIntent();
        String subject = intent.getStringExtra("subject");
        tv.setText(Objects.equals(subject, "") ? "reminder..." : subject);

        try {
            mediaPlayer.setDataSource(this, Uri.parse("android.resource://com.example.reminders/raw/" + R.raw.alarm));
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        } catch (IOException e) {
            Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
        }
        Button stop = findViewById(R.id.alarm_stop_button);
        stop.setOnClickListener(v -> {
            finish();
        });
        long c1 = Calendar.getInstance().getTimeInMillis();
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long c2 = Calendar.getInstance().getTimeInMillis();
                if (c2 - c1 >= 60000) {
                    finish();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        mediaPlayer.stop();
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

    }
}
