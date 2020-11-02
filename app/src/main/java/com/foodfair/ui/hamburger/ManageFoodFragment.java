package com.foodfair.ui.hamburger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.foodfair.R;
import com.foodfair.databinding.FragmentManageFoodBinding;
import com.foodfair.model.FooditemTransaction;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ManageFoodFragment extends Fragment implements
        ManageFoodAdapter.OnManageFoodSelectedListener, View.OnClickListener {

    private static final String TAG = "ManageFoodFragment";
    private FirebaseFirestore mFirestore;
    private Query mQueryDonor;
    private Query mQueryConsumer;

    private FragmentManageFoodBinding mBinding;

    private ManageFoodAdapter mAdapterConsumer;
    private ManageFoodAdapter mAdapterDonor;

    private boolean isAsDonor = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mBinding = FragmentManageFoodBinding.inflate(getLayoutInflater());

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        // Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // TODO: Change to legit user id
        String userId = "yXnhEl9OBqgKqHLAPMPV";

        DocumentReference userCriteria = mFirestore.collection(
                getResources().getString(R.string.FIREBASE_COLLECTION_USER_INFO))
                .document(userId);

        mQueryConsumer = mFirestore.collection(
                getResources().getString(R.string.FIREBASE_COLLECTION_FOOD_ITEM_TRANSACTION))
                .whereEqualTo(FooditemTransaction.FIELD_CONSUMER, userCriteria);

        mAdapterConsumer = new ManageFoodAdapter(mQueryConsumer, this, false);

        mQueryDonor = mFirestore.collection(
                getResources().getString(R.string.FIREBASE_COLLECTION_FOOD_ITEM_TRANSACTION))
                .whereEqualTo(FooditemTransaction.FIELD_DONOR, userCriteria);

        mAdapterDonor = new ManageFoodAdapter(mQueryDonor, this, true);

        mBinding.bookingList.setAdapter(mAdapterConsumer);
        mBinding.bookingList.setLayoutManager(new LinearLayoutManager(getActivity()));

        mBinding.btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAsDonor){
                    mBinding.textManageFood.setText("Your Bookings");
                    mBinding.bookingList.setAdapter(mAdapterConsumer);
                    mBinding.bookingList.setLayoutManager(new LinearLayoutManager(getActivity()));
                    mBinding.btnSwitch.setText("Switch to pending requests");
                    isAsDonor = false;
                } else {
                    mBinding.textManageFood.setText("Your Pending Requests");
                    mBinding.bookingList.setAdapter(mAdapterDonor);
                    mBinding.bookingList.setLayoutManager(new LinearLayoutManager(getActivity()));
                    mBinding.btnSwitch.setText("Switch to your bookings");
                    isAsDonor = true;
                }

            }
        });

        return mBinding.getRoot();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Start listening for Firestore updates
        if (mAdapterDonor != null) {
            mAdapterDonor.startListening();
        }
        if (mAdapterConsumer != null) {
            mAdapterConsumer.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapterDonor != null) {
            mAdapterDonor.stopListening();
        }
        if (mAdapterConsumer != null) {
            mAdapterConsumer.stopListening();
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onManageFoodSelected() {

    }
}