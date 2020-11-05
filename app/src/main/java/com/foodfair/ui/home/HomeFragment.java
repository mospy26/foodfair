package com.foodfair.ui.home;

import android.content.Intent;
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
import com.foodfair.ui.login.Sign_Up;
import com.foodfair.utilities.Utility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

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
        Query query = dbReference.collection("foodItemInfo");

        FirestoreRecyclerOptions<FoodItemInfo> foodItemInfoFirestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<FoodItemInfo>().setQuery(query, FoodItemInfo.class).build();


        adapter = new FirestoreRecyclerAdapter<FoodItemInfo, FoodPostingHolder>(foodItemInfoFirestoreRecyclerOptions) {


            @NonNull
            @Override
            public FoodPostingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.foodposting, parent, false);
                return new FoodPostingHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FoodPostingHolder holder, int position, @NonNull FoodItemInfo model) {

                DocumentReference ref = model.getDonorRef();
                ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot rf = task.getResult();
                        if (rf.exists()) {
                            String name = (String) rf.get("name");
                            holder.foodPostedBy.setText(name);
                            holder.foodTitle.setText(model.getName());
                            holder.foodPostedDate.setText("Expires  " + Utility.timeStampToDateString(model.getDateExpire()));

                            if (model.getImageDescription() != null && model.getImageDescription().size() != 0) {
                                Picasso.get().load(model.getImageDescription().get(0)).into(holder.imageToDisplay);
                            }
                            ;
                            holder.foodItemId = ((DocumentSnapshot) adapter.getSnapshots().getSnapshot(position)).getId();
                        }


                    }
                });


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Start Food Detail Activity
                        Intent intent = new Intent(getContext(), FoodDetailActivity.class);
                        intent.putExtra("foodId",holder.foodItemId);
                        startActivity(intent);
                    }
                });

            }
        };


        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setAdapter(adapter);

        fabStuff(root);
        return root;
    }

    private void fabStuff(View root) {
        FloatingActionButton fab = root.findViewById(R.id.floating_action_button);
        fab.setOnClickListener(view ->{
            Intent intent = new Intent(getContext(), MapViewActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}