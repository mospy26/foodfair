package com.foodfair.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;

@Deprecated
public class FoodRecieved {
    private DocumentReference receiver;
    private Timestamp receivedDate;
    private DocumentReference foodRef;
    private DocumentReference transactionRef;

    public DocumentReference getReceiver() {
        return receiver;
    }

    public void setReceiver(DocumentReference receiver) {
        this.receiver = receiver;
    }

    public Timestamp getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Timestamp receivedDate) {
        this.receivedDate = receivedDate;
    }

    public DocumentReference getFoodRef() {
        return foodRef;
    }

    public void setFoodRef(DocumentReference foodRef) {
        this.foodRef = foodRef;
    }

    public DocumentReference getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(DocumentReference transactionRef) {
        this.transactionRef = transactionRef;
    }
}
