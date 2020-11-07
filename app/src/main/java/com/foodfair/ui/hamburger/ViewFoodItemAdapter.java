package com.foodfair.ui.hamburger;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.foodfair.databinding.AdapterBookingBinding;
import com.foodfair.databinding.AdapterViewFoodBinding;
import com.foodfair.model.FoodItemInfo;
import com.foodfair.utilities.Utility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

public class ViewFoodItemAdapter extends FirestoreAdapter<ViewFoodItemAdapter.ViewHolder>{

    public ViewFoodItemAdapter(Query query, OnViewItemSelectedListener listener) {
        super(query);
        this.mListener = listener;
    }

    public interface OnViewItemSelectedListener {
        void onManageFoodSelected(DocumentSnapshot snapshot);
    }
    private OnViewItemSelectedListener mListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewFoodItemAdapter.ViewHolder(AdapterViewFoodBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private AdapterViewFoodBinding binding;
        private FirebaseFirestore mFirestore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public ViewHolder(AdapterViewFoodBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            mFirestore = FirebaseFirestore.getInstance();
        }

        public void bind(DocumentSnapshot snapshot, OnViewItemSelectedListener listener) {
            FoodItemInfo foodItemInfo = snapshot.toObject(FoodItemInfo.class);
            Resources resources = itemView.getResources();

            Picasso.get().load(foodItemInfo.getImageDescription().get(0))
                    .into(binding.foodPhoto);
            binding.foodNameFood.setText(foodItemInfo.getName());
            binding.quantity.setText("Quantity: " + foodItemInfo.getCount());
            binding.expiryDate.setText("Expire: " + Utility.timeStampToDateString(foodItemInfo.getDateExpire()));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onManageFoodSelected(snapshot);
                    }
//                    Intent intent =  new Intent(context, UserProfileActivity.class);
                    // get user id information and send before starting
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String userId = user.getUid();
//                    intent.putExtra("userId", userId);
//                    context.startActivity(intent);
                }
            });
        }
    }
}
