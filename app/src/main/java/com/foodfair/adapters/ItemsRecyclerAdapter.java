package com.foodfair.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.foodfair.R;
import com.foodfair.models.FoodItem;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class ItemsRecyclerAdapter extends FirestoreAdapter<ItemsRecyclerAdapter.CardViewHolder> {

    public ItemsRecyclerAdapter(Query query) {
        super(query);
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e("HAHAHAH", "createddddddddd");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.homepage_card_view, parent, false);
        return new CardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        holder.bind(getSnapshot(position));
    }

    @Override
    public int getItemCount() {
        return 0;
    }



    public static class CardViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        ImageView imageView;
        TextView title;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view_item);
//            imageView = cardView.findViewById(R.id.image_view_card_item);
            title = cardView.findViewById(R.id.text_view_title_card_item);
        }

        public void bind(DocumentSnapshot snapshot) {
            FoodItem item = snapshot.toObject(FoodItem.class);
            title.setText(item.getTitle());
        }
    }
}
