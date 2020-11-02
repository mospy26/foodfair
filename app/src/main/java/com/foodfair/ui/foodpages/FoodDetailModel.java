package com.foodfair.ui.foodpages;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.foodfair.model.FoodItemInfo;
import com.foodfair.model.ReviewInfo;
import com.foodfair.model.UsersInfo;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;

import java.sql.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

public class FoodDetailModel extends ViewModel {
    MutableLiveData<FoodItemInfo> currentFoodDetailInfo = new MutableLiveData<>();
    MutableLiveData<UsersInfo> donorUserInfo = new MutableLiveData<>();


    // ------------
    MutableLiveData<String> foodImageUrl = new MutableLiveData<>();
    MutableLiveData<String> foodName = new MutableLiveData<>();
    MutableLiveData<ArrayList<Long>> allergy = new MutableLiveData<>();
    MutableLiveData<Timestamp> foodDateOn = new MutableLiveData<>();
    MutableLiveData<Timestamp> foodDateExpire = new MutableLiveData<>();
    MutableLiveData<Long> foodQuantity = new MutableLiveData<>();
    MutableLiveData<String> donorName = new MutableLiveData<>();
    MutableLiveData<String> donorAddress = new MutableLiveData<>();
    MutableLiveData<String> foodTextDescription = new MutableLiveData<>();
}

