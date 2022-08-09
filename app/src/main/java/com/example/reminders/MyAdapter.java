package com.example.reminders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MyAdapter extends ArrayAdapter<MyData> {
    private boolean makeCheckVisible=false;
    private final ArrayList<MyData> arr;
    private final Context context;
    private final MainActivity mainActivity;
    int nselected=0;





    public MyAdapter(@NonNull MainActivity context, int resource, @NonNull ArrayList<MyData> a) {
        super(context, resource, a);
        this.context=context;
        mainActivity=context;
        arr=a;
    }



    @Nullable
    @Override
    public MyData getItem(int position) {
        return arr.get(position);
    }




    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView=convertView==null?LayoutInflater.from(context).inflate(R.layout.my_layout,parent,false):convertView;

        TextView tv1=convertView.findViewById(R.id.date);
        TextView tv2=convertView.findViewById(R.id.message);
        CheckBox check = convertView.findViewById(R.id.checkBox);
        MyData md=getItem(position);

        View finalConvertView = convertView;
        mainActivity.runOnUiThread(() -> {
            check.setOnCheckedChangeListener((compoundButton, b) -> md.checked=b);
            finalConvertView.setOnLongClickListener(view -> {

                if(!md.checked) {
                    setCheckVisible(true);
                    nselected++;
                    mainActivity.nselected++;
                    setCheckedAt(position, true);
                    mainActivity.selectionStarted();
                    notifyDataSetChanged();
                }
                return true;
            });
            finalConvertView.setOnClickListener(view -> {
                if(nselected>0){
                    if(md.checked){
                        md.checked=false;
                        nselected--;
                        mainActivity.nselected--;
                        if(nselected==0){
                            setCheckVisible(false);
                        }
                        setCheckedAt(position,false);
                    }
                    else{
                        md.checked=true;
                        nselected++;
                        mainActivity.nselected++;
                        setCheckedAt(position,true);
                    }
                    notifyDataSetChanged();
                }
                else{
                    Intent intent = new Intent(context, SetReminder.class);
                    intent.putExtra("pos",position);
                    mainActivity.startActivity(intent);
                }
            });
        });

        tv1.setText(md.date);
        tv1.setVisibility(View.VISIBLE);
        check.setVisibility(makeCheckVisible?View.VISIBLE:View.GONE);
        check.setChecked(md.checked);
        tv2.setText(md.message);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            convertView.setFocusable(View.NOT_FOCUSABLE);
        }

        convertView.setBackgroundColor(Color.parseColor(md.isVIP?"#603F51B5":"#00000000"));

        return convertView;
    }






    public void setCheckVisible(boolean v){
        makeCheckVisible=v;
        if(!v)
            mainActivity.selectionStopped();
    }




    public void setCheckedAt(int pos,boolean v){
        arr.get(pos).checked=v;
    }


    public void resetAllCheck(){
        for(MyData md : arr) {
            md.checked = false;
        }
    }



}
