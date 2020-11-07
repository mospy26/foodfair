package com.foodfair.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.foodfair.FoodPostingHolder;
import com.foodfair.R;
import com.foodfair.databinding.FragmentHomeBinding;
import com.foodfair.model.FoodItemInfo;
import com.foodfair.ui.foodpages.FoodDetailActivity;
import com.foodfair.ui.foodpages.MapViewActivity;
import com.foodfair.ui.foodpages.PostFoodActivity;
import com.foodfair.ui.login.Sign_Up;
import com.foodfair.utilities.Utility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment implements
    HomeAdapter.OnHomeItemSelectedListener, View.OnClickListener {

//    private HomeViewModel homeViewModel;
//    private FirebaseFirestore dbReference;
//    private FirestoreRecyclerAdapter adapter;
//    private RecyclerView recyclerView;

    private FragmentHomeBinding mBinding;

    private HomeAdapter mAdapter;
    private FirebaseFirestore mFirestore;
    private Query mQuery;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mBinding = FragmentHomeBinding.inflate(getLayoutInflater());
        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        // Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // TODO: @Sadman, add more criteria here and order by if you can. 
        mQuery = mFirestore.collection("foodItemInfo")
                .whereGreaterThan("count", 0);


        mAdapter = new HomeAdapter(mQuery, this);

        //recyclerView.setHasFixedSize(true);
        mBinding.list.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        mBinding.list.setAdapter(mAdapter);

        fabStuff(mBinding.getRoot());
        return mBinding.getRoot();
    }

    private void fabStuff(View root) {
        FloatingActionButton fab = root.findViewById(R.id.floating_action_button);
        fab.setOnClickListener(view ->{
            Intent intent = new Intent(getContext(), MapViewActivity.class);
            startActivity(intent);
        });

        FloatingActionButton postButton = root.findViewById(R.id.post_food_button);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("foodfair", Context.MODE_PRIVATE);
        String uid = sharedPref.getString("firebasekey", null);
        Long status = sharedPref.getLong(uid + "_status", -1);
        if (status == 2 || FirebaseAuth.getInstance().getCurrentUser() == null) {
            postButton.setVisibility(View.GONE);
        }

        postButton.setOnClickListener(view ->{
            Intent intent = new Intent(getContext(), PostFoodActivity.class);
            startActivity(intent);
        });
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
    public void onHomeItemSelected(DocumentSnapshot snapshot) {
        Intent intent = new Intent(getContext(), FoodDetailActivity.class);
                    intent.putExtra("foodId", snapshot.getReference().getId());
                    startActivity(intent);
    }

    @Override
    public void onClick(View v) {

    }
}