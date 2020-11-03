package com.foodfair.ui.qr_success;

import androidx.lifecycle.MutableLiveData;

import com.foodfair.model.FoodItemInfo;
import com.foodfair.model.FooditemTransaction;
import com.foodfair.model.ReviewInfo;
import com.foodfair.model.UsersInfo;

public class QRSuccessViewModel {
    MutableLiveData<FoodItemInfo> foodItemInfo = new MutableLiveData<>();
    MutableLiveData<UsersInfo> consumerUserInfo = new MutableLiveData<>();
    MutableLiveData<UsersInfo> donorUserInfo = new MutableLiveData<>();
    MutableLiveData<FooditemTransaction> foodItemTransaction = new MutableLiveData<>();
}
