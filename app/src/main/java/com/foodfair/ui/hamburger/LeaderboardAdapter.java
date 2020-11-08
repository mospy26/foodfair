package com.foodfair.ui.hamburger;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.foodfair.R;
import com.foodfair.databinding.AdapterLeaderboardBinding;
import com.foodfair.model.Leaderboard;
import com.foodfair.model.Ranking;
import com.foodfair.model.UsersInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

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
                LayoutInflater.from(parent.getContext()), parent, false),
                parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener, position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private AdapterLeaderboardBinding binding;
        private Context context;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public ViewHolder(AdapterLeaderboardBinding binding, Context context) {
            super(binding.getRoot());
            this.binding = binding;
            this.context = context;
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnLeaderboardSelectListener listener,
                         int leaderboardPosition) {
            Ranking ranking = snapshot.toObject(Ranking.class);
            Resources resources = itemView.getResources();

            DocumentReference donorRef = ranking.getDonor();
            donorRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    UsersInfo usersInfo = documentSnapshot.toObject(UsersInfo.class);
                    if(usersInfo != null){
                        binding.leaderboardRank.setText(String.valueOf(leaderboardPosition));
                        if(usersInfo.getProfileImage() != null && !usersInfo.getProfileImage().isEmpty()) {
                            Picasso.get().load(usersInfo.getProfileImage())
                                    .into(binding.icon);
                        }
                        binding.userName.setText(usersInfo.getName());
                        binding.userBio.setText(usersInfo.getBio());
                        binding.donationCount.setText("Total Donations: " + ranking.getDonationCount());
                        binding.donationScore.setText("Score: " + ranking.getScore());
                    } else {
                        // TODO: Error messages
                    }

                }
            });

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onLeaderboardSelected(snapshot);
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
