package com.foodfair.ui.hamburger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.foodfair.R;
import com.foodfair.databinding.FragmentViewFoodBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ViewFoodItemFragment extends Fragment implements
        ViewFoodItemAdapter.OnViewItemSelectedListener, View.OnClickListener {

    private static final String TAG = "ViewFoodItemFragment";
    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private FragmentViewFoodBinding mBinding;

    private ViewFoodItemAdapter mAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentViewFoodBinding.inflate(getLayoutInflater());
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

        mQuery = mFirestore.collection("foodItemInfo")
                .whereEqualTo("donorRef", userCriteria)
                .whereGreaterThan("count", 0);

        mAdapter = new ViewFoodItemAdapter(mQuery, this);

        mBinding.foodList.setAdapter(mAdapter);
        mBinding.foodList.setLayoutManager(new LinearLayoutManager(getActivity()));


        return mBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mAdapter != null){
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAdapter != null){
            mAdapter.stopListening();
        }
    }

    @Override
    public void onManageFoodSelected(DocumentSnapshot snapshot) {

    }

    @Override
    public void onClick(View v) {

    }
}
