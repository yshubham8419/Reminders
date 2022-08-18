package com.example.reminders.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import com.example.reminders.Adapter.MyAdapter;
import com.example.reminders.Data.MyData;
import com.example.reminders.Data.SyncedArray;
import com.example.reminders.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private SyncedArray arr;
    private MyAdapter adapter;
    private ListView listView;
    private FloatingActionButton button;
    private int selected;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private void initValues(){
        selected = 0;
        listView = findViewById(R.id.list);
        arr = new SyncedArray(this);
        adapter = new MyAdapter(this, R.layout.my_layout, arr);
        button = findViewById(R.id.floatingActionButton3);
        sharedPreferences = getSharedPreferences("MySharedPreference", Context.MODE_PRIVATE);
        editor =  sharedPreferences.edit();
        editor.putBoolean("updated",false);
        editor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initValues();
        listView.setAdapter(adapter);
        button.setOnClickListener(view -> onFloatingButtonClicked());
    }

    public void onItemClickListener(int position){
        MyData myData = arr.get(position);
        if(selected>0){
            if(myData.isChecked()){
                myData.setChecked(false);
                selected--;
                if(selected==0){
                    adapter.setCheckVisible(false);
                    notifySelectionStopped();
                }
            }
            else{
                selected++;
                myData.setChecked(true);
            }
            adapter.notifyDataSetChanged();
        }
        else{
            startSetRemindersActivity(position);
        }
    }

    public void onItemLongClickListener(int position){
        MyData myData=arr.get(position);
        if(!myData.isChecked()) {
            adapter.setCheckVisible(true);
            adapter.setCheckedAt(position, true);
            selected++;
            if(selected == 1){
                notifySelectionStarted();
            }
            adapter.notifyDataSetChanged();
        }
    }

    public void onCheckChangeListener(int position,boolean b){
        MyData myData = arr.get(position);
        myData.setChecked(b);
        selected = selected+ (b?1:-1);
        if(selected == 0){
            adapter.setCheckVisible(false);
            notifySelectionStopped();
        }
        adapter.notifyDataSetChanged();
    }

    private void onFloatingButtonClicked() {
        if (selected == 0) {
            startSetRemindersActivity(arr.size());
        } else {
            performDeletion();
            selected = 0;
            adapter.setCheckVisible(false);
            adapter.notifyDataSetChanged();
            notifySelectionStopped();
        }
    }

    private void performDeletion() {
        int i=0;
        for(MyData myData: arr){
            if(myData.isChecked()){
                arr.remove(i);
            }
            else {
                i++;
            }
        }
    }

    private void startSetRemindersActivity(int pos) {
        Intent intent = new Intent(getBaseContext(), SetReminder.class);
        intent.putExtra("pos",pos);
        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        if (sharedPreferences.getBoolean("updated",false)) {
            adapter.notifyDataSetChanged();
            listView.smoothScrollToPosition(arr.size() - 1);
            editor.putBoolean("updated",false);
            editor.apply();
        }
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        if (selected > 0) {
            selected = 0;
            adapter.resetAllCheck();
            adapter.setCheckVisible(false);
            adapter.notifyDataSetChanged();
            notifySelectionStopped();
        } else {
            arr.close();
            super.onBackPressed();
        }
    }

    public void notifySelectionStopped() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Resources resources = getResources();
            Drawable drawable = ResourcesCompat.getDrawable(resources, R.drawable.abcd, null);
            button.setForeground(drawable);
        }
    }

    public void notifySelectionStarted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Resources resources = getResources();
            Drawable drawable = ResourcesCompat.getDrawable(resources, R.drawable.delete_icon, null);
            button.setForeground(drawable);
        }
    }
}

