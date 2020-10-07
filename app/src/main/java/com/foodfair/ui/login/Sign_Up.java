package com.foodfair.ui.login;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.foodfair.R;
import com.foodfair.utilities.LoadingDialog;
import com.foodfair.utilities.SignUpHandler;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Sign_Up extends AppCompatActivity {


    Context context = this;
    private EditText Name;
    private EditText Email;
    private EditText password;
    private Button signUpBtn;
    private SignUpHandler  signUpHandler;
    LoadingDialog loadingDialog = new LoadingDialog(this);

    //private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState);setContentView(R.layout.activity_sign__up);

    Name = findViewById(R.id.newName);
    Email = findViewById(R.id.newEmail);
    password = findViewById(R.id.newPassword);
    signUpHandler = SignUpHandler.getInstance();

    signUpBtn = findViewById(R.id.signupbtn);

    signUpBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            signUpHandler.createUserAccount(Email.getText().toString(),password.getText().toString().trim(),Name.getText().toString());
            loadingDialog.startLoadingAnimationg();

            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadingDialog.dismissDialog();
                }
            },2000);

            Intent intent = new Intent(context, Login.class);
            startActivity(intent);

        }
    });

    }







}