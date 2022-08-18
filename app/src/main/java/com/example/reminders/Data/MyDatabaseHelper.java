package com.example.reminders.Data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private final String request_code_column = "REQUEST_CODE";
    private final String Table_name = "MyDatabase";
    private final String date = "DATE";
    private final String message = "MESSAGE";
    private final String vip = "VIP";

    @SuppressLint("NewApi")
    public MyDatabaseHelper(Context context) {
        super(context, "MyDatabase.db",null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery= "create table " + Table_name + " ( " + request_code_column + " INTEGER PRIMARY KEY AUTOINCREMENT, " + date + " TEXT, " + message + " TEXT, " + vip + " INTEGER)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void add(MyData myData){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(message,myData.message);
        cv.put(date,myData.dateString);
        cv.put(vip,myData.isVIP?1:0);
        db.insert(Table_name,null,cv);
        db.close();
        db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("select max("+request_code_column+") from "+Table_name,null);
        cursor.moveToFirst();
        myData.requestCode=cursor.getInt(0);
        cursor.close();
        db.close();
    }

    public void delete(MyData myData){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(Table_name,request_code_column+" = "+myData.requestCode,null);
        db.close();
    }

    public ArrayList<MyData> getAll(){
        ArrayList<MyData> arrayList= new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(Table_name,null,null,null,null,null,null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            MyData myData = new MyData();
            myData.requestCode = cursor.getInt(0);
            myData.dateString = cursor.getString(1);
            myData.message = cursor.getString(2);
            myData.isVIP = cursor.getInt(3)==1;
            arrayList.add(myData);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return arrayList;
    }

    public void deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(Table_name,null,null);
        db.close();
    }

    public void update(MyData myData) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(message,myData.message);
        cv.put(date,myData.dateString);
        cv.put(vip,myData.isVIP?1:0);
        db.update( Table_name , cv ,request_code_column + " = " + myData.requestCode , null );
    }
}
