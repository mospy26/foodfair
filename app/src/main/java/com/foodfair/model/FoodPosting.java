package com.foodfair.model;

import android.media.Image;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Deprecated
public class FoodPosting {

    public Object[] postedBy = new Object[1];
    public List<User> requestedBy;
    public Date foodExpiryDate;
    //I think Size is irrelavant.

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String name;
    public Date postingRemovalDate;
    public String description;
    public String locationAsString;
    public String Longitude;
    public String Latitude;
    public String confirmedFoodName;
    public Image confirmedImage;

    public Object[] getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(Object[] postedBy) {
        this.postedBy = postedBy;
    }

    public List<User> getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(List<User> requestedBy) {
        this.requestedBy = requestedBy;
    }

    public Date getFoodExpiryDate() {
        return foodExpiryDate;
    }

    public void setFoodExpiryDate(Date foodExpiryDate) {
        this.foodExpiryDate = foodExpiryDate;
    }

    public Date getPostingRemovalDate() {
        return postingRemovalDate;
    }

    public void setPostingRemovalDate(Date postingRemovalDate) {
        this.postingRemovalDate = postingRemovalDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocationAsString() {
        return locationAsString;
    }

    public void setLocationAsString(String locationAsString) {
        this.locationAsString = locationAsString;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getConfirmedFoodName() {
        return confirmedFoodName;
    }

    public void setConfirmedFoodName(String confirmedFoodName) {
        this.confirmedFoodName = confirmedFoodName;
    }

    public Image getConfirmedImage() {
        return confirmedImage;
    }

    public void setConfirmedImage(Image confirmedImage) {
        this.confirmedImage = confirmedImage;
    }
}
