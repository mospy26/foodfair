package com.foodfair.utilities;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class SignInHandler {

    private FirebaseAuth firebaseAuth;

    public SignInHandler(){firebaseAuth = FirebaseAuth.getInstance();}


    public  void SignIn(String email, String password)
    {
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.d(TAG, "createUserWithEmail:success"); }
        });

    }
}
