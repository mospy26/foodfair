package com.foodfair.ui.hamburger;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.foodfair.databinding.AdapterLeaderboardBinding;
import com.foodfair.model.Leaderboard;
import com.foodfair.model.Ranking;
import com.foodfair.model.UsersInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class LeaderboardAdapter extends FirestoreAdapter<LeaderboardAdapter.ViewHolder> {

    public interface OnLeaderboardSelectListener {
        void onLeaderboardSelected(DocumentSnapshot leaderboard);
    }

    private OnLeaderboardSelectListener mListener;

    private static final String TAG = "LeaderboardAdapter";

    public LeaderboardAdapter(Query query, OnLeaderboardSelectListener listener){
        super(query);
        mListener = listener;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(AdapterLeaderboardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private AdapterLeaderboardBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public ViewHolder(AdapterLeaderboardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnLeaderboardSelectListener listener) {
            Ranking ranking = snapshot.toObject(Ranking.class);
            Resources resources = itemView.getResources();

            DocumentReference donorRef = ranking.getDonor();
            donorRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    UsersInfo usersInfo = documentSnapshot.toObject(UsersInfo.class);
                    if(usersInfo != null){
                        binding.firstLine.setText(usersInfo.getName());
                        binding.secondLine.setText(usersInfo.getBio());
                    } else {
                        // TODO: Error messages
                    }

                }
            });

            // Click listener
            // in case we want to do something
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onLeaderboardSelected(snapshot);
                    }
                }
            });

        }
    }
}
