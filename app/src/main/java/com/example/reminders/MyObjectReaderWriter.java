package com.example.reminders;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.app.PendingIntent;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MyObjectReaderWriter<T> {
    ArrayList<T> arr;
    String url;

    public MyObjectReaderWriter(String u, ArrayList<T> a) {
        url = u;
        arr = a;
    }
    ArrayList<T> getArray(){
        return arr;
    }

    /**
     * this function writes objects to the internal storage
     */
    void writeObjects() {
        try {
            FileOutputStream fos = new FileOutputStream(url);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            for (T md : arr) {
                oos.writeObject(md);
            }
            oos.close();
            fos.close();
            Log.d(TAG, "writeObjects: "+arr.size());
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }


    /**
     * reads data from internal storage
     */
     void readObjects(){
        try{
            File file= new File(url);
            file.createNewFile();
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        arr= new ArrayList<>();
        try{
            FileInputStream fis=new FileInputStream(url);
            ObjectInputStream ois = new ObjectInputStream(fis);
            for(;;){
                T d;
                try{
                    d=(T) ois.readObject();
                }catch(Exception x){
                    ois.close();
                    fis.close();
                    Log.d(TAG, "readObjects: "+arr.size());
                    break;
                }
                arr.add(d);
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
