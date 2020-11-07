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
import com.foodfair.model.FoodItemInfo;
import com.foodfair.ui.foodpages.FoodDetailActivity;
import com.foodfair.ui.foodpages.MapViewActivity;
import com.foodfair.ui.foodpages.PostFoodActivity;
import com.foodfair.ui.login.Sign_Up;
import com.foodfair.utilities.FoodPostingAdapter;
import com.foodfair.utilities.Utility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FirebaseFirestore dbReference;
    private FirestoreRecyclerAdapter adapter;
    private RecyclerView recyclerView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = root.findViewById(R.id.list);
        System.out.println(recyclerView);
        dbReference = FirebaseFirestore.getInstance();

        // Queries items where status is "1" and the count is greater than 0

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
       DocumentReference df = dbReference.collection("usersInfo").document("XWNQVbEdFKWbGOnok7wgMARYXgp2");


       FoodItemInfo n = new FoodItemInfo();
       n.setName("LOL");

       List<FoodItemInfo> fd = new ArrayList<>();
       fd.add(n);

       recyclerView.setHasFixedSize(true);
       recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
       FoodPostingAdapter adapt = new FoodPostingAdapter(getContext());

       recyclerView.setAdapter(adapt);



        return root;
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


}