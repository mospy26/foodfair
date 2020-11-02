package com.foodfair.ui.qr_success;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.foodfair.R;
import com.foodfair.model.FooditemTransaction;
import com.google.gson.Gson;

public class QRSuccess extends AppCompatActivity {

    private FooditemTransaction transaction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        this.transaction = new Gson().fromJson(extras.getString("transactionId"), FooditemTransaction.class);
        Log.e("naa", transaction.getAliveRecord().toString());
    }
}
