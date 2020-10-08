package com.foodfair.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import androidx.recyclerview.widget.RecyclerView;

import com.foodfair.R;
import com.foodfair.adapters.ItemsRecyclerAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private ItemsRecyclerAdapter adapter;
    private FirebaseFirestore firestore;
    private RecyclerView itemRecycler;
    private Query query;
    private final int LIMIT = 10;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseFirestore.setLoggingEnabled(true);
        initFirestore();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        homeViewModel =
//                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.homepage, container, false);
        itemRecycler = (RecyclerView) root.findViewById(R.id.recycler_view_homepage);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
////                textView.setText(s);
//            }
//        });
        initRecycleView();
        return root;
    }

    public void initRecycleView() {

        if (query == null) {
            Log.w("Query", "No query, not initializing RecyclerView");
        }

        adapter = new ItemsRecyclerAdapter(query) {
//
//            @Override
//            protected void onDataChanged() {
////                 Show/hide content if the query returns empty.
//                Log.e("count", "" + getItemCount());
//                Log.e("count", "" + getItemCount());
//                Log.e("count", "" + getItemCount());
//                Log.e("count", "" + getItemCount());
//                Log.e("count", "" + getItemCount());
//                Log.e("count", "" + getItemCount());
//                if (getItemCount() == 0) {
//                    itemRecycler.setVisibility(View.VISIBLE);
////                    mEmptyView.setVisibility(View.VISIBLE);
//                } else {
//                    itemRecycler.setVisibility(View.VISIBLE);
////                    mEmptyView.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            protected void onError(FirebaseFirestoreException e) {
//                // Show a snackbar on errors
////                Snackbar.make(findViewById(android.R.id.content),
////                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
//            }
        };
        itemRecycler.setAdapter(adapter);
        itemRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public void initFirestore() {
        firestore = FirebaseFirestore.getInstance();
//        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//                .setTimestampsInSnapshotsEnabled(true)
//                .build();
//        firestore.setFirestoreSettings(settings);
        CollectionReference fooditems = firestore.collection("fooditems");
        query = fooditems.orderBy("rating", Query.Direction.DESCENDING)
                .limit(LIMIT);
    }
}