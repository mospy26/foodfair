package com.foodfair.utilities;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.foodfair.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUpHandler {


    static SignUpHandler signUpHandler;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userID;


    private SignUpHandler() {
    }

    public static SignUpHandler getInstance() {
        if (signUpHandler == null) {
            signUpHandler = new SignUpHandler();
        }
        return signUpHandler;
    }

    public void createUserAccount(String userName, String passWord, String Name)
    {
        firebaseAuth.createUserWithEmailAndPassword(userName,passWord).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                userID = firebaseAuth.getUid();
                Log.println(Log.INFO,"Signup","Sign Up Successful");
                DocumentReference documentReference = db.collection("Users").document(userID);
                Map<String,Object> users = new HashMap<>();
                User user = new User(Name,userName,new ArrayList<>(),new ArrayList<>(),new ArrayList<>());
                users.put("User",user);
                documentReference.set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.println(Log.INFO,"Sign Up/Database","Successfully Persisted Infomation");


                    }
                });
            }
        }).addOnFailureListener(e->{
            e.printStackTrace();
        });
    }

}

