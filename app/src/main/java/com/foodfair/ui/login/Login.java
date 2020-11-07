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
import com.foodfair.model.UsersInfo;
import com.foodfair.task.UiHandler;
import com.foodfair.utilities.Cache;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
    Cache cache;
    String userId;
    UsersInfo user;
    boolean started = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("foodfair", Context.MODE_PRIVATE);
        String uid = sharedPref.getString("firebasekey", null);

        firebaseAuth = FirebaseAuth.getInstance();

        textView = findViewById(R.id.newAccount);
        email = findViewById(R.id.loginEmail);
        signIn = findViewById(R.id.sign_in_btn);
        password = findViewById(R.id.loginPassword);
        cache = Cache.getInstance(this);

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
                    sharedPreferences = getSharedPreferences("foodfair", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("firebasekey", userId);
                    editor.commit();
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
                userId = firebaseAuth.getUid();
                // store it globally for access
                sharedPreferences = getSharedPreferences("foodfair", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("firebasekey", userId);
                editor.commit();
                fetchUser();
                Log.d("Sign in", "Success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Log.e("Sign in", "Failed");
                Toast.makeText(context, "Invalid login, please try again", Toast.LENGTH_LONG).show();
            }
        });
        return;
    }

    private void fetchUser() {
        FirebaseFirestore.getInstance().collection(getResources().getString(R.string.FIREBASE_COLLECTION_USER_INFO)).document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    user = document.toObject(UsersInfo.class);
                    // store it globally for access
                    sharedPreferences = getSharedPreferences("foodfair", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putLong(userId + "_status", user.getStatus());
                    editor.commit();
                } else {
                    Toast.makeText(context, "Invalid Login", Toast.LENGTH_LONG).show();
                }
                if (!started) {
                    if (user.getLocation() == null || user.getLocation().equals("") ||
                            user.getAllergy() == null || user.getAllergy().size() == 0 || user.getPreference() == null) {
                        Intent i = new Intent(Login.this, SetUp.class);
                        startActivity(i);
                        finish();
                        started = true;
                    } else {
                        Intent i = new Intent(Login.this, MainActivity.class);
                        startActivity(i);
                        finish();
                        started = true;
                    }
                }
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        UiHandler.getInstance().context = this;
    }
}