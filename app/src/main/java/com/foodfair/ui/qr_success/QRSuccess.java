package com.foodfair.ui.qr_success;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.foodfair.R;
import com.foodfair.model.FoodItemInfo;
import com.foodfair.model.FooditemTransaction;
import com.foodfair.model.ReviewInfo;
import com.foodfair.model.UsersInfo;
import com.foodfair.ui.qrscanner.OnStateChangeListener;
import com.foodfair.utilities.Cache;
import com.foodfair.utilities.CacheObject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class QRSuccess extends AppCompatActivity {

    private TextView success_consumerNameTextView;
    private TextView success_receivedTime;
    private TextView success_foodNameTextView;
    private TextView success_pickupLocationTextView;

    private QRSuccessViewModel qrSuccessViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrsuccess);
        qrSuccessViewModel = new QRSuccessViewModel();
        Bundle extras = getIntent().getExtras();
        String transactionId = (String) extras.get("transactionId");
        initViews();
        addObservables();
        retrieveData(transactionId);
    }

    private void initViews() {
        success_consumerNameTextView = findViewById(R.id.success_consumerNameTextView);
        success_receivedTime = findViewById(R.id.success_receivedTime);
        success_foodNameTextView = findViewById(R.id.success_foodNameTextView);
        success_pickupLocationTextView = findViewById(R.id.success_pickupLocationTextView);
    }

    private void addObservables() {
        qrSuccessViewModel.foodItemInfo.observe(this, foodItemInfo -> {
            success_foodNameTextView.setText(foodItemInfo.getName());
        });

        qrSuccessViewModel.consumerUserInfo.observe(this, consumerUserInfo -> {
            success_consumerNameTextView.setText(consumerUserInfo.getName());
        });

        qrSuccessViewModel.foodItemTransaction.observe(this, fooditemTransaction -> {
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String consumedDate = sfd.format(fooditemTransaction.getFinishDate().toDate());
            success_receivedTime.setText(consumedDate);
        });
    }

    private void retrieveData(String transactionId) {
        Cache cache = Cache.getInstance(getApplicationContext());
        CacheObject transactionObject = cache.get(transactionId);
        if (transactionObject == null || transactionObject.getData() == null) {
            fetchTransaction(transactionId);
        }
        else {
            qrSuccessViewModel.foodItemTransaction.setValue((FooditemTransaction) cache.get(transactionId).getData());
            qrSuccessViewModel.consumerUserInfo.setValue((UsersInfo) cache.get(qrSuccessViewModel.foodItemTransaction.getValue().getConsumer().getId()).getData());
            qrSuccessViewModel.foodItemInfo.setValue((FoodItemInfo) cache.get(qrSuccessViewModel.foodItemTransaction.getValue().getFoodRef().getId()).getData());
        }
    }

    private void fetchTransaction(String transactionId) {
        FirebaseFirestore.getInstance().collection(getResources().getString(R.string.FIREBASE_COLLECTION_FOOD_ITEM_TRANSACTION)).document(transactionId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    qrSuccessViewModel.foodItemTransaction.setValue(document.toObject(FooditemTransaction.class));
                    qrSuccessViewModel.foodItemTransaction.getValue().getDonor().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                qrSuccessViewModel.donorUserInfo.setValue(document.toObject(UsersInfo.class));
                                Log.d("Transaction Donor", "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d("Transaction Donor", "No such document");
                            }
                        }
                    });

                    qrSuccessViewModel.foodItemTransaction.getValue().getConsumer().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                qrSuccessViewModel.consumerUserInfo.setValue(document.toObject(UsersInfo.class));
                                Log.d("Transaction Consumer", "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d("Transaction Consumer", "No such document");
                            }
                        }
                    });

                    qrSuccessViewModel.foodItemTransaction.getValue().getFoodRef().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                qrSuccessViewModel.foodItemInfo.setValue(document.toObject(FoodItemInfo.class));
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
}

