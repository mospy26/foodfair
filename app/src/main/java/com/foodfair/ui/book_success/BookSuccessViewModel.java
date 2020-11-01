package com.foodfair.ui.book_success;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.foodfair.model.FooditemTransaction;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;

public class BookSuccessViewModel extends ViewModel {
    private MutableLiveData<String> mDonorName = new MutableLiveData<>();
    private MutableLiveData<String> mFoodName = new MutableLiveData<>();
    private MutableLiveData<String> mPickupLocation = new MutableLiveData<>();
    private MutableLiveData<Long> mDeadlineTimeStampToPickup = new MutableLiveData<>();
    private MutableLiveData<LatLng> mLocationLatLng = new MutableLiveData<>();
    private MutableLiveData<String> mQRCodeContent = new MutableLiveData<>();
    private MutableLiveData<String> mFoodUrl = new MutableLiveData<>();


    private MutableLiveData<String> mTransactionId = new MutableLiveData<>();

    public BookSuccessViewModel(String donorName, String foodName, String pickupLocationText,
                                Timestamp transactionStartDate,
                                int transactionAliveSeconds, LatLng latLng, String transactionId, String foodUrl){
        mDonorName.setValue(donorName);
        mFoodName.setValue(foodName);
        mPickupLocation.setValue(pickupLocationText);
        mDeadlineTimeStampToPickup.setValue(transactionStartDate.toDate().getTime()/1000 + transactionAliveSeconds);
        mLocationLatLng.setValue(latLng);
        mQRCodeContent.setValue(transactionId);
        mTransactionId.setValue(transactionId);
        mFoodUrl.setValue(foodUrl);
    }
    public LiveData<String> getDonorName() {
        return mDonorName;
    }

    public LiveData<String> getFoodName() {
        return mFoodName;
    }

    public LiveData<String> getPickupLocation() {
        return mPickupLocation;
    }

    public LiveData<Long> getDeadlineTimeStampToPickup() {
        return mDeadlineTimeStampToPickup;
    }

    public LiveData<LatLng> getLocationLatLng() {
        return mLocationLatLng;
    }

    public LiveData<String> getQRCodeContent() {
        return mQRCodeContent;
    }

    public LiveData<String> getFoodUrl() {
        return mFoodUrl;
    }
    public MutableLiveData<String> getTransactionId() {
        return mTransactionId;
    }

}