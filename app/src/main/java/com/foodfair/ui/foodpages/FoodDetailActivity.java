package com.foodfair.ui.foodpages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.foodfair.ui.profiles.UserProfileViewModel;
import com.foodfair.utilities.Const;
import com.foodfair.utilities.Utility;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class FoodDetailActivity extends AppCompatActivity {


    // !!!!!! Assume we have user ID!!!
    private static final String UID = "yXnhEl9OBqgKqHLAPMPV";
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

    private FirebaseFirestore mFirestore;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirestore = FirebaseFirestore.getInstance();
        setContentView(R.layout.food_item);
        foodDetailModel = new FoodDetailModel();
        InitUI();
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        userId = user.getUid();
        foodId = "8g51f4M0j881nqW0v59o";
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
        String email = getResources().getString(R.string.firebase_email);
        String password = getResources().getString(R.string.firebase_password);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.isSignInWithEmailLink(email)) {
            fetchDBInfo(foodId);
        } else {
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            fetchDBInfo(foodId);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                        }
                    });
        }
    }
    private void fetchDBInfo(String userId) {
        mFirestore.collection(getResources().getString(R.string.FIREBASE_COLLECTION_FOOD_ITEM_INFO)).document(foodId).get().addOnCompleteListener(task -> {
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
            foodDetailModel.donorAddress.setValue(donorUserInfo.getLocation());
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
        foodDetailModel.foodQuantity.observe(this, foodQuantity->{
            foodNameTextView.setText(Long.toString(foodQuantity));
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
        DocumentReference foodRef = mFirestore.document(foodTableStr + "/" + mFoodItemID);
        Timestamp openDate = Utility.parseDateTime(Utility.getCurrentTimeStr(), aConst.DATE_TIME_PATTERN);

        populateFoodItemTransaction(aConst.ALIVE_RECORD, null, consumerRef, null,
                donorRef, null, foodRef, openDate, bookStatus, foodItemTransaction);

        postFoodTransactionToFirebase(foodItemTransaction);
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
        transCollect.document().set(foodItemTransaction)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

}
