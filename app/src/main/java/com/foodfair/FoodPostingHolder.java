package com.foodfair;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FoodPostingHolder extends RecyclerView.ViewHolder {

     TextView foodTitle;
     TextView foodDesc;

    public FoodPostingHolder(@NonNull View itemView) {
        super(itemView);
        foodTitle = itemView.findViewById(R.id.foodName);
        foodDesc = itemView.findViewById(R.id.foodDetail);
    }


}
