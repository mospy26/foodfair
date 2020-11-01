package com.foodfair.ui.hamburger;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.foodfair.databinding.AdapterHistoryBinding;
import com.foodfair.model.FoodItemInfo;
import com.foodfair.model.FooditemTransaction;
import com.foodfair.model.UsersInfo;
import com.foodfair.ui.profiles.UserProfileActivity;
import com.foodfair.utilities.Utility;
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

    public boolean isAsDonor = true;

    public HistoryAdapter(Query query, OnHistorySelectedListener listener, boolean isAsDonor) {
        super(query);
        mListener = listener;
        this.isAsDonor = isAsDonor;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(AdapterHistoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false),
                parent.getContext(), isAsDonor);
    }


    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener, isAsDonor);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private AdapterHistoryBinding binding;
        private Context context;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public ViewHolder(AdapterHistoryBinding binding, Context context, boolean isAsDonor){
            super(binding.getRoot());
            this.binding = binding;
            if(isAsDonor){
                binding.reviewLayout.setVisibility(View.GONE);
                binding.toFrom.setText("To");
            } else {
                binding.reviewLayout.setVisibility(View.VISIBLE);
                binding.toFrom.setText("From");
            }
            binding.toFromPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    context.startActivity(intent);
                }
            });
        }

        public void bind(DocumentSnapshot snapshot, OnHistorySelectedListener listener,
                         boolean isAsDonor) {
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
                        binding.foodName.setText(foodItemInfo.getName());
                        binding.quantity.setText("Quantity: " + foodItemInfo.getCount());
                        binding.donationDate.setText(
                                Utility.timeStampToDateString(transaction.getFinishDate()));
                    } else {
                        // TODO: Error messages or something
                    }

                }
            });

            DocumentReference userRef;
            if(isAsDonor){
                userRef = transaction.getConsumer();
            } else {
                userRef = transaction.getDonor();
            }
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    UsersInfo usersInfo = documentSnapshot.toObject(UsersInfo.class);
                    if(usersInfo != null){
                        Picasso.get()
                                .load(usersInfo.getProfileImage())
                                .into(binding.toFromPhoto);
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
