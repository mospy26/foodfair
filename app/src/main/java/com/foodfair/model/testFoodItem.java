package com.foodfair.model;

import java.io.Serializable;


/*
DO NOT USE THIS CLASS IN PRODUCTION, FOR TESTING PURPOSES I HAVE DEVELOPED THIS CLASS PLEASE USE FOODPOSTING TO DEFINE AND DISPLAY A POSTING
OBJECT IN THE UI, THIS CLASS WILL BE DELETED SOON.
 */
public class testFoodItem implements Serializable {

    public String description;
    public String rating;
    public String title;



    public testFoodItem()
    {

    }

    public testFoodItem(String description, String rating, String title) {
        this.description = description;
        this.rating = rating;
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
