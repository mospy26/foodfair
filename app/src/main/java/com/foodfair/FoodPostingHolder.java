package com.foodfair;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FoodPostingHolder extends RecyclerView.ViewHolder {

    public TextView foodTitle;
    public TextView foodPostedBy;
    public TextView foodPostedDate;
    public ImageView imageToDisplay;
    public String foodItemId;


    public FoodPostingHolder(@NonNull View itemView) {
        super(itemView);
        foodTitle = itemView.findViewById(R.id.food_name_home);
        foodPostedBy = itemView.findViewById(R.id.userPosted);
        foodPostedDate = itemView.findViewById(R.id.postedDate);
        imageToDisplay = itemView.findViewById(R.id.foodImage);
    }


}
