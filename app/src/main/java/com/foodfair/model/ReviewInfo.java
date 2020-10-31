package com.foodfair.model;

import android.widget.ListView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReviewInfo {
    Timestamp date;
    DocumentReference foodRef;
    DocumentReference fromUser;
    ArrayList<String> imageReviews;
    Double rating;
    String textReview;
    DocumentReference toUser;
    DocumentReference transactionRef;
    Long type;

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public DocumentReference getFoodRef() {
        return foodRef;
    }

    public void setFoodRef(DocumentReference foodRef) {
        this.foodRef = foodRef;
    }

    public DocumentReference getFromUser() {
        return fromUser;
    }

    public void setFromUser(DocumentReference fromUser) {
        this.fromUser = fromUser;
    }

    public ArrayList<String> getImageReviews() {
        return imageReviews;
    }

    public void setImageReviews(ArrayList<String> imageReviews) {
        this.imageReviews = imageReviews;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getTextReview() {
        return textReview;
    }

    public void setTextReview(String textReview) {
        this.textReview = textReview;
    }

    public DocumentReference getToUser() {
        return toUser;
    }

    public void setToUser(DocumentReference toUser) {
        this.toUser = toUser;
    }

    public DocumentReference getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(DocumentReference transactionRef) {
        this.transactionRef = transactionRef;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }



}
