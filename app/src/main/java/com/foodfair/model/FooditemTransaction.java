package com.foodfair.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;

public class FooditemTransaction /*implements Parcelable*/ {

    public static final String FIELD_ALIVE_RECORD = "aliveRecord";
    public static final String FIELD_CD_REVIEW = "cdReview";
    public static final String FIELD_DC_REVIEW = "dcReview";
    public static final String FIELD_CONSUMER = "consumer";
    public static final String FIELD_DONOR = "donor";
    public static final String FIELD_FINISH_DATE = "finishDate";
    public static final String FIELD_FOOD_REF = "foodRef";
    public static final String FIELD_OPEN_DATE = "openDate";
    public static final String FIELD_STATUS = "status";

    private Long aliveRecord;
    private DocumentReference cdReview;
    private DocumentReference consumer;
    private DocumentReference dcReview;
    private DocumentReference donor;
    private Timestamp finishDate;
    private DocumentReference foodRef;
    private Timestamp openDate;
    private Long status;

    public Long getAliveRecord() {
        return aliveRecord;
    }

    public void setAliveRecord(Long aliveRecord) {
        this.aliveRecord = aliveRecord;
    }

    public DocumentReference getCdReview() {
        return cdReview;
    }

    public void setCdReview(DocumentReference cdReview) {
        this.cdReview = cdReview;
    }

    public DocumentReference getConsumer() {
        return consumer;
    }

    public void setConsumer(DocumentReference consumer) {
        this.consumer = consumer;
    }

    public DocumentReference getDcReview() {
        return dcReview;
    }

    public void setDcReview(DocumentReference dcReview) {
        this.dcReview = dcReview;
    }

    public DocumentReference getDonor() {
        return donor;
    }

    public void setDonor(DocumentReference donor) {
        this.donor = donor;
    }

    public Timestamp getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Timestamp finishDate) {
        this.finishDate = finishDate;
    }

    public DocumentReference getFoodRef() {
        return foodRef;
    }

    public void setFoodRef(DocumentReference foodRef) {
        this.foodRef = foodRef;
    }

    public Timestamp getOpenDate() {
        return openDate;
    }

    public void setOpenDate(Timestamp openDate) {
        this.openDate = openDate;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeLong(aliveRecord);
//        openDate.writeToParcel(parcel, i);
//        finishDate.writeToParcel(parcel, i);
//        parcel.writeLong(status);
//        // fetch other data manually...
//    }
}
