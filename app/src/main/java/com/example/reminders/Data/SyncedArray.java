package com.example.reminders.Data;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.example.reminders.Recievers.AlarmReciever;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class serve as a Data structure
 */
public class SyncedArray implements Iterable<MyData>{

    private final MyDatabaseHelper myDatabaseHelper;
    private final Context context;
    private final AlarmManager alarmManager;
    private final ArrayList<MyData> arr;

    @SuppressLint("StaticFieldLeak")
    private static SyncedArray instance = null;

    public SyncedArray(Context context){
        this.context = context;
        myDatabaseHelper = new MyDatabaseHelper(context);
        arr=myDatabaseHelper.getAll();
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        instance=this;
    }

    public static SyncedArray getCurrentInstance(){
        return instance;
    }

    public void close() {
        myDatabaseHelper.close();
    }

    public void add(MyData o) {
        myDatabaseHelper.add(o);
        if(o.isVIP) {
            setAlarm(o);
        }
        arr.add(o);
    }

    public void addAll(SyncedArray c) {
        for(MyData item : c){
            add(item);
        }
    }

    public void remove(int index) {
        MyData myData = arr.get(index);
        myDatabaseHelper.delete(myData);
        if(myData.isVIP){
            cancelAlarm(myData);
        }
        arr.remove(index);
    }

    public void clear() {
        myDatabaseHelper.deleteAll();
        for(MyData myData : this){
            if(myData.isVIP){
                cancelAlarm(myData);
            }
        }
        arr.clear();
    }

    private void setAlarm(MyData myData) {
        Intent intent = new Intent(context, AlarmReciever.class);
        intent.putExtra("subject", myData.getMessage());
        int requestCode = myData.getRequestCode();
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, myData.calendar.getTimeInMillis(), alarmIntent);
    }

    private void cancelAlarm(MyData myData) {
        Intent intent = new Intent(context, AlarmReciever.class);
        intent.putExtra("subject", myData.getMessage());
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, myData.getRequestCode(), intent, 0);
        alarmManager.cancel(alarmIntent);
    }

    @NonNull
    @Override
    public Iterator<MyData> iterator() {
        return new MyIterator();
    }

    public MyData get(int position) {
        return arr.get(position);
    }

    public int size() {
        return arr.size();
    }

    public void update(int pos, MyData tempData) {
        MyData myData =arr.get(pos);

        if(myData.isVIP())
            cancelAlarm(myData);

        myData.setDateString(tempData.getDateString());
        myData.setChecked(tempData.isChecked());
        myData.setVIP(tempData.isVIP());
        myData.setMessage(tempData.getMessage());
        myData.setCalendar(tempData.getCalendar());

        if(myData.isVIP())
            setAlarm(myData);
        myDatabaseHelper.update(myData);
    }

    class MyIterator implements Iterator<MyData> {
        int i=0;
        @Override
        public boolean hasNext() {
            return i<arr.size();
        }

        @Override
        public MyData next() {
            i++;
            return arr.get(i-1);
        }
    }
}
