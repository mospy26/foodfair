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
import com.foodfair.model.Leaderboard;
import com.foodfair.model.Ranking;
import com.foodfair.model.User;
import com.foodfair.model.UsersInfo;
import com.foodfair.task.UiHandler;
import com.foodfair.ui.qr_success.QRSuccess;
import com.foodfair.utilities.Cache;
import com.foodfair.utilities.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;
import com.google.zxing.Result;

import org.w3c.dom.Document;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    private Ranking ranking;
    String period;
    String consumerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 1);
        } else {
            init();
        }

        cache = Cache.getInstance(getApplicationContext());
        String period = _getPeriod();
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
        UiHandler.getInstance().context = this;

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
                                    consumerId = document.getId();
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
                                    if (!foodRef.getDonorRef().getId().equals(FirebaseAuth.getInstance().getUid())) {
                                        spawnNotExistsDialog();
                                        return;
                                    }
                                    onStateChange(null);
                                    Log.d("Transaction Food", "DocumentSnapshot data: " + document.getData());
                                } else {
                                    Log.d("Transaction Food", "No such document");
                                }
                            }
                        });
                        Log.d("Transaction", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("Transaction", "No such document");
                        spawnNotExistsDialog();
                    }
                } else {
                    // Could not complete firestore call
                    Log.e("Transaction", "Could not be obtained");
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
                        cache.add(transactionId, transaction);
                        cache.add(transaction.getFoodRef().getId(), foodRef);
                        cache.add(transaction.getConsumer().getId(), consumer);
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

    private String _getPeriod() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        int month = cal.get(Calendar.MONTH) + 1;
        period = cal.get(Calendar.YEAR) + "" + month;
        return period;
    }

    private void updateLeaderboard() {
        CollectionReference leaderboardRef = FirebaseFirestore.getInstance().collection(getResources().getString(R.string.FIREBASE_COLLECTION_LEADERBOARD));
        leaderboardRef
                .document(period)
                .collection("ranking")
                .document(FirebaseAuth.getInstance().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                ranking = document.toObject(Ranking.class);
                                ranking.setScore(ranking.getScore() + 100);
                                ranking.setDonationCount(ranking.getDonationCount() + 1);
                            } else {
                                ranking = new Ranking();
                                ranking.setDonationCount(1L);
                                ranking.setAverageRating(2.9999);
                                ranking.setScore(100L);
                                ranking.setDonor(transaction.getDonor());
                            }
                            saveRanking();
                        }
                    }

                });

        leaderboardRef
                .document("total")
                .collection("ranking")
                .document(FirebaseAuth.getInstance().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Ranking totalRanking;
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        totalRanking = document.toObject(Ranking.class);
                        totalRanking.setScore(totalRanking.getScore() + 100);
                        totalRanking.setDonationCount(totalRanking.getDonationCount() + 1);
                        if (totalRanking.getDonationCount() == 5) {
                            addBadgeToDonor(201L);
                            cache.add(FirebaseAuth.getInstance().getCurrentUser().getUid(), donor);
                        }
                    } else {
                        addBadgeToDonor(200L);
                        totalRanking = new Ranking();
                        totalRanking.setDonationCount(1L);
                        totalRanking.setAverageRating(2.9999);
                        totalRanking.setScore(100L);
                        totalRanking.setDonor(transaction.getDonor());
                    }
                    saveTotalRanking();

                    if (((ArrayList<DocumentReference>) consumer.getAsConsumer().get(getResources().getString(R.string.FIREBASE_COLLECTION_USER_INFO_SUB_KEY_OF_AS_CONSUMER_TRANSACTIONS))).size() == 5) {
                        addBadgeToConsumer(101L);
                    } else if (((ArrayList<DocumentReference>) consumer.getAsConsumer().get(getResources().getString(R.string.FIREBASE_COLLECTION_USER_INFO_SUB_KEY_OF_AS_CONSUMER_TRANSACTIONS))).size() == 1) {
                        addBadgeToConsumer(100L);
                    }
                }
            }

        });
    }

    public void saveTotalRanking() {
        CollectionReference leaderboardRef = FirebaseFirestore.getInstance().collection(getResources().getString(R.string.FIREBASE_COLLECTION_LEADERBOARD));
        FirebaseFirestore.getInstance().collection(("leaderboard"))
                .document("total")
                .collection("ranking")
                .document(FirebaseAuth.getInstance().getUid()).set(ranking).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("Leaderboard", "Success updating leaderboard");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Leaderboard", "Failed to update ranking");
            }
        });
    }

    public void saveRanking() {
        CollectionReference leaderboardRef = FirebaseFirestore.getInstance().collection(getResources().getString(R.string.FIREBASE_COLLECTION_LEADERBOARD));
        FirebaseFirestore.getInstance().collection(("leaderboard"))
                .document(period)
                .collection("ranking")
                .document(FirebaseAuth.getInstance().getUid()).set(ranking).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("Leaderboard", "Success updating leaderboard");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Leaderboard", "Failed to update ranking");
            }
        });
    }

    private void addBadgeToDonor(Long badgeId) {
        String badgesString = getResources().getString(R.string.FIREBASE_COLLECTION_USER_INFO_SUB_KEY_OF_AS_CONSUMER_BADGES);
        ArrayList<Long> badges = (ArrayList<Long>) donor.getAsDonor().get(badgesString);
        Set<Long> badgesSet = badges.stream().collect(Collectors.toSet());
        badgesSet.add(badgeId);
        Map<String, Object> asDonor = donor.getAsDonor();
        asDonor.replace(badgesString, badgesSet.stream().collect(Collectors.toList()));
        donor.setAsDonor(asDonor);
        FirebaseFirestore.getInstance().collection(getResources().getString(R.string.FIREBASE_COLLECTION_USER_INFO))
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("asDonor", asDonor);
    }

    private void addBadgeToConsumer(Long badgeId) {
        String badgesString = getResources().getString(R.string.FIREBASE_COLLECTION_USER_INFO_SUB_KEY_OF_AS_CONSUMER_BADGES);
        ArrayList<Long> badges = (ArrayList<Long>) consumer.getAsConsumer().get(badgesString);
        Set<Long> badgesSet = badges.stream().collect(Collectors.toSet());
        badgesSet.add(badgeId);
        Map<String, Object> asConsumer = consumer.getAsConsumer();
        asConsumer.replace(badgesString, badgesSet.stream().collect(Collectors.toList()));
        FirebaseFirestore.getInstance().collection(getResources().getString(R.string.FIREBASE_COLLECTION_USER_INFO))
                .document(consumerId).update("asConsumer", asConsumer).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("Consumer badges", "added");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Consumer badges", "unsuccessful");
            }
        });
    }

    public void approveTransactionAndSave(FooditemTransaction transaction) {

        CollectionReference transactions = FirebaseFirestore.getInstance().collection(getResources().getString(R.string.FIREBASE_COLLECTION_FOOD_ITEM_TRANSACTION));
        transaction.setFinishDate(Timestamp.now());
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

        transactions.document(this.transactionId).update("finishDate", new Date(System.currentTimeMillis())) // TODO replace with SUCCESS status
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

        updateLeaderboard();
    }
}
