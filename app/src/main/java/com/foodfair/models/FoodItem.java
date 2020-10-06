package com.foodfair.models;

import android.widget.ImageView;

public class FoodItem {
    private String title;
    private int rating;
    private String imageURL;

    public FoodItem(String title, int rating, String imageURL) {
        this.title = title;
        this.rating = rating;
        this.imageURL = imageURL;
    }

    public String getImageView() {
        return imageURL;
    }

    public void setImageView(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
