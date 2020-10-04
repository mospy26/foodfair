package com.foodfair.ui.book_success;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;

public class BookSuccessViewModel extends ViewModel {

    private MutableLiveData<String> mDonorName = new MutableLiveData<>();
    private MutableLiveData<String> mFoodName = new MutableLiveData<>();
    private MutableLiveData<String> mPickupLocation = new MutableLiveData<>();
    private MutableLiveData<Long> mDeadlineTimeStampToPickup = new MutableLiveData<>();
    private MutableLiveData<LatLng> mLocationLatLng = new MutableLiveData<>();
    private MutableLiveData<String> mQRCodeContent = new MutableLiveData<>();
    private MutableLiveData<Bitmap> mFoodBitmap = new MutableLiveData<>();



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

    public LiveData<Bitmap> getFoodBitmap() {
        return mFoodBitmap;
    }

}