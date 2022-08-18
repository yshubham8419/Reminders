package com.example.reminders.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.reminders.Activities.MainActivity;
import com.example.reminders.Data.MyData;
import com.example.reminders.Data.SyncedArray;
import com.example.reminders.R;

public class MyAdapter extends ArrayAdapter<MyData> {

    private boolean makeCheckVisible;
    private final SyncedArray arr;
    private final Context context;
    private final MainActivity mainActivity;
    public MyAdapter(@NonNull Context context, int resource, @NonNull SyncedArray a) {
        super(context, resource);
        arr=a;
        this.context=context;
        mainActivity = (MainActivity) context;
        makeCheckVisible=false;
    }

    @Nullable
    @Override
    public MyData getItem(int position) {
        return arr.get(position);
    }


    @Override
    public int getCount() {
        return arr.size();
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            convertView = convertView == null ? LayoutInflater.from(context).inflate(R.layout.my_layout, parent, false) : convertView;
            TextView dateView = convertView.findViewById(R.id.date);
            TextView subjectView = convertView.findViewById(R.id.message);
            CheckBox check = convertView.findViewById(R.id.checkBox);
            MyData myData = getItem(position);

            dateView.setText(myData.getDateString());
            check.setVisibility(makeCheckVisible ? View.VISIBLE : View.GONE);
            check.setChecked(myData.isChecked());
            subjectView.setText(myData.getMessage());
            convertView.setBackgroundColor(Color.parseColor(myData.isVIP() ? "#603F51B5" : "#00000000"));
            View finalConvertView = convertView;

            mainActivity.runOnUiThread(() -> {
                finalConvertView.setOnClickListener(view -> mainActivity.onItemClickListener(position));
                finalConvertView.setOnLongClickListener(view -> {
                    mainActivity.onItemLongClickListener(position);
                    return true;
                });
                check.setOnClickListener(view -> {mainActivity.onCheckChangeListener(position,!myData.isChecked());});
            });


        return convertView;
    }

    public void setCheckVisible(boolean v){
        makeCheckVisible=v;
    }

    public void setCheckedAt(int pos,boolean v){
        arr.get(pos).setChecked(v);
    }

    public void resetAllCheck(){
        for(MyData md : arr) {
            md.setChecked(false);
        }
    }

}
