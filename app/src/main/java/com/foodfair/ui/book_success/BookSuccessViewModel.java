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
    MutableLiveData<String> mDonorName = new MutableLiveData<>();
    MutableLiveData<String> mFoodName = new MutableLiveData<>();
    MutableLiveData<String> mPickupLocation = new MutableLiveData<>();
    MutableLiveData<LatLng> mLocationLatLng = new MutableLiveData<>();
    MutableLiveData<String> mQRCodeContent = new MutableLiveData<>();
    MutableLiveData<String> mFoodUrl = new MutableLiveData<>();
    MutableLiveData<Timestamp> mTransactionStartDate = new MutableLiveData<>();
    MutableLiveData<Long> mTransactionLiveSeconds = new MutableLiveData<>();
    MutableLiveData<Long> mDeadlineTimeStampToPickup = new MutableLiveData<>();

    // for testing
    MutableLiveData<Bitmap> mFoodBitmap = new MutableLiveData<>();


    private MutableLiveData<String> mTransactionId = new MutableLiveData<>();
    BookSuccessViewModel(){}
    public BookSuccessViewModel(Bitmap foodSample) {
        // Fake data
        // todo until firebase structure completes, remove these codes
        mDonorName.setValue("Mild Grass Hotpot Restaurant");
        mFoodName.setValue("Large Size Beef Hotpot");
        mPickupLocation.setValue("1/102-108 Hay St, Haymarket NSW 2000");
        mDeadlineTimeStampToPickup.setValue(new Date().getTime()/1000 + 3600 + 60*2 + 32);
        mLocationLatLng.setValue(new LatLng(-33.8904925, 151.1203401));
        mQRCodeContent.setValue("It is the content of the qrcode");
        mFoodBitmap.setValue(foodSample);
    }
    public BookSuccessViewModel(String donorName, String foodName, String pickupLocationText,
                                Timestamp transactionStartDate,
                                Long transactionAliveSeconds, LatLng latLng, String transactionId, String foodUrl){
        mDonorName.setValue(donorName);
        mFoodName.setValue(foodName);
        mPickupLocation.setValue(pickupLocationText);
        mTransactionStartDate.setValue(transactionStartDate);
        mTransactionLiveSeconds.setValue(transactionAliveSeconds);
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