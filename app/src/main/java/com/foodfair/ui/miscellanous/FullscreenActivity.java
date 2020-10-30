package com.foodfair.ui.miscellanous;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.foodfair.MainActivity;
import com.foodfair.R;
import com.foodfair.network.FoodFairWSClient;
import com.foodfair.task.UiHandler;
import com.foodfair.ui.login.Login;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    Context context = this;
    UiHandler uiHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        uiHandler =  UiHandler.createHandler(this.getMainLooper());
        FoodFairWSClient.globalCon.connect();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            Intent intent = new Intent(context, Login.class);

            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        },1000);




    }
}

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
