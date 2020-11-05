package com.foodfair.ui.hamburger;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.foodfair.databinding.AdapterBookingBinding;
import com.foodfair.model.FoodItemInfo;
import com.foodfair.model.FooditemTransaction;
import com.foodfair.model.UsersInfo;
import com.foodfair.utilities.Const;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

public class ManageFoodAdapter extends FirestoreAdapter<ManageFoodAdapter.ViewHolder>{

    private boolean isAsDonor = true;

    public interface OnManageFoodSelectedListener {
        void onManageFoodSelected();
    }
    private OnManageFoodSelectedListener mListener;

    public ManageFoodAdapter(Query query, OnManageFoodSelectedListener listener, boolean isAsDonor) {
        super(query);
        mListener = listener;
        this.isAsDonor = isAsDonor;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(AdapterBookingBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false),
                isAsDonor);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener, isAsDonor);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private AdapterBookingBinding binding;
        private FirebaseFirestore mFirestore;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public ViewHolder(AdapterBookingBinding binding, boolean isAsDonor) {
            super(binding.getRoot());
            this.binding = binding;
            if(isAsDonor){
                binding.confirmButtonLayout.setVisibility(View.VISIBLE);
                binding.confirmText.setVisibility(View.GONE);
            } else {
                binding.confirmButtonLayout.setVisibility(View.GONE);
                binding.confirmText.setVisibility(View.VISIBLE);
            }

            mFirestore = FirebaseFirestore.getInstance();
        }

        public void bind(DocumentSnapshot snapshot, OnManageFoodSelectedListener listener,
                         boolean isAsDonor) {
            FooditemTransaction transaction = snapshot.toObject(FooditemTransaction.class);
            Resources resources = itemView.getResources();

            // Only list status = booked
            if(transaction.getStatus() == 0){
                binding.cvFood.setVisibility(View.VISIBLE);
                binding.cvUser.setVisibility(View.VISIBLE);

                // Get user info - gets who is requesting or who is donating
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
                        Picasso.get().load(usersInfo.getProfileImage())
                                .into(binding.profileImage);
                        String userRequest;
                        if(isAsDonor){
                            userRequest = usersInfo.getName() + " has requested to book";
                        } else {
                            userRequest = "You have requested " + usersInfo.getName() + " for";
                        }
                        binding.userRequestInfo.setText(userRequest);
                    }
                });

                // Get food info
                DocumentReference foodRef = transaction.getFoodRef();
                foodRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        FoodItemInfo foodItemInfo = documentSnapshot.toObject(FoodItemInfo.class);
                        Picasso.get().load(foodItemInfo.getImageDescription().get(0))
                                .into(binding.foodPhoto);
                        binding.foodNameFood.setText(foodItemInfo.getName());
                        binding.quantity.setText("Quantity: " + foodItemInfo.getCount());
                    }
                });

                binding.bookingConfirmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Update firebase - transaction success
                        snapshot.getReference().update(FooditemTransaction.FIELD_STATUS,
                                Const.getInstance().TRANSACTION_STATUS.get("Success"));
                    }
                });

                binding.bookingDeclineBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Update firebase - remove transaction
                        snapshot.getReference().update(FooditemTransaction.FIELD_STATUS,
                                Const.getInstance().TRANSACTION_STATUS.get("Cancelled"));
                    }
                });

            } else {
                binding.cvFood.setVisibility(View.GONE);
                binding.cvUser.setVisibility(View.GONE);
            }
        }

    }
}
