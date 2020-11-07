package com.foodfair.ui.hamburger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.foodfair.R;
import com.foodfair.databinding.FragmentHistoryBinding;
import com.foodfair.model.FooditemTransaction;
import com.foodfair.utilities.Const;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class HistoryFragment extends Fragment implements
        HistoryAdapter.OnHistorySelectedListener, View.OnClickListener {

    private static final String TAG = "HistoryFragment";
    private FirebaseFirestore mFirestore;
    private Query mQueryDonor;
    private Query mQueryConsumer;

    private FragmentHistoryBinding mBinding;

    private HistoryAdapter mAdapterDonor;
    private HistoryAdapter mAdapterConsumer;

    private boolean isAsDonor = true;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mBinding = FragmentHistoryBinding.inflate(getLayoutInflater());

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        // Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Change to legit user id
        // String userId = "yXnhEl9OBqgKqHLAPMPV";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        DocumentReference userCriteria = mFirestore.collection(
                getResources().getString(R.string.FIREBASE_COLLECTION_USER_INFO))
                .document(userId);

        mQueryDonor = mFirestore.collection(
                getResources().getString(R.string.FIREBASE_COLLECTION_FOOD_ITEM_TRANSACTION))
                .whereEqualTo(FooditemTransaction.FIELD_DONOR, userCriteria)
                .whereEqualTo(FooditemTransaction.FIELD_STATUS,
                        Const.getInstance().TRANSACTION_STATUS.get("Success"));

        mAdapterDonor = new HistoryAdapter(mQueryDonor, this, true);

        mQueryConsumer = mFirestore.collection(
                getResources().getString(R.string.FIREBASE_COLLECTION_FOOD_ITEM_TRANSACTION))
                .whereEqualTo(FooditemTransaction.FIELD_CONSUMER, userCriteria)
                .whereEqualTo(FooditemTransaction.FIELD_STATUS,
                        Const.getInstance().TRANSACTION_STATUS.get("Success"));

        mAdapterConsumer = new HistoryAdapter(mQueryConsumer, this, false);

        mBinding.historyList.setHasFixedSize(true);
        mBinding.historyList.setAdapter(mAdapterDonor);
        mBinding.historyList.setLayoutManager(new LinearLayoutManager(getActivity()));

        mBinding.btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAsDonor){
                    mBinding.textHistory.setText("Consumption History");
                    mBinding.historyList.setAdapter(mAdapterConsumer);
                    mBinding.historyList.setLayoutManager(new LinearLayoutManager(getActivity()));
                    isAsDonor = false;
                } else {
                    mBinding.textHistory.setText("Donation History");
                    mBinding.historyList.setAdapter(mAdapterDonor);
                    mBinding.historyList.setLayoutManager(new LinearLayoutManager(getActivity()));
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
    public void onHistorySelected(DocumentSnapshot history) {

    }
}
