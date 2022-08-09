package com.example.reminders;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

public class SetReminder extends AppCompatActivity {
    static EditText datepick;
    static Calendar calendar = Calendar.getInstance();
    static EditText timepick;
    Switch sw;
    ArrayList<MyData>  arr;
    MyData myOldData;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_reminder);
        Button button = findViewById(R.id.submit_button);
        datepick = findViewById(R.id.datepick);
        timepick = findViewById(R.id.timepick);
        ImageButton dateset = findViewById(R.id.date_button);
        ImageButton timeset = findViewById(R.id.time_button);
        EditText subject = findViewById(R.id.subject);
        calendar = Calendar.getInstance();
        sw = findViewById(R.id.switch1);
        Intent intent=getIntent();
        arr=MainActivity.arr;
        int pos=(int)intent.getIntExtra("pos",arr.size());
        Toast.makeText(this, ""+arr.size(), Toast.LENGTH_SHORT).show();
        myOldData=pos<arr.size()?arr.get(pos):new MyData();
        try {
            if (pos < arr.size()) {
                MyData md = arr.get(pos);
                Scanner sn = new Scanner(md.date);
                datepick.setText(sn.nextLine());
                timepick.setText(sn.nextLine());
                sn.close();
                sw.setChecked(md.isVIP);
                subject.setText(md.message);
            }

        timeset.setOnClickListener(view -> {
            DialogFragment newFragment = new TimePickerFragment();
            newFragment.show(getSupportFragmentManager(), "timePicker");
        });

        dateset.setOnClickListener(view -> {
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(getSupportFragmentManager(), "datePicker");
        });
        button.setOnClickListener(view -> {
            if(pos==arr.size())
                arr.add(new MyData());
            MainActivity.updated = true;
            MyData data= arr.get(pos);
            data.isVIP = sw.isChecked();
            data.date = datepick.getText() + "\n" + timepick.getText();
            data.message = subject.getText().toString();
            if(data.isVIP){
                cancelAlarm();
                setAlarm(pos);
            }
            else{
                cancelAlarm();
            }
            SetReminder.super.onBackPressed();
        });
        }catch(Exception ex){
            Toast.makeText(this, ""+ ex, Toast.LENGTH_SHORT).show();
        }
    }

    private void cancelAlarm() {
        Intent intent = new Intent(this, AlarmReciever.class);
        intent.putExtra("subject",myOldData.message);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, myOldData.requestCode, intent, 0);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(alarmIntent);
    }

    private void setAlarm(int pos) {
        Intent intent = new Intent(this, AlarmReciever.class);
        intent.putExtra("subject",arr.get(pos).message);
        int requestCode=getRequestCode();
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, requestCode, intent, 0);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        arr.get(pos).requestCode=requestCode;
    }

    private int getRequestCode() {
        int code=0;
        int[] feilds = {Calendar.MINUTE,
                Calendar.HOUR_OF_DAY,
                Calendar.DAY_OF_MONTH,
                Calendar.MONTH,
                Calendar.YEAR
        };
        int i=0;
        for(int f:feilds){
            int[] x=getIntwoDigits(calendar.get(f));
            code=code*100+x[0]*10+x[1];
        }
        return code;
    }
    private int[] getIntwoDigits(int x){
        int[] val=new int[2];
        val[0]=x/10;
        val[1]=x%10;
        return val;
    }

    /**
     * TimePicker class
     */
    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            final Calendar calender = Calendar.getInstance();
            int hour = calender.get(Calendar.HOUR_OF_DAY);
            int minute = calender.get(Calendar.MINUTE);
            return new TimePickerDialog(getActivity(), this, hour, minute, false);

        }

        @Override
        public void onTimeSet(android.widget.TimePicker timePicker, int i, int i1) {
            requireActivity().runOnUiThread(() -> {
                String t = getIn12(i, i1);
                timepick.setText(t);
                calendar.set(Calendar.HOUR_OF_DAY, i);
                calendar.set(Calendar.MINUTE, i1);
            });
        }

        private String getIn12(int i, int i1) {
            if (i < 12) {
                return i + ":" + (i1 < 10 ? "0" : "") + i1 + " am";
            } else if (i == 12) {
                return i + ":" + (i1 < 10 ? "0" : "") + i1 + " pm";
            }
            return (i - 12) + ":" + (i1 < 10 ? "0" : "") + i1 + " pm";
        }
    }

    /**
     * datepicker class
     */
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(requireContext(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            requireActivity().runOnUiThread(() -> {
                String d = day + " " + getMonth(month);
                datepick.setText(d);
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
            });
        }

        private String getMonth(int month) {
            month++;
            switch (month) {
                case 1:
                    return "Jan";
                case 2:
                    return "Feb";
                case 3:
                    return "Mar";
                case 4:
                    return "Apr";
                case 5:
                    return "May";
                case 6:
                    return "Jun";
                case 7:
                    return "Jul";
                case 8:
                    return "Aug";
                case 9:
                    return "Sep";
                case 10:
                    return "Oct";
                case 11:
                    return "Nov";
            }
            return "Dec";
        }

    }


}
