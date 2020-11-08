package com.foodfair.utilities;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.foodfair.R;
import com.foodfair.model.FoodItemInfo;
import com.foodfair.ui.foodpages.FoodDetailActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class FoodPostingAdapter extends   RecyclerView.Adapter<FoodPostingAdapter.ViewHolder> {

    List<FoodItemInfo> items;
    Context context;
    FirebaseFirestore ref = FirebaseFirestore.getInstance();
    double userLat = 0;
  double userLong = 0.0;

    public FoodPostingAdapter(Context context) {

        items = new ArrayList<>();

        this.context = context;
        String uid = FirebaseAuth.getInstance().getUid();
        String uidFormat = "usersInfo/" +uid;

// ADD UID TO FoodItemInfo model class.








        ref.collection("foodItemInfo").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        FoodItemInfo item = document.toObject(FoodItemInfo.class);
                        item.setItemID(document.getId());

                        if (item.getStatus().equals(1) && item.getCount() != 0 && !item.getDonorRef().getPath().equals(uidFormat)) {
                            items.add(item);
                            Collections.sort(items, new Comparator<FoodItemInfo>() {
                                @Override
                                public int compare(FoodItemInfo foodItemInfo, FoodItemInfo t1) {

                                    return foodItemInfo.getDateExpire().toDate().compareTo(t1.getDateOn().toDate());
                                }
                            });

                        }
                    }
                }
                notifyDataSetChanged();
            }


        });

//TO-DO REMOVE EXPIRED ITEMS.

//        for(int i = 0; i<items.size(); ++i)
//        {
//            items.get(i).getDateOn().
//        }


        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.foodposting,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.foodName.setText(items.get(position).getName());
        holder.foodPostedDate.setText("Expires " + Utility.timeStampToDateString(items.get(position).getDateExpire()));
        //holder.whenWasFoodPosted.setText("Posted on " + Utility.timeStampToDateString(items.get(position).getDateOn()));

        DocumentReference ref = items.get(position).getDonorRef();
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot rf = task.getResult();
                if (rf.exists()) {
                    String name = (String) rf.get("name");
                    holder.foodPostedBy.setText("Posted By "  +name);
                }


            }
        });
        Picasso.get().load(items.get(position).getImageDescription().get(0)).resize(400,400).centerCrop().into(holder.foodImage);

       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(context,FoodDetailActivity.class);
               intent.putExtra("foodId",items.get(position).getItemID());
               context.startActivity(intent);
           }
       });



    }

    @Override
    public int getItemCount() {
        return items.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView foodPostedDate;
        public TextView foodName;
        public ImageView foodImage;
        public TextView foodPostedBy;
        public TextView whenWasFoodPosted;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            foodName = itemView.findViewById(R.id.food_name_home);
            foodPostedDate = itemView.findViewById(R.id.postedDate);
            foodImage = itemView.findViewById(R.id.foodImage);
            foodPostedBy = itemView.findViewById(R.id.userPosted);
            //whenWasFoodPosted = itemView.findViewById(R.id.postedDate2);
        }
    }


}
