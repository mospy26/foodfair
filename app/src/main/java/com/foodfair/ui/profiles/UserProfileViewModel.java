package com.foodfair.ui.profiles;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.foodfair.model.FoodItemInfo;
import com.foodfair.model.ReviewInfo;
import com.foodfair.model.UsersInfo;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.sql.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

public class UserProfileViewModel extends ViewModel {
    MutableLiveData<UsersInfo> currentUserInfo = new MutableLiveData<>();
    MutableLiveData<ReviewInfo> consumerReviewInfo = new MutableLiveData<>();
    MutableLiveData<UsersInfo> consumerReviewDonorUserInfo = new MutableLiveData<>();
    MutableLiveData<FoodItemInfo> consumerReviewFoodInfo = new MutableLiveData<>();

    MutableLiveData<FoodItemInfo> donorOnShelfFoodInfo = new MutableLiveData<>();
    MutableLiveData<ReviewInfo> donorReviewedInfo = new MutableLiveData<>();
    MutableLiveData<UsersInfo> donorReviewedUserInfo = new MutableLiveData<>();
    MutableLiveData<FoodItemInfo> donorReviewedFoodInfo = new MutableLiveData<>();


    // ------------
    MutableLiveData<String> profileImageUrl = new MutableLiveData<>();
    MutableLiveData<String> name = new MutableLiveData<>();
    MutableLiveData<Integer> gender = new MutableLiveData<>();
    MutableLiveData<String> id = new MutableLiveData<>();
    MutableLiveData<Timestamp> birthday = new MutableLiveData<>();
    MutableLiveData<String> bio = new MutableLiveData<>();
    MutableLiveData<ArrayList<Long>> allergy = new MutableLiveData<>();
    MutableLiveData<Long> preference = new MutableLiveData<>();
    MutableLiveData<GeoPoint> location = new MutableLiveData<>();
    MutableLiveData<Timestamp> joinDate = new MutableLiveData<>();
    MutableLiveData<Timestamp> lastLoginDate = new MutableLiveData<>();
    // As Consumer
    // Review
    MutableLiveData<Integer> asConsumerTotalReviewCount = new MutableLiveData<>();
    MutableLiveData<String> asConsumerReviewDonorName = new MutableLiveData<>();
    MutableLiveData<String> asConsumerReviewFoodName = new MutableLiveData<>();
    MutableLiveData<Timestamp> asConsumerReviewDate = new MutableLiveData<>();
    MutableLiveData<Double> asConsumerReviewRating = new MutableLiveData<>();
    MutableLiveData<ArrayList<String>> asConsumerReviewImageUrls = new MutableLiveData<>();
    MutableLiveData<String> asConsumerReviewText = new MutableLiveData<>();
    // Badge
    MutableLiveData<Integer> asConsumerGotBadgeCount = new MutableLiveData<>();
    MutableLiveData<Integer> asConsumerTotalBadgeCount = new MutableLiveData<>();
    MutableLiveData<ArrayList<Number>> asConsumerBadges = new MutableLiveData<>();

    // As Donor
    // On the shelf
    MutableLiveData<Integer> asDonorTotalOnShelfCount = new MutableLiveData<>();
    MutableLiveData<String> asDonorFoodName = new MutableLiveData<>();
    MutableLiveData<Timestamp> asDonorFoodDateOn = new MutableLiveData<>();
    MutableLiveData<Timestamp> asDonorFoodDateExpire = new MutableLiveData<>();
    MutableLiveData<ArrayList<String>> asDonorFoodImageUrls = new MutableLiveData<>();
    MutableLiveData<String> asDonorFoodTextDescription = new MutableLiveData<>();
    // Reviewed
    MutableLiveData<Integer> asDonorTotalReviewedCount = new MutableLiveData<>();
    MutableLiveData<String> asDonorReviewedConsumerName = new MutableLiveData<>();
    MutableLiveData<String> asDonorReviewedFoodName = new MutableLiveData<>();
    MutableLiveData<Timestamp> asDonorReviewedDate = new MutableLiveData<>();
    MutableLiveData<Double> asDonorReviewedRating = new MutableLiveData<>();
    MutableLiveData<ArrayList<String>> asDonorReviewedImageUrls = new MutableLiveData<>();
    MutableLiveData<String> asDonorReviewedText = new MutableLiveData<>();
    // Badge
    MutableLiveData<Integer> asDonorGotBadgeCount = new MutableLiveData<>();
    MutableLiveData<Integer> asDonorTotalBadgeCount = new MutableLiveData<>();
    MutableLiveData<ArrayList<Number>> asDonorBadges = new MutableLiveData<>();
}
