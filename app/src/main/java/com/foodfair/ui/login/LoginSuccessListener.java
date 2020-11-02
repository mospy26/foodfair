package com.foodfair.ui.login;

import com.google.firebase.auth.FirebaseUser;

interface LoginSuccessListener {
    public void callback(FirebaseUser user);
}