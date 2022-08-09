package com.example.reminders;

import android.app.PendingIntent;
import android.os.Parcel;

import java.io.Serializable;
import java.util.Calendar;

public class MyData implements Serializable {

    String date;
    String message;
    boolean isVIP;
    int requestCode;
    transient boolean checked;

    public MyData() {
        date = "";
        message = "";
        isVIP = false;
        checked = false;
        requestCode=0;
    }
}
