package com.foodfair;

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

import com.foodfair.ui.login.Login;
import com.foodfair.ui.profiles.UserProfileActivity;
import com.foodfair.ui.qrscanner.QRScanner;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;
    private MenuItem signInMenu;


        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_user_profile, R.id.nav_history_of_items, R.id.nav_leaderboards,
                R.id.nav_manage_food_postings, R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.nav_user_profile ||
                    item.getItemId() == R.id.nav_settings) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user != null){
                        navController.navigate(item.getItemId());
                        DrawerLayout drawer = findViewById(R.id.drawer_layout);
                        drawer.close();
                    } else {
                        Toast toast = new Toast(getApplicationContext())
                                .makeText(getApplicationContext(), "Please sign in", Toast.LENGTH_SHORT);
                        toast.show();
                    }
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