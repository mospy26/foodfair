package com.foodfair.ui.hamburger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.foodfair.databinding.FragmentLeaderboardBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class LeaderboardFragment extends Fragment implements
        LeaderboardAdapter.OnLeaderboardSelectListener, View.OnClickListener {

    private static final String TAG = "LeaderboardFragment";
    private LeaderboardViewModel leaderboardViewModel;
    private FirebaseFirestore mFirestore;
    private Query mQueryMonthly;
    private Query mQueryAllTime;

    private boolean isMonthlyLeaderBoard = true;

    private FragmentLeaderboardBinding mBinding;

    private LeaderboardAdapter mAdapterMonthly;
    private LeaderboardAdapter mAdapterAllTime;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mBinding = FragmentLeaderboardBinding.inflate(getLayoutInflater());

        mBinding.textLeaderboard.setText("Monthly Leaderboard");
        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        // Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get monthly leaderboard
        mQueryMonthly = mFirestore.collection(("leaderboard"))
                .document("period 1")
                .collection("ranking")
                .orderBy("position", Query.Direction.DESCENDING)
                .limit(10);

        mAdapterMonthly = new LeaderboardAdapter(mQueryMonthly, this){
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    mBinding.leaderboardList.setVisibility(View.GONE);
                } else {
                    mBinding.leaderboardList.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(mBinding.getRoot(),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };

        // Get all time leaderboard
        mQueryAllTime = mFirestore.collection(("leaderboard"))
                .document("total")
                .collection("ranking")
                .orderBy("position", Query.Direction.DESCENDING)
                .limit(10);

        mAdapterAllTime = new LeaderboardAdapter(mQueryAllTime, this){
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    mBinding.leaderboardList.setVisibility(View.GONE);
                } else {
                    mBinding.leaderboardList.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(mBinding.getRoot(),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };

        mBinding.leaderboardList.setAdapter(mAdapterMonthly);
        mBinding.leaderboardList.setLayoutManager(new LinearLayoutManager(getActivity()));



        mBinding.btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Switch leaderboard
                if(isMonthlyLeaderBoard) {
                    mBinding.textLeaderboard.setText("All Time Leaderboard");
                    mBinding.leaderboardList.setAdapter(mAdapterAllTime);
                    mBinding.leaderboardList.setLayoutManager(new LinearLayoutManager(getActivity()));
                    isMonthlyLeaderBoard = false;
                } else {
                    mBinding.textLeaderboard.setText("Monthly Leaderboard");
                    mBinding.leaderboardList.setAdapter(mAdapterMonthly);
                    mBinding.leaderboardList.setLayoutManager(new LinearLayoutManager(getActivity()));
                    isMonthlyLeaderBoard = true;
                }
            }
        });

        return mBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Start listening for Firestore updates
        if (mAdapterMonthly != null) {
            mAdapterMonthly.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapterMonthly != null) {
            mAdapterMonthly.stopListening();
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onLeaderboardSelected(DocumentSnapshot leaderboard) {

    }
}
