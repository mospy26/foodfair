package com.foodfair.ui.login;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class SignInHandler {

    private FirebaseAuth firebaseAuth;
    private Context context;
    private LoginSuccessListener listener;

    public SignInHandler(Context context, LoginSuccessListener listener) {
        firebaseAuth = FirebaseAuth.getInstance();
        this.context = context;
        this.listener = listener;
    }


    public void SignIn(String email, String password)
    {
        if (email.equals("") || password.equals("")) {
            Toast.makeText(context, "Please fill in both email and password", Toast.LENGTH_LONG).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.d(TAG, "createUserWithEmail:success");
                listener.callback(firebaseAuth.getCurrentUser());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "createUserWithEmail:failed");
                Toast.makeText(context, "Invalid login, please try again", Toast.LENGTH_LONG).show();
            }
        });
        return;
    }
}
