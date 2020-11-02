package com.foodfair.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

public class FoodItemInfo {
    ArrayList<Integer> allergyInfo;
    Integer count;
    Timestamp dateExpire;
    Timestamp dateOn;
    DocumentReference donorRef;
    ArrayList<String> imageDescription;
    String name;
    Integer status;
    String textDescription;
    Long type;

    public ArrayList<Integer> getAllergyInfo() {
        return allergyInfo;
    }

    public void setAllergyInfo(ArrayList<Integer> allergyInfo) {
        this.allergyInfo = allergyInfo;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Timestamp getDateExpire() {
        return dateExpire;
    }

    public void setDateExpire(Timestamp dateExpire) {
        this.dateExpire = dateExpire;
    }

    public Timestamp getDateOn() {
        return dateOn;
    }

    public void setDateOn(Timestamp dateOn) {
        this.dateOn = dateOn;
    }

    public DocumentReference getDonorRef() {
        return donorRef;
    }

    public void setDonorRef(DocumentReference donorRef) {
        this.donorRef = donorRef;
    }

    public ArrayList<String> getImageDescription() {
        return imageDescription;
    }

    public void setImageDescription(ArrayList<String> imageDescription) {
        this.imageDescription = imageDescription;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTextDescription() {
        return textDescription;
    }

    public void setTextDescription(String textDescription) {
        this.textDescription = textDescription;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }
}
