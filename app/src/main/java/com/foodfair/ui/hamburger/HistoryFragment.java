package com.foodfair.ui.hamburger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.foodfair.R;
import com.foodfair.databinding.FragmentHistoryBinding;
import com.foodfair.model.FooditemTransaction;
import com.foodfair.utilities.Const;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class HistoryFragment extends Fragment implements
        HistoryAdapter.OnHistorySelectedListener, View.OnClickListener {

    private static final String TAG = "HistoryFragment";
    private HistoryViewModel historyViewModel;
    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private FragmentHistoryBinding mBinding;

    private HistoryAdapter mAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        historyViewModel =
                ViewModelProviders.of(this).get(HistoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_history, container, false);
        final TextView textView = root.findViewById(R.id.text_history);
        historyViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        mBinding = FragmentHistoryBinding.inflate(getLayoutInflater());

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        // Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // TODO: Change to legit user id
        String userId = "yXnhEl9OBqgKqHLAPMPV";

        DocumentReference userCriteria = mFirestore.collection(
                getResources().getString(R.string.FIREBASE_COLLECTION_USER_INFO))
                .document(userId);

        mQuery = mFirestore.collection(
                getResources().getString(R.string.FIREBASE_COLLECTION_FOOD_ITEM_TRANSACTION))
                .whereEqualTo(FooditemTransaction.FIELD_DONOR, userCriteria);

        mAdapter = new HistoryAdapter(mQuery, this){
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    mBinding.historyList.setVisibility(View.GONE);
                    mBinding.textHistory.setText("No history data found :(");
                } else {
                    mBinding.textHistory.setText("History");
                    mBinding.historyList.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(mBinding.getRoot(),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };

        mBinding.historyList.setAdapter(mAdapter);
        mBinding.historyList.setLayoutManager(new LinearLayoutManager(getActivity()));

        return mBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Start listening for Firestore updates
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onHistorySelected(DocumentSnapshot history) {

    }
}
