package com.foodfair.model;

import com.google.firebase.firestore.DocumentReference;

public class Ranking {

    public static final String FIELD_AVERAGE_RATING = "averageRating";
    public static final String FIELD_DONATION_COUNT = "donationCount";
    public static final String FIELD_DONOR = "donor";
    public static final String FIELD_POSITION = "position";

    private Double averageRating;
    private Long donationCount;
    private Long score;
    private DocumentReference donor;
    private Long position;

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Long getDonationCount() {
        return donationCount;
    }

    public void setDonationCount(Long donationCount) {
        this.donationCount = donationCount;
    }

    public DocumentReference getDonor() {
        return donor;
    }

    public void setDonor(DocumentReference donor) {
        this.donor = donor;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }
}
