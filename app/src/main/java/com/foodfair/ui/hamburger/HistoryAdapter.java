package com.foodfair.ui.hamburger;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.foodfair.databinding.AdapterHistoryBinding;
import com.foodfair.model.FoodItemInfo;
import com.foodfair.model.FooditemTransaction;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

public class HistoryAdapter extends FirestoreAdapter<HistoryAdapter.ViewHolder> {

    public interface OnHistorySelectedListener {
        void onHistorySelected(DocumentSnapshot history);
    }

    private OnHistorySelectedListener mListener;

    private static final String TAG = "HistoryAdapter";

    public HistoryAdapter(Query query, OnHistorySelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(AdapterHistoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }


    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private AdapterHistoryBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public ViewHolder(AdapterHistoryBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(DocumentSnapshot snapshot, OnHistorySelectedListener listener) {
            FooditemTransaction transaction = snapshot.toObject(FooditemTransaction.class);
            Resources resources = itemView.getResources();

            DocumentReference foodRef = transaction.getFoodRef();
            foodRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    FoodItemInfo foodItemInfo = documentSnapshot.toObject(FoodItemInfo.class);
                    if(foodItemInfo!= null){
                        Picasso.get()
                                .load(foodItemInfo.getImageDescription().get(0))
                                .into(binding.foodPhoto);
                        binding.personName.setText(foodItemInfo.getName());
                        binding.personAge.setText(foodItemInfo.getDateOn().toString());
                    } else {
                        // TODO: Error messages or something
                    }

                }
            });

            // Click listener
            // in case we want to do something
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onHistorySelected(snapshot);
                    }
                }
            });
        }
    }
}
