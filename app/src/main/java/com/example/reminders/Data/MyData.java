package com.example.reminders.Data;

import java.util.Calendar;

public class MyData{

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isVIP() {
        return isVIP;
    }

    public void setVIP(boolean VIP) {
        isVIP = VIP;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    Calendar calendar;
    String message;
    boolean isVIP;
    int requestCode;
    boolean checked;
    String dateString;

    public MyData() {
        dateString="";
        calendar=Calendar.getInstance();
        message = "";
        isVIP = false;
        checked = false;
        requestCode = 0 ;
    }
}
