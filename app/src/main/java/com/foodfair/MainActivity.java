package com.foodfair;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.foodfair.network.FoodFairWSClient;
import com.foodfair.network.Register;
import com.foodfair.task.UiHandler;
import com.foodfair.ui.login.Login;
import com.foodfair.ui.qrscanner.QRScanner;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;
    private MenuItem signInMenu;
    UiHandler uiHandler;

    private NavController navController;


        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uiHandler =  UiHandler.createHandler(this.getMainLooper(),this);
        if (user != null){
            if (FoodFairWSClient.globalCon != null){
                FoodFairWSClient.globalCon.close();
            }
            try {
                FoodFairWSClient.globalCon = new FoodFairWSClient(new URI("ws://ss.caihuashuai.com:8282"));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            FoodFairWSClient.globalCon.connect();
        }

//
//        NotificationManager notif=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification notify=new Notification.Builder
//                (getApplicationContext()).setContentTitle(tittle).setContentText(body).
//                setContentTitle(subject).setSmallIcon(R.drawable.abc).build();

//        notify.flags |= Notification.FLAG_AUTO_CANCEL;
//        notif.notify(0, notify);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_user_profile, R.id.nav_history_of_items,
                R.id.nav_leaderboards, R.id.nav_manage_food_bookings,
                R.id.nav_view_food_postings, R.id.nav_qr_scanner, R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.nav_user_profile ||
                    item.getItemId() == R.id.nav_settings ||
                    item.getItemId() == R.id.nav_history_of_items ||
                    item.getItemId() == R.id.nav_manage_food_bookings ||
                    item.getItemId() == R.id.nav_qr_scanner) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user != null){
                        navController.navigate(item.getItemId());
                    } else {
                        Toast toast = new Toast(getApplicationContext())
                                .makeText(getApplicationContext(), "Please sign in", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    DrawerLayout drawer = findViewById(R.id.drawer_layout);
                    drawer.close();
                } else if(item.getItemId() == R.id.nav_qr_scanner) {
                    Intent intent = new Intent(getApplicationContext(), QRScanner.class);
                    // Could be startActivityForResult or something
                    startActivity(intent);
                } else {
                    navController.navigate(item.getItemId());
                    DrawerLayout drawer = findViewById(R.id.drawer_layout);
                    drawer.close();
                }
                return true;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        signInMenu = menu.findItem(R.id.action_settings);
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            signInMenu.setTitle("Sign in");
        } else {
            signInMenu.setTitle("Sign out");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                if(FirebaseAuth.getInstance().getCurrentUser() == null){
                    Intent intent = new Intent(this, Login.class);
                    startActivity(intent);
                } else {
                    FirebaseAuth.getInstance().signOut();
                    Toast toast = new Toast(this).makeText(this,
                            "Successfully sign out", Toast.LENGTH_SHORT);
                    toast.show();
                    item.setTitle("Sign in");
                    navController.navigate(R.id.nav_home);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


}