package com.example.reminders.Activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.reminders.Data.MyData;
import com.example.reminders.Data.SyncedArray;
import com.example.reminders.R;

import java.util.Calendar;
import java.util.Scanner;

public class SetReminder extends AppCompatActivity {

    private EditText dateEdit;
    private EditText timeEdit;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch sw;
    private Button submitButton;
    private ImageButton datePicker;
    private SyncedArray arr;
    private ImageButton timePicker;
    private EditText subjectEdit;
    private int pos;
    private Mode mode;
    private MyData tempData;
    private TimePickerDialog timePickerDialog;
    private DatePickerDialog datePickerDialog;
    private enum Mode{ UPDATING, ADDITION }

    private void initValues(){
        arr = getSyncedArray();
        sw = findViewById(R.id.switch1);
        dateEdit = findViewById(R.id.datepick);
        timeEdit = findViewById(R.id.timepick);
        submitButton = findViewById(R.id.submit_button);
        datePicker = findViewById(R.id.date_button);
        timePicker = findViewById(R.id.time_button);
        subjectEdit = findViewById(R.id.subject);
        pos=(int)getIntent().getIntExtra("pos",arr.size());
        mode=pos < arr.size()?Mode.UPDATING : Mode.ADDITION;
        tempData=new MyData();
        timePickerDialog = createTimePickerDialog();
        datePickerDialog = createDatePickerDialog();
    }

    private void initUI() {
        if (mode==Mode.UPDATING) {
            MyData md = arr.get(pos);
            Scanner sn = new Scanner(md.getDateString());
            dateEdit.setText(sn.nextLine());
            timeEdit.setText(sn.nextLine());
            sw.setChecked(md.isVIP());
            subjectEdit.setText(md.getMessage());
            sn.close();
        }
    }

    private void onSubmitButtonClicked(){
        tempData.setVIP(sw.isChecked());
        tempData.setDateString(dateEdit.getText() + "\n" + timeEdit.getText());
        tempData.setMessage(subjectEdit.getText().toString());
        if (mode == Mode.ADDITION)
            arr.add(tempData);
        else
            arr.update(pos, tempData);
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =  sharedPreferences.edit();
        editor.putBoolean("updated",true);
        editor.apply();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_reminder);
        initValues();
        initUI();
        timePicker.setOnClickListener(view -> timePickerDialog.show());
        timeEdit.setOnClickListener(view -> timePicker.performClick());
        datePicker.setOnClickListener(view -> datePickerDialog.show());
        dateEdit.setOnClickListener(view -> datePicker.performClick());
        submitButton.setOnClickListener(view -> onSubmitButtonClicked() );
    }

    private TimePickerDialog createTimePickerDialog() {
        Calendar calender =Calendar.getInstance();
        return new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            String t = getIn12(hourOfDay,minute);
            timeEdit.setText(t);
            tempData.getCalendar().set(Calendar.HOUR_OF_DAY,hourOfDay);
            tempData.getCalendar().set(Calendar.MINUTE,minute);
        },calender.get(Calendar.HOUR_OF_DAY),calender.get(Calendar.MINUTE),false);
    }

    private DatePickerDialog createDatePickerDialog(){
        Calendar calender =Calendar.getInstance();
        return new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String d = dayOfMonth + " " + getMonth(month);
            dateEdit.setText(d);
            tempData.getCalendar().set(Calendar.YEAR,year);
            tempData.getCalendar().set(Calendar.MONTH,month);
            tempData.getCalendar().set(Calendar.DAY_OF_MONTH,dayOfMonth);
        },calender.get(Calendar.YEAR),calender.get(Calendar.MONTH),calender.get(Calendar.DAY_OF_MONTH));
    }

    private SyncedArray getSyncedArray() {
        SyncedArray instance;
        if (SyncedArray.getCurrentInstance() != null) {
            instance = SyncedArray.getCurrentInstance();
        } else {
            instance = new SyncedArray(this);
        }
        return instance;
    }

    private int[] getInTwoDigits(int x){
        int[] val=new int[2];
        val[0]=x/10;
        val[1]=x%10;
        return val;
    }

    private String getIn12(int i, int i1) {
        if (i < 12) {
            return i + ":" + (i1 < 10 ? "0" : "") + i1 + " am";
        } else if (i == 12) {
            return i + ":" + (i1 < 10 ? "0" : "") + i1 + " pm";
        }
        return (i - 12) + ":" + (i1 < 10 ? "0" : "") + i1 + " pm";
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
