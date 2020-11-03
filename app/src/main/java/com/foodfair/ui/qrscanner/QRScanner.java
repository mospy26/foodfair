package com.foodfair.ui.qrscanner;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.foodfair.R;
import com.foodfair.model.FoodItemInfo;
import com.foodfair.model.FooditemTransaction;
import com.foodfair.model.User;
import com.foodfair.model.UsersInfo;
import com.foodfair.ui.qr_success.QRSuccess;
import com.foodfair.utilities.Cache;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.zxing.Result;

import java.io.Serializable;

/**
 * Adopted from https://github.com/yuriy-budiyev/code-scanner
 *
 */
public class QRScanner extends AppCompatActivity implements OnStateChangeListener {

    private CodeScanner codeScanner;
    private FooditemTransaction transaction;
    private UsersInfo donor;
    private UsersInfo consumer;
    private FoodItemInfo foodRef;
    private String transactionId;
    private Cache cache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 1);
        } else {
            init();
        }

        cache = Cache.getInstance(getApplicationContext());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            init();
        }
    }

    public void init() {
        setContentView(R.layout.camera_qrscannerview);
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        codeScanner = new CodeScanner(this, scannerView);
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                transactionId = result.getText();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fetchTransaction(result.getText());
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 1);
        } else {
            codeScanner.startPreview();
        }
    }

    @Override
    protected void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }

    private FooditemTransaction fetchTransaction(String transactionId) {
        try {
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

                        if (transaction.getStatus() == 1 || transaction.getStatus() == 2) {
                            spawnAlreadyDonatedDialog();
                            return;
                        }

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
                        Log.d("Transaction", "DocumentSnapshot data: " + document.getData());

                        cache.add(transactionId, transaction);
                        cache.add(transaction.getFoodRef().getId(), foodRef);
                        cache.add(transaction.getConsumer().getId(), consumer);
                    } else {
                        Log.d("Transaction", "No such document");
                        spawnNotExistsDialog();
                    }
                } else {
                    // Could not complete firestore call
                    Log.e("CRAP", "helloworld");
                }
            });
        }
        catch (Exception e) {
            spawnNotExistsDialog();
        }
        return transaction;
    }

    private void spawnDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm booking")
                .setMessage("Approve this transaction for consumer: " + consumer.getName() + " for food item " + foodRef.getName() + " ?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        approveTransactionAndSave(transaction);
                        Intent i = new Intent(QRScanner.this, QRSuccess.class);
                        i.putExtra("transactionId", transactionId);
                        startActivity(i);
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    private void spawnNotExistsDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Transaction not found");
        alertDialog.setMessage("This consumer has not booked this item and/or does not exist");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.show();
    }

    private void spawnAlreadyDonatedDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Transaction already completed");
        alertDialog.setMessage("This Food Item was already donated");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.show();
    }

    @Override
    public void onAttach(Activity activity) {

    }

    @Override
    public void onStateChange(Object object) {
        if (this.foodRef != null && this.consumer != null && this.transaction != null)
            spawnDialog();
    }

    public void approveTransactionAndSave(FooditemTransaction transaction) {

        CollectionReference transactions = FirebaseFirestore.getInstance().collection(getResources().getString(R.string.FIREBASE_COLLECTION_FOOD_ITEM_TRANSACTION));
        transactions.document(this.transactionId).update("status", 2) // TODO replace with SUCCESS status
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Transaction update", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Transaction update", "Error writing document", e);
                    }
                });
    }
}
