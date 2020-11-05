package com.foodfair.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.foodfair.MainActivity;
import com.foodfair.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Login extends AppCompatActivity {

    TextView textView;
    Button signIn;
    Context context = this;
    EditText email;
    EditText password;
    FirebaseUser currentUser;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("uid", Context.MODE_PRIVATE);
        String uid = sharedPref.getString("uid", null);

        firebaseAuth = FirebaseAuth.getInstance();

        textView = findViewById(R.id.newAccount);
        email = findViewById(R.id.loginEmail);
        signIn = findViewById(R.id.sign_in_btn);
        password = findViewById(R.id.loginPassword);

        explodeNewScene();
        signIn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                SignIn(email.getText().toString(), password.getText().toString().trim());
                return true;
            }
        });

        FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    String userId = firebaseUser.getUid();

                    // store it globally for access
                    sharedPreferences = getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("firebasekey", userId);
                    editor.commit();
                    Intent i = new Intent(Login.this, MainActivity.class);
                    startActivity(i);
                }
            }
        };

        firebaseAuth.addAuthStateListener(authListener);
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

    public void SignIn(String email, String password)  {
        if (email.equals("") || password.equals("") || email == null || password == null) {
            Toast.makeText(context, "Please fill in both email and password", Toast.LENGTH_LONG).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.d(TAG, "createUserWithEmail:success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Log.d(TAG, "createUserWithEmail:failed");
                Toast.makeText(context, "Invalid login, please try again", Toast.LENGTH_LONG).show();
            }
        });
        return;
    }
}