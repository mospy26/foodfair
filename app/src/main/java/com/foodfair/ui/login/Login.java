package com.foodfair.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.foodfair.MainActivity;
import com.foodfair.R;
import com.google.firebase.auth.FirebaseUser;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Login extends AppCompatActivity implements LoginSuccessListener {

    TextView textView;
    Button signIn;
    Context context = this;
    SignInHandler signInHandler;
    EditText email;
    EditText password;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        textView = findViewById(R.id.newAccount);
        email = findViewById(R.id.loginEmail);
        signIn = findViewById(R.id.sign_in_btn);
        password = findViewById(R.id.loginPassword);

        explodeNewScene();
        signInHandler = new SignInHandler(this, this);
        signIn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                signInHandler.SignIn(email.getText().toString(),password.getText().toString().trim());
                return true;
            }
        });

    }

    public void explodeNewScene()
    {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,Sign_Up.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void callback(FirebaseUser user) {
        // Because Firebase will call this multiple times, and they won't resolve this.
        // https://github.com/firebase/quickstart-android/issues/80
        if (currentUser == null) {
            this.currentUser = user;
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}