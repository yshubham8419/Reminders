package com.example.reminders.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.reminders.Adapter.MyAdapter;
import com.example.reminders.Data.MyData;
import com.example.reminders.Data.SyncedArray;
import com.example.reminders.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.Scope;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.util.Collections;

public class MainActivity extends AppCompatActivity{

    private SyncedArray arr;
    private MyAdapter adapter;
    private ListView listView;
    private FloatingActionButton button;
    private int selected;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private androidx.appcompat.widget.Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private int drawerSelectedId;
    private boolean drawerSelectedIdChanged;
    private GoogleSignInAccount account;
    private TextView nameTv;
    private ShapeableImageView photo;
    private ImageButton sync;
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
        drawerSelectedId = R.id.nav_home;
        drawerSelectedIdChanged = false;
        account = GoogleSignIn.getLastSignedInAccount(this);
        sync=findViewById(R.id.sync_button);
        sync.setOnClickListener(view -> onSyncClicked());
    }

    private  void initNavigationDrawer(){
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Reminders");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawerOpen,R.string.drawerClose){
            @Override
            public void onDrawerClosed(View drawerView) {
                if(drawerSelectedIdChanged) {
                    startSelectedNavActivity();
                }
                super.onDrawerClosed(drawerView);
            }
        };
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        navigationView = findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(item -> {
            Toast.makeText(this, "called", Toast.LENGTH_SHORT).show();
            drawerSelectedIdChanged = drawerSelectedId != item.getItemId();
            drawerSelectedId = item.getItemId();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
        navigationView.setCheckedItem(R.id.nav_home);
        accountUpdate();
    }
    void accountUpdate(){
        account = GoogleSignIn.getLastSignedInAccount(this);
        View header = navigationView.getHeaderView(0);
        nameTv=header.findViewById(R.id.nav_name);
        photo=header.findViewById(R.id.nav_photo);
        if(account!=null) {
            nameTv.setText(account.getDisplayName());
        }
        else {
            nameTv.setText(R.string.signinrequest);
        }
    }
    @SuppressLint("NonConstantResourceId")
    void startSelectedNavActivity(){
        switch(drawerSelectedId){
            case R.id.nav_account :
                Intent intent = new Intent(getBaseContext(), account==null?GoogleLoginActivity.class:AccountPage.class);
                startActivity(intent);
                break;
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initValues();
        initNavigationDrawer();
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
        if(sharedPreferences.getBoolean("account_updated",false)){
            accountUpdate();
            editor.putBoolean("account_updated",false);
            editor.apply();
        }
        navigationView.setCheckedItem(R.id.nav_home);
        drawerSelectedId = R.id.nav_home;
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
    public void onSyncClicked(){
        if(account==null){
            Intent intent = new Intent(getBaseContext(), GoogleLoginActivity.class);
            startActivity(intent);
            return;
        }
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(MainActivity.this),new Scope(DriveScopes.DRIVE_APPDATA))){
            GoogleSignIn.requestPermissions(MainActivity.this, 0, GoogleSignIn.getLastSignedInAccount(MainActivity.this), new Scope(DriveScopes.DRIVE_APPDATA));
            Toast.makeText(this, "no permissions", Toast.LENGTH_SHORT).show();
            return;
        }
        uploadFile();
    }

    private void  uploadFile(){
        GoogleAccountCredential credential=
                GoogleAccountCredential
                        .usingOAuth2(this, Collections.singleton(DriveScopes.DRIVE_FILE));
        credential.setSelectedAccount(account.getAccount());
        Drive drive = new Drive.Builder(new NetHttpTransport(),new GsonFactory(),credential)
                .setApplicationName("Reminders")
                .build();
        File fileMetadata = new File();
        fileMetadata.setName("photo.jpeg");
        // File's content.
        java.io.File filePath = new java.io.File(Environment.getExternalStorageDirectory()+"/image.jpeg");
        // Specify media type and file-path for file.
        FileContent mediaContent = new FileContent("image/jpeg", filePath);
        Thread thread= new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = drive.files().create(fileMetadata, mediaContent)
                            .setFields("id")
                            .execute();
                }catch(UserRecoverableAuthIOException e){
                            startActivity(e.getIntent());
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

}



