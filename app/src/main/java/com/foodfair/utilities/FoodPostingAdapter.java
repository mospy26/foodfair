package com.foodfair.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.foodfair.FoodPostingHolder;
import com.foodfair.R;
import com.foodfair.model.FoodItemInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodPostingAdapter extends   RecyclerView.Adapter<FoodPostingAdapter.ViewHolder> {

    List<FoodItemInfo> items;
    Context context;
    FirebaseFirestore ref = FirebaseFirestore.getInstance();

    public FoodPostingAdapter(Context context) {

        items = new ArrayList<>();
        this.context = context;
        String uid = FirebaseAuth.getInstance().getUid();
        System.out.println(uid);
        ref.collection("foodItemInfo").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        FoodItemInfo item = document.toObject(FoodItemInfo.class);
                        String[] splitted = item.getDonorRef().getPath().split("/");
                        if(item.getCount() > 0 && item.getStatus().equals(1) && splitted[1] != uid)
                        {
                        items.add(item);
                        }


                    }

                }
                notifyDataSetChanged();
            }
        });





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

        DocumentReference ref = items.get(position).getDonorRef();
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot rf = task.getResult();
                if (rf.exists()) {
                    String name = (String) rf.get("name");
                    holder.foodPostedBy.setText(name);
                }


            }
        });
        Picasso.get().load(items.get(position).getImageDescription().get(0)).into(holder.foodImage);



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

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            foodName = itemView.findViewById(R.id.food_name_home);
            foodPostedDate = itemView.findViewById(R.id.postedDate);
            foodImage = itemView.findViewById(R.id.foodImage);
            foodPostedBy = itemView.findViewById(R.id.userPosted);
        }
    }


}
