package com.example.reminders;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static boolean updated = false;
    static ArrayList<MyData> arr;
    private final String url = Environment.getExternalStorageDirectory() + "/reminders.dat";
    private final MyObjectReaderWriter<MyData> readerWriter = new MyObjectReaderWriter<>(url, new ArrayList<>());
    int nselected = 0;
    private MyAdapter adapter;
    private ListView listView;
    private FloatingActionButton button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.list);
        readerWriter.readObjects();
        arr = readerWriter.getArray();
        adapter = new MyAdapter(this, R.layout.my_layout, arr);
        listView.setAdapter(adapter);                                                
        button = findViewById(R.id.floatingActionButton3);
        setClickListeners();
        Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();
    }


    private void setClickListeners() {
        button.setOnClickListener(view -> {

            if (nselected == 0) {
                Intent intent = new Intent(getBaseContext(), SetReminder.class);
                intent.putExtra("pos",arr.size());
                startActivity(intent);
            } else {
                ArrayList<MyData> newList = new ArrayList<>(arr);
                arr.clear();
                for (MyData md : newList) {
                    if (!md.checked) {
                        arr.add(md);
                    }
                    else{
                       cancelAlarm(md);
                    }
                }
                adapter.setCheckVisible(false);
                adapter.nselected = 0;
                nselected = 0;
                adapter.notifyDataSetChanged();
            }
        });

    }


    @Override
    protected void onDestroy() {
        readerWriter.writeObjects();
        super.onDestroy();
    }


    @Override
    protected void onRestart() {
        if (updated) {
            adapter.notifyDataSetChanged();
            listView.smoothScrollToPosition(arr.size() - 1);
            Toast.makeText(this, ""+arr.size(), Toast.LENGTH_SHORT).show();
            updated = false;
        }
        super.onRestart();
    }


    @Override
    public void onBackPressed() {
        if (nselected > 0) {
            adapter.setCheckVisible(false);
            adapter.resetAllCheck();
            adapter.nselected = 0;
            nselected = 0;
            adapter.notifyDataSetChanged();
        } else {
            super.onBackPressed();
        }
    }


    void selectionStopped() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Resources resources = getResources();
            Drawable drawable = ResourcesCompat.getDrawable(resources, R.drawable.abcd, null);
            button.setForeground(drawable);
        }
    }

    void selectionStarted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Resources resources = getResources();
            Drawable drawable = ResourcesCompat.getDrawable(resources, R.drawable.delete_icon, null);
            button.setForeground(drawable);
        }
    }
    private void cancelAlarm(MyData myOldData) {
        Intent intent = new Intent(this, AlarmReciever.class);
        intent.putExtra("subject",myOldData.message);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, myOldData.requestCode, intent, 0);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(alarmIntent);
    }

}