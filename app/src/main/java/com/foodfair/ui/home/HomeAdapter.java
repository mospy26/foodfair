package com.foodfair.ui.home;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.foodfair.databinding.FoodpostingBinding;
import com.foodfair.model.FoodItemInfo;
import com.foodfair.model.UsersInfo;
import com.foodfair.ui.foodpages.FoodDetailActivity;
import com.foodfair.ui.hamburger.FirestoreAdapter;
import com.foodfair.utilities.Utility;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

public class HomeAdapter extends FirestoreAdapter<HomeAdapter.ViewHolder> {

    public HomeAdapter(Query query, OnHomeItemSelectedListener listener) {
        super(query);
        this.mListener = listener;
    }

    public interface OnHomeItemSelectedListener {
        void onHomeItemSelected(DocumentSnapshot snapshot);
    }
    private OnHomeItemSelectedListener mListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(FoodpostingBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private FoodpostingBinding binding;
        private FirebaseFirestore mFirestore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public ViewHolder(FoodpostingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            mFirestore = FirebaseFirestore.getInstance();
        }

        public void bind(DocumentSnapshot snapshot, OnHomeItemSelectedListener listener) {
            FoodItemInfo foodItemInfo = snapshot.toObject(FoodItemInfo.class);

            Picasso.get().load(foodItemInfo.getImageDescription().get(0))
                    .resize(230, 230)
                    .onlyScaleDown().centerCrop().into(binding.foodImage);
            binding.foodNameHome.setText(foodItemInfo.getName());
            binding.postedDate.setText("Date: " +
                    Utility.timeStampToDateString(foodItemInfo.getDateOn()));

//            binding.posting.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //Start Food Detail Activity
//                    Intent intent = new Intent(itemView.getContext(), FoodDetailActivity.class);
//                    intent.putExtra("foodId", snapshot.getReference().getId());
//                    itemView.getContext().startActivity(intent);
//                }
//            });

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onHomeItemSelected(snapshot);
                    }
                }
            });

            DocumentReference donorRef = foodItemInfo.getDonorRef();
            donorRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot snapshot) {
                    UsersInfo donorInfo = snapshot.toObject(UsersInfo.class);
                    binding.userPosted.setText("Posted by: " + donorInfo.getName());
                }
            });


        }
    }
}
