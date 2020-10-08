package com.foodfair.holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.foodfair.R;

public class foodItemViewHolder extends RecyclerView.ViewHolder {

    public TextView t1;
    public TextView t2;
    public TextView t3;


    public foodItemViewHolder(@NonNull View itemView) {
        super(itemView);
        t1 = itemView.findViewById(R.id.itemDescription);
        t2 = itemView.findViewById(R.id.userDescription);
        t3 = itemView.findViewById(R.id.dateDue);
    }


}
