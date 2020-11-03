package com.foodfair.model;

import android.location.Location;


import java.io.Serializable;
import java.util.List;

@Deprecated
public class User implements Serializable {


    public String firstName;
    public  String email;
    public List<FoodPosting> foodPostings;
    public List<FoodRequest> foodRequests;
    public List<FoodRecieved> foodRecieveds;
    public List<FoodRequest> pendingConfirmations;


    public User(String firstName, String email, List<FoodPosting> foodPostings, List<FoodRequest> foodRequests, List<FoodRecieved> foodRecieveds) {
        this.firstName = firstName;
        this.email = email;
        this.foodPostings = foodPostings;
        this.foodRequests = foodRequests;
        this.foodRecieveds = foodRecieveds;
    }

    public List<FoodRequest> getFoodRequests() {
        return foodRequests;
    }

    public void setFoodRequests(List<FoodRequest> foodRequests) {
        this.foodRequests = foodRequests;
    }

    public List<FoodRecieved> getFoodRecieveds() {
        return foodRecieveds;
    }

    public void setFoodRecieveds(List<FoodRecieved> foodRecieveds) {
        this.foodRecieveds = foodRecieveds;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<FoodPosting> getFoodPostings() {
        return foodPostings;
    }

    public void setFoodPostings(List<FoodPosting> foodPostings) {
        this.foodPostings = foodPostings;
    }


}
