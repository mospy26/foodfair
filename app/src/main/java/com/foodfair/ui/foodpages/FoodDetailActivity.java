package com.foodfair.ui.foodpages;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.foodfair.R;
import com.foodfair.model.FoodItemInfo;
import com.foodfair.model.FooditemTransaction;
import com.foodfair.model.ReviewInfo;
import com.foodfair.model.UsersInfo;
import com.foodfair.network.BookFood;
import com.foodfair.network.FoodFairWSClient;
import com.foodfair.task.UiHandler;
import com.foodfair.ui.book_success.BookSuccessActivity;
import com.foodfair.ui.book_success.BookSuccessViewModel;
import com.foodfair.utilities.Const;
import com.foodfair.utilities.Utility;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FoodDetailActivity extends AppCompatActivity {


    private final int BOOK_SUCCESS_REQ_CODE = 1121;
    // !!!!!! Assume we have user ID!!!
    private static String UID;
    private static final String TAG = MapViewActivity.class.getSimpleName();

    String foodId;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    Const aConst = Const.getInstance();
    FoodDetailModel foodDetailModel;
    TextView foodNameTextView;
    ShapeableImageView foodImageView;
    TextView foodAllergyTextView;
    TextView foodOnDateTextView;
    TextView foodExpireDateTextView;
    TextView foodQuantityTextView;
    TextView foodDonorNameTextView;
    TextView foodDonorAddressTextView;
    TextView foodTextDescriptionTextView;
    String mDonorID;
    String mFoodItemID;
    Timestamp mOpenDate;


    private FirebaseFirestore mFirestore;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirestore = FirebaseFirestore.getInstance();
        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        setContentView(R.layout.food_item);
        foodDetailModel = new FoodDetailModel();
        InitUI();
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("foodfair", Context.MODE_PRIVATE);
        String uid = sharedPref.getString("firebasekey", null);
        Long status = sharedPref.getLong(uid + "_status", -1);
        if (status == 0) {
            View view = findViewById(R.id.fooddetail_bookButton);
            view.setVisibility(View.GONE);
        }

        Intent intent = getIntent();
        String idFromIntent = intent.getStringExtra("foodId");
        if(idFromIntent != null && !idFromIntent.isEmpty()){
            foodId = idFromIntent;
        }
        viewModelObserverSetup();
    }
    @Override
    protected void onStart() {
        super.onStart();
        firebaseRegisterAndLogin();
    }

    private void firebaseRegisterAndLogin() {
        fetchDBInfo(foodId);
//        String email = getResources().getString(R.string.firebase_email);
//        String password = getResources().getString(R.string.firebase_password);
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        if (auth.isSignInWithEmailLink(email)) {
//            fetchDBInfo(foodId);
//        } else {
//            auth.signInWithEmailAndPassword(email, password)
//                    .addOnCompleteListener(this, task -> {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            fetchDBInfo(foodId);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w("TAG", "signInWithEmail:failure", task.getException());
//                        }
//                    });
//        }
    }
    private void fetchDBInfo(String userId) {
        mFirestore.collection(getResources().getString(R.string.FIREBASE_COLLECTION_FOOD_ITEM_INFO))
                .document(foodId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    mFoodItemID = document.getId();
                    FoodItemInfo foodItemInfo = document.toObject(FoodItemInfo.class);
                    setFoodDetailUI(foodItemInfo);
                    Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                } else {
                    Log.d("TAG", "No such document");
                }
            } else {
                int k = 1;
            }
        });
    }

    private void setFoodDetailUI(FoodItemInfo foodItemInfo) {
        foodDetailModel.currentFoodDetailInfo.setValue(foodItemInfo);
        DocumentReference donor = foodItemInfo.getDonorRef();
        donor.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                mDonorID = task.getResult().getId();
                UsersInfo donorInfo = task.getResult().toObject(UsersInfo.class);
                foodDetailModel.donorUserInfo.setValue(donorInfo);
            }
        });
    }

    private void viewModelObserverSetup() {
        foodDetailModel.donorUserInfo.observe(this,donorUserInfo->{
            foodDetailModel.donorName.setValue(donorUserInfo.getName());
            if (donorUserInfo.getLocation() != null){
                foodDetailModel.donorAddress.setValue(donorUserInfo.getLocation().toString());
            }
        });
        foodDetailModel.currentFoodDetailInfo.observe(this, currentFoodDetailInfo->{
            foodDetailModel.foodName.setValue(currentFoodDetailInfo.getName());
            if (currentFoodDetailInfo.getImageDescription() != null && currentFoodDetailInfo.getImageDescription().size() > 0){
                foodDetailModel.foodImageUrl.setValue(currentFoodDetailInfo.getImageDescription().get(0));
            }
            foodDetailModel.allergy.setValue(currentFoodDetailInfo.getAllergyInfo());
            foodDetailModel.foodDateOn.setValue(currentFoodDetailInfo.getDateOn());
            foodDetailModel.foodDateExpire.setValue(currentFoodDetailInfo.getDateExpire());
            foodDetailModel.foodQuantity.setValue(currentFoodDetailInfo.getCount().longValue());
            foodDetailModel.foodTextDescription.setValue(currentFoodDetailInfo.getTextDescription());
        });

        // ------------
        foodDetailModel.foodName.observe(this, foodName->{
            foodNameTextView.setText(foodName);
        });
        foodDetailModel.foodImageUrl.observe(this, foodImageUrl->{
            Picasso.get().load(foodImageUrl).into(foodImageView);
        });
        foodDetailModel.allergy.observe(this,allergy->{
            String allergy_text = "";
            if (allergy != null) {
                for (int i = 0; i < allergy.size(); ++i) {
                    allergy_text += aConst.ALLERGY_DETAIL.get(allergy.get(i));
                    if (i != allergy.size() - 1 && i != allergy.size() - 2) {
                        allergy_text += ", ";
                    }
                    if (i == allergy.size() - 2) {
                        allergy_text += " and ";
                    }
                }
            }
            foodAllergyTextView.setText(allergy_text);
        });
        foodDetailModel.foodDateExpire.observe(this, foodDateExpire -> {
            if (foodDateExpire != null) {
                Date date = foodDateExpire.toDate();
                String expireDateText = simpleDateFormat.format(date);
                foodExpireDateTextView.setText(expireDateText);
            }
        });
        foodDetailModel.foodDateOn.observe(this, foodDateOn -> {
            if (foodDateOn != null) {
                Date date = foodDateOn.toDate();
                String onDateText = simpleDateFormat.format(date);
                foodOnDateTextView.setText(onDateText);
            }
        });


        foodDetailModel.foodQuantity.observe(this, foodQuantity->{
            foodQuantityTextView.setText(Long.toString(foodQuantity));
        });
        foodDetailModel.donorName.observe(this, donorName->{
            foodDonorNameTextView.setText(donorName);
        });
        foodDetailModel.donorAddress.observe(this, donorAddress->{
            foodDonorAddressTextView.setText(donorAddress);
        });
        foodDetailModel.foodTextDescription.observe(this, foodTextDescription->{
            foodTextDescriptionTextView.setText(foodTextDescription);
        });
    }

    private void InitUI() {
        foodNameTextView = findViewById(R.id.fooddetail_foodNameTextView);
        foodImageView = findViewById(R.id.fooddetail_foodImageView);
        foodAllergyTextView = findViewById(R.id.fooddetail_foodallergyTextView);
        foodOnDateTextView = findViewById(R.id.fooddetail_foodOnDateTextView);
        foodExpireDateTextView = findViewById(R.id.fooddetail_foodExpireDateTextView);
        foodQuantityTextView = findViewById(R.id.fooddetail_foodQuantityTextView);
        foodDonorNameTextView = findViewById(R.id.fooddetail_donorNameTextView);
        foodDonorAddressTextView = findViewById(R.id.fooddetail_donorAddressTextView);
        foodTextDescriptionTextView = findViewById(R.id.fooddetail_foodTextDescriptionTextView);
    }

    /**
     * When book button is clicked, produce a transaction with
     * consumer, donor, foodref, openDate and status
     * @param view
     */

    public void onBookClick(View view){
        FooditemTransaction foodItemTransaction = new FooditemTransaction();
        Long bookStatus = aConst.TRANSACTION_STATUS.get("Booked");
        String userTableStr = getResources().getString(R.string.FIREBASE_COLLECTION_USER_INFO);
        String foodTableStr = getResources().getString(R.string.FIREBASE_COLLECTION_FOOD_ITEM_INFO);
        DocumentReference donorRef = mFirestore.document(userTableStr + "/" + mDonorID);
        DocumentReference consumerRef = mFirestore.document(userTableStr + "/" + UID);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
            consumerRef = mFirestore.document(userTableStr + "/" + firebaseUser.getUid());
        }
        DocumentReference foodRef = mFirestore.document(foodTableStr + "/" + mFoodItemID);
        mOpenDate = Utility.parseDateTime(Utility.getCurrentTimeStr(), aConst.DATE_TIME_PATTERN);

        DocumentReference finalConsumerRef = consumerRef;
        foodRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    FoodItemInfo item = document.toObject(FoodItemInfo.class);
                    if (item.getCount() > 0) {
                        populateFoodItemTransaction(aConst.ALIVE_RECORD, null, finalConsumerRef, null,
                                donorRef, null, foodRef, mOpenDate, bookStatus, foodItemTransaction);

                        postFoodTransactionToFirebase(foodItemTransaction);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Not enough quantity left. Cannot book this.", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }
        });
    }

    public void populateFoodItemTransaction(Long aliveRecord, DocumentReference cdReview,
                                            DocumentReference consumer, DocumentReference dcReview, DocumentReference donor,
                                            Timestamp finishDate, DocumentReference foodRef, Timestamp openDate, Long status,
                                            FooditemTransaction foodItemTransaction) {

        foodItemTransaction.setAliveRecord(aliveRecord);
        foodItemTransaction.setCdReview(cdReview);
        foodItemTransaction.setConsumer(consumer);
        foodItemTransaction.setDcReview(dcReview);
        foodItemTransaction.setDonor(donor);
        foodItemTransaction.setFinishDate(finishDate);
        foodItemTransaction.setFoodRef(foodRef);
        foodItemTransaction.setOpenDate(openDate);
        foodItemTransaction.setStatus(status);

    }


    public void postFoodTransactionToFirebase(FooditemTransaction foodItemTransaction) {

        String foodTransStr = getResources().getString(R.string.FIREBASE_COLLECTION_FOOD_ITEM_TRANSACTION);
        CollectionReference transCollect = mFirestore.collection(foodTransStr);
        transCollect.add(foodItemTransaction)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");

                        Gson g = new Gson();
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        FoodFairWSClient.globalCon.send(g.toJson(new BookFood(uid,
                                foodItemTransaction.getDonor().getId(),documentReference.getId()
                        ).buildToMessage(foodItemTransaction.getDonor().getId(),uid)));

                        String transID = documentReference.getId();

                        quantityDecrement(foodId);
                        // add to userinfo -> asconsumer -> transaction (array)
                        updateConsumerTrans(UID, transID);


                        returnBookSuccessPage(documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(FoodDetailActivity.this,
                                        "Unable to Book this food at the moment", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });
    }



    public void returnBookSuccessPage(String transactionId) {

        UsersInfo donorInfo = foodDetailModel.donorUserInfo.getValue();
        FoodItemInfo foodInfo = foodDetailModel.currentFoodDetailInfo.getValue();
        String donorAddr = donorInfo.getLocation().toString();
        String donorName = donorInfo.getName();
        String foodName = foodInfo.getName();
        double lat = 23d;
        double lon = 23d;
        if (donorInfo.getAsDonor() != null){
            lat = (double) donorInfo.getAsDonor().get("lat");
            lon = (double) donorInfo.getAsDonor().get("lon");
        }
        LatLng geoloc = new LatLng(lat, lon);
        String foodUrl = foodInfo.getImageDescription().get(0);
        Timestamp transactionStartDate = mOpenDate;
        Long transactionLiveSec = aConst.ALIVE_RECORD;

        BookSuccessViewModel bookSuccessViewModel = new BookSuccessViewModel(donorName, foodName,
                donorAddr, transactionStartDate, transactionLiveSec, geoloc, transactionId, foodUrl);


        Intent intent = new Intent(FoodDetailActivity.this,
                BookSuccessActivity.class);
        if (intent != null) {

            intent.putExtra("transactionId", transactionId);

            // Bring up the second activity
            startActivityForResult(intent, BOOK_SUCCESS_REQ_CODE);
        }
        finish();
    }

    public void quantityDecrement(String foodItemId) {
        mFirestore.collection(getResources().getString(R.string.FIREBASE_COLLECTION_FOOD_ITEM_INFO))
                .document(foodItemId).update("count", FieldValue.increment(-1));
    }
    @Override
    protected void onResume() {
        super.onResume();
        UiHandler.getInstance().context = this;
    }

    public void updateConsumerTrans(String userId, String transId) {

        mFirestore.collection(getResources().getString(R.string.FIREBASE_COLLECTION_USER_INFO))
                .document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                String transStr = "transactions";
                String userTableStr = getResources().getString(R.string.FIREBASE_COLLECTION_USER_INFO);
                String transTableStr = getResources().getString(R.string.FIREBASE_COLLECTION_TRANSACTION_INFO);
                if (document.exists()) {
                    UsersInfo usersInfo = document.toObject(UsersInfo.class);
                    Map<String, Object> asConsumer = usersInfo.getAsConsumer();
                    if (asConsumer != null && !asConsumer.isEmpty()) {
                        ArrayList<DocumentReference> transList = (ArrayList<DocumentReference>)
                                asConsumer.get(transStr);
                        DocumentReference transRef = mFirestore.document(transTableStr + "/" + transId);
                        transList.add(transRef);
                        asConsumer.replace(transStr, transList);
                        mFirestore.collection(userTableStr)
                                .document(userId).update("asConsumer", asConsumer);
                    }
                    Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                } else {
                    Log.d("TAG", "No such document");
                }
            }
        });

    }
}
