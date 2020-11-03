package com.foodfair.ui.qr_success;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.foodfair.R;
import com.foodfair.model.FoodItemInfo;
import com.foodfair.model.FooditemTransaction;
import com.foodfair.model.UsersInfo;
import com.foodfair.ui.qrscanner.OnStateChangeListener;
import com.foodfair.utilities.Cache;
import com.foodfair.utilities.CacheObject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

public class QRSuccess extends AppCompatActivity implements OnStateChangeListener {

    private FooditemTransaction transaction;
    private UsersInfo donor;
    private UsersInfo consumer;
    private FoodItemInfo foodRef;

        

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_success);
        Bundle extras = getIntent().getExtras();
        String transactionId = (String) extras.get("transactionId");
        retrieveData(transactionId);
    }

    private void retrieveData(String transactionId) {
        Cache cache = Cache.getInstance(getApplicationContext());
        CacheObject transactionObject = cache.get(transactionId);
        if (transactionObject == null) {
            fetchTransaction(transactionId);
        }
        else {
            transaction = (FooditemTransaction) cache.get(transactionId).getData();
            consumer = (UsersInfo) cache.get(transaction.getConsumer().getId()).getData();
            foodRef = (FoodItemInfo) cache.get(transaction.getFoodRef().getId()).getData();
        }
    }

    private void fetchTransaction(String transactionId) {
        FirebaseFirestore.getInstance().collection(getResources().getString(R.string.FIREBASE_COLLECTION_FOOD_ITEM_TRANSACTION)).document(transactionId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    transaction = document.toObject(FooditemTransaction.class);
                    transaction.getDonor().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                donor = document.toObject(UsersInfo.class);
                                onStateChange(null);
                                Log.d("Transaction Donor", "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d("Transaction Donor", "No such document");
                            }
                        }
                    });

                    transaction.getConsumer().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                consumer = document.toObject(UsersInfo.class);
                                onStateChange(null);
                                Log.d("Transaction Consumer", "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d("Transaction Consumer", "No such document");
                            }
                        }
                    });

                    transaction.getFoodRef().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                foodRef = document.toObject(FoodItemInfo.class);
                                onStateChange(null);
                                Log.d("Transaction Food", "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d("Transaction Food", "No such document");
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {

    }

    @Override
    public void onStateChange(Object object) {
        if (this.foodRef != null && this.consumer != null && this.transaction != null) {}
            // TODO: Fill page in
    }
}

