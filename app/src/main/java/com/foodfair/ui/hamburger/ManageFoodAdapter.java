package com.foodfair.ui.hamburger;

import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.foodfair.R;
import com.foodfair.databinding.AdapterBookingBinding;
import com.foodfair.model.FoodItemInfo;
import com.foodfair.model.FooditemTransaction;
import com.foodfair.model.Ranking;
import com.foodfair.model.UsersInfo;
import com.foodfair.utilities.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Document;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ManageFoodAdapter extends FirestoreAdapter<ManageFoodAdapter.ViewHolder>{

    private boolean isAsDonor = true;
    private Ranking ranking;
    private String period;

    public interface OnManageFoodSelectedListener {
        void onManageFoodSelected(DocumentSnapshot snapshot);
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
                        Map<String, Object> updateMap = new HashMap<String, Object>();
                        updateMap.put(FooditemTransaction.FIELD_STATUS,
                                Const.getInstance().TRANSACTION_STATUS.get("Success"));
                        updateMap.put(FooditemTransaction.FIELD_FINISH_DATE,
                                Timestamp.now());
                        snapshot.getReference().update(updateMap);
                        foodRef.update("status",
                                Const.getInstance().TRANSACTION_STATUS.get("success"));
                        updateLeaderboard(transaction);
                    }
                });

                binding.bookingDeclineBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Update firebase - remove transaction
                        snapshot.getReference().update(FooditemTransaction.FIELD_STATUS,
                                Const.getInstance().TRANSACTION_STATUS.get("Cancelled"));
                        foodRef.update("count", FieldValue.increment(1));
                    }
                });

            } else {
                binding.cvFood.setVisibility(View.GONE);
                binding.cvUser.setVisibility(View.GONE);
            }
        }

    }

    private String _getPeriod() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        int month = cal.get(Calendar.MONTH) + 1;
        period = cal.get(Calendar.YEAR) + "" + month;
        return period;
    }

    private void updateLeaderboard(FooditemTransaction transaction) {
        CollectionReference leaderboardRef = FirebaseFirestore.getInstance().collection(
                "leaderboard");
        leaderboardRef
                .document(_getPeriod())
                .collection("ranking")
                .document(FirebaseAuth.getInstance().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ranking = document.toObject(Ranking.class);
                        ranking.setScore(ranking.getScore() + 100);
                    } else {
                        ranking = new Ranking();
                        ranking.setDonationCount(1L);
                        ranking.setAverageRating(2.9999);
                        ranking.setScore(100L);
                        ranking.setDonor(transaction.getDonor());
                    }
                    saveRanking();
                }
            }

        });
    }

    public void saveRanking() {
        CollectionReference leaderboardRef = FirebaseFirestore.getInstance().collection(
                "leaderboard");
        FirebaseFirestore.getInstance().collection(("leaderboard"))
                .document(period)
                .collection("ranking")
                .document(FirebaseAuth.getInstance().getUid()).set(ranking).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("Leaderboard", "Success updating leaderboard");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Leaderboard", "Failed to update ranking");
            }
        });
    }
}
