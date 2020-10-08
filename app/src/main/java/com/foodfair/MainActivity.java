package com.foodfair;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.foodfair.holder.foodItemViewHolder;
import com.foodfair.model.testFoodItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth firebaseAuth;
    private RecyclerView mFirebaseRecycler;
    private FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseFirestore = FirebaseFirestore.getInstance();   // Get instance of database
        mFirebaseRecycler = findViewById(R.id.firebaseList);  // Set and start the recycler

        Query query = firebaseFirestore.collection("fooditems");   //Start a query that will access db
        FirestoreRecyclerOptions<testFoodItem> options = new FirestoreRecyclerOptions.Builder<testFoodItem>().setQuery(query,testFoodItem.class).build();  //recycler view will now display items from db. using builder


        //View Holder

    adapter = new FirestoreRecyclerAdapter<testFoodItem, foodItemViewHolder>(options) {   // View Holder sets the adapter,

            @NonNull
            @Override
            public foodItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_home,parent,false);  // My custom cardview layout set
                return new foodItemViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull foodItemViewHolder holder, int position, @NonNull testFoodItem model) {
                holder.t1.setText(model.title);   // Set the Textfields
                holder.t2.setText(model.description);
                holder.t3.setText(model.rating);

            }


        };

        mFirebaseRecycler.setHasFixedSize(true);
        mFirebaseRecycler.setLayoutManager(new LinearLayoutManager(this));
        mFirebaseRecycler.setAdapter(adapter);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onStop() {

        super.onStop();
        adapter.stopListening();
    }
    @Override
    protected void onStart() {

        super.onStart();
        adapter.startListening();
    }

}