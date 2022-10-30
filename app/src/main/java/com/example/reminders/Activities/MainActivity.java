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
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.reminders.Adapter.MyAdapter;
import com.example.reminders.Data.MyData;
import com.example.reminders.Data.SyncedArray;
import com.example.reminders.Drive.Syncer;
import com.example.reminders.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;

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
    private ImageButton upload_button;
    private ImageButton restore_button;

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
        upload_button=findViewById(R.id.upload_button);
        upload_button.setOnClickListener(view -> onSyncClicked("upload"));
        restore_button = findViewById(R.id.restore_button);
        restore_button.setOnClickListener(view -> onSyncClicked("restore"));
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
        Resources resources = getResources();
        Drawable drawable = ResourcesCompat.getDrawable(resources, R.drawable.abcd, null);
        button.setForeground(drawable);
    }

    public void notifySelectionStarted() {
        Resources resources = getResources();
        Drawable drawable = ResourcesCompat.getDrawable(resources, R.drawable.delete_icon, null);
        button.setForeground(drawable);
    }

    public void onSyncClicked(String s){
        if(account==null){
            Intent intent = new Intent(getBaseContext(), GoogleLoginActivity.class);
            startActivity(intent);
            return;
        }
        Syncer syncer =new Syncer(this);
        if(s=="upload")
            syncer.uploadDatabase();
        if(s=="restore")
            syncer.restoreDatabase(arr);
    }

}



