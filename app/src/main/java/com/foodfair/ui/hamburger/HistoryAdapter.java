package com.foodfair.ui.hamburger;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.foodfair.R;
import com.foodfair.databinding.AdapterHistoryBinding;
import com.foodfair.model.FoodItemInfo;
import com.foodfair.model.FooditemTransaction;
import com.foodfair.model.ReviewInfo;
import com.foodfair.model.UsersInfo;
import com.foodfair.ui.foodpages.FoodDetailActivity;
import com.foodfair.ui.profiles.UserProfileActivity;
import com.foodfair.utilities.Utility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.Date;

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
        private FirebaseFirestore mFirestore;

        public ViewHolder(AdapterHistoryBinding binding, Context context, boolean isAsDonor){
            super(binding.getRoot());
            this.binding = binding;
            this.context = context;
            if(isAsDonor){
                binding.reviewLayout.setVisibility(View.GONE);
                binding.toFrom.setText("To");
                binding.restaurantItemRating.setIsIndicator(true);
            } else {
                binding.reviewLayout.setVisibility(View.VISIBLE);
                binding.toFrom.setText("From");
                binding.restaurantItemRating.setIsIndicator(false);
            }
            binding.toFromPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(context, UserProfileActivity.class);
//                    context.startActivity(intent);
                    Fragment fragment = new ProfileFragment();
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    activity.getSupportFragmentManager().beginTransaction().replace(
                            R.id.drawer_layout, fragment).addToBackStack(null).commit();

                }
            });
            // Firestore
            mFirestore = FirebaseFirestore.getInstance();

        }

        public void bind(DocumentSnapshot snapshot, OnHistorySelectedListener listener,
                         boolean isAsDonor) {
            FooditemTransaction transaction = snapshot.toObject(FooditemTransaction.class);
            Resources resources = itemView.getResources();

            // Only list status = success
            if(transaction.getStatus() == 2){
                binding.cv.setVisibility(View.VISIBLE);
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
                            binding.foodPhoto.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context, FoodDetailActivity.class);
                                    intent.putExtra("foodId", foodRef.getId());
                                    context.startActivity(intent);
                                }
                            });
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

                binding.reviewSubmitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // submit new review
                        ReviewInfo reviewInfo = new ReviewInfo();
                        reviewInfo.setDate(new Timestamp(new Date()));
                        reviewInfo.setFoodRef(transaction.getFoodRef());
                        reviewInfo.setFromUser(transaction.getConsumer());
                        reviewInfo.setToUser(transaction.getDonor());
                        reviewInfo.setTransactionRef(snapshot.getReference());
                        reviewInfo.setTextReview(binding.reviewText.getText().toString());
                        reviewInfo.setRating(new Double(binding.restaurantItemRating.getRating()));
                        binding.reviewText.setText("");


                        if(transaction.getCdReview() != null){
                            transaction.getCdReview().set(reviewInfo);
                        } else {
                            CollectionReference reviewCollection = mFirestore.collection("reviewInfo");
                            String id = reviewCollection.document().getId();
                            reviewCollection.document(id).set(reviewInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // update review reference to transaction collection
                                    snapshot.getReference().update("cdReview", reviewCollection.document(id));
                                }
                            });
                        }

                    }
                });
                ;
            } else {
                binding.cv.setVisibility(View.GONE);
            }

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
