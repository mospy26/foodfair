package com.foodfair.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

public class FoodItemInfo /*implements Parcelable*/ {
    ArrayList<Long> allergyInfo;
    Integer count;
    Timestamp dateExpire;
    Timestamp dateOn;
    DocumentReference donorRef;
    ArrayList<String> imageDescription;
    String name;
    Integer status;
    String textDescription;
    Long type;

    public ArrayList<Long> getAllergyInfo() {
        return allergyInfo;
    }

    public void setAllergyInfo(ArrayList<Long> allergyInfo) {
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
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeString(name);
//        dateExpire.writeToParcel(parcel, i);
//        dateOn.writeToParcel(parcel, i);
//        parcel.writeInt(count);
//        parcel.writeInt(status);
//        parcel.writeString(textDescription);
//    }
}