package com.foodfair.ui.profiles;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.foodfair.R;
import com.foodfair.model.FoodItemInfo;
import com.foodfair.model.UsersInfo;
import com.foodfair.model.ReviewInfo;
import com.foodfair.utilities.Const;
import com.foodfair.utilities.Utility;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class UserProfileActivity extends AppCompatActivity {
    FirebaseFirestore db;
    public String userId;
    public CircleImageView profileCircleImageView;
    public TextView nameTextView;
    public ImageView genderImageView;
    public TextView userIdTextView;
    public TextView ageTextView;
    public TextView bioTextView;
    public TextView allergyTextView;
    public TextView preferenceTextView;
    public TextView locationTextView;
    public TextView joinDateTextView;
    public TextView lastLoginDateTextView;
    public TextView reviewsCountTextView;
    public LinearLayout reviewsLinearLayout;
    public TextView reviewsFoodNameTextView;
    public TextView reviewsDonorNameTextView;
    public TextView reviewsDateTextView;
    public RatingBar reviewsRatingBar;
    public TextView reviewsTextReviewTextView;
    public ArrayList<ShapeableImageView> reviewsImageShapeableImageViews = new ArrayList<>();
    public TextView usersConsumerBadgeCountTextView;
    public TableLayout usersConsumerBadgeTableLayout;
    public TextView usersDonorBadgeCountTextView;
    public TableLayout usersDonorBadgeTableLayout;
    public TextView donorOnShelfCountTextView;
    public LinearLayout donorOnShelfLinearLayout;
    public TextView donorOnShelfFoodNameTextView;
    public TextView donorOnShelfDateOnTextView;
    public TextView donorOnShelfDateExpireTextView;
    public ArrayList<ShapeableImageView> donorOnShelfFoodImageShapeableImageViews = new ArrayList<>();
    public TextView donorOnShelfTextDescriptionTextView;

    public TextView donorReviewedItemCountTextView;
    public LinearLayout donorReviewedItemLinearLayout;
    public TextView donorReviewedItemConsumerTextView;
    public TextView donorReviewedItemFoodNameTextView;
    public TextView donorReviewedItemReviewedDateTextView;
    public RatingBar donorReviewedItemRatingBar;
    public ArrayList<ShapeableImageView> donorReviewedItemImageShapeableImageViews = new ArrayList<>();
    public TextView donorReviewedItemTextDescriptionTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        InitUI();
//        InitListener();
//        mBookSuccessViewModel = new BookSuccessViewModel(BitmapFactory.decodeResource(getResources(),
//                R.drawable.foodsample));
//        ViewModelObserverSetup();


        userId = "yXnhEl9OBqgKqHLAPMPV";
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.sample_profile_image);
        icon = Utility.getResizedBitmap(icon, 200);

        String photoBase64 = Utility.bitmapToBase64(icon);
        int a = 1;
        firebaseRegisterAndLogin("tom@gmail.com", "Iamtomspassword");
        db = FirebaseFirestore.getInstance();


//        // Get the data from an ImageView as bytes
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        Bitmap bitmap = ((BitmapDrawable) profileCircleImageView.getDrawable()).getBitmap();
//        bitmap = Utility.getResizedBitmap(bitmap,200);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] data = baos.toByteArray();
//
//        StorageReference storageRef = storage.getReference();
//        StorageReference mountainsRef = storageRef.child("tomsimage.jpg");
//        UploadTask uploadTask = mountainsRef.putBytes(data);
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle unsuccessful uploads
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
//                // ...
//                taskSnapshot.getStorage().getDownloadUrl();
//            }
//        });
    }

    private void createBadgeViews(ArrayList<Number> badges, TableLayout tableLayout) {
        View testView = LayoutInflater.from(this).inflate(R.layout.badg_row, null);
        int childCount = ((ViewGroup) testView).getChildCount();
        int count = badges.size();
        int single = count % childCount;
        int lines = count / childCount;
        for (int i = 0; i < lines; ++i) {
            View view = LayoutInflater.from(this).inflate(R.layout.badg_row,
                    tableLayout, false);
            tableLayout.addView(view);
            for (int index = 0; index < ((ViewGroup) view).getChildCount(); index++) {
                CircleImageView imageView = (CircleImageView) ((ViewGroup) view).getChildAt(index);
                imageView.setImageResource(R.drawable.sample_profile_image);
            }
        }

        if (single != 0) {
            View view = LayoutInflater.from(this).inflate(R.layout.badg_row,
                    tableLayout, false);
            tableLayout.addView(view);
            for (int index = 0; index < ((ViewGroup) view).getChildCount(); index++) {
                CircleImageView imageView = (CircleImageView) ((ViewGroup) view).getChildAt(index);
                imageView.setVisibility(INVISIBLE);
            }
            for (int index = 0; index < single; index++) {
                CircleImageView imageView = (CircleImageView) ((ViewGroup) view).getChildAt(index);
                imageView.setImageResource(R.drawable.sample_profile_image);
                imageView.setVisibility(VISIBLE);
            }
        }
    }

    private void InitUI() {
        profileCircleImageView = findViewById(R.id.userProfile_profileCircleImageView);
        nameTextView = findViewById(R.id.userProfile_nameTextView);
        genderImageView = findViewById(R.id.userProfile_genderImageView);
        userIdTextView = findViewById(R.id.userProfile_userIdTextView);
        ageTextView = findViewById(R.id.userProfile_ageTextView);
        bioTextView = findViewById(R.id.userProfile_bioTextView);
        allergyTextView = findViewById(R.id.userProfile_allergyTextView);
        preferenceTextView = findViewById(R.id.userProfile_preferenceTextView);
        locationTextView = findViewById(R.id.userProfile_locationTextView);
        joinDateTextView = findViewById(R.id.userProfile_joinDateTextView);
        lastLoginDateTextView = findViewById(R.id.userProfile_lastLoginDateTextView);
        reviewsCountTextView = findViewById(R.id.userProfile_reviewsCountTextView);
        reviewsLinearLayout = findViewById(R.id.userProfile_reviewsLinearLayout);
        reviewsFoodNameTextView = findViewById(R.id.userProfile_reviewsFoodNameTextView);
        reviewsDonorNameTextView = findViewById(R.id.userProfile_reviewsDonorNameTextView);
        reviewsDateTextView = findViewById(R.id.userProfile_reviewsDateTextView);
        reviewsRatingBar = findViewById(R.id.userProfile_reviewsRatingBar);
        reviewsTextReviewTextView = findViewById(R.id.userProfile_reviewsTextReviewTextView);
        reviewsImageShapeableImageViews.add(findViewById(R.id.userProfile_reviewsImageReviewShapeableImageView1));
        reviewsImageShapeableImageViews.add(findViewById(R.id.userProfile_reviewsImageReviewShapeableImageView2));
        reviewsImageShapeableImageViews.add(findViewById(R.id.userProfile_reviewsImageReviewShapeableImageView3));
        reviewsImageShapeableImageViews.add(findViewById(R.id.userProfile_reviewsImageReviewShapeableImageView4));
        usersConsumerBadgeCountTextView = findViewById(R.id.userProfile_usersConsumerBadgeCountTextView);
        usersConsumerBadgeTableLayout = findViewById(R.id.userProfile_usersConsumerBadgeTableLayout);
        usersDonorBadgeCountTextView = findViewById(R.id.userProfile_usersDonorBadgeCountTextView);
        usersDonorBadgeTableLayout = findViewById(R.id.userProfile_usersDonorBadgeTableLayout);
        donorOnShelfCountTextView = findViewById(R.id.userProfile_donorOnShelfCountTextView);
        donorOnShelfLinearLayout = findViewById(R.id.userProfile_donorOnShelfLinearLayout);
        donorOnShelfFoodNameTextView = findViewById(R.id.userProfile_donorOnShelfFoodNameTextView);
        donorOnShelfDateOnTextView = findViewById(R.id.userProfile_donorOnShelfDateOnTextView);
        donorOnShelfDateExpireTextView = findViewById(R.id.userProfile_donorOnShelfDateExpireTextView);
        donorOnShelfFoodImageShapeableImageViews.add(findViewById(R.id.userProfile_donorOnShelfFoodImageImageView1));
        donorOnShelfFoodImageShapeableImageViews.add(findViewById(R.id.userProfile_donorOnShelfFoodImageImageView2));
        donorOnShelfFoodImageShapeableImageViews.add(findViewById(R.id.userProfile_donorOnShelfFoodImageImageView3));
        donorOnShelfFoodImageShapeableImageViews.add(findViewById(R.id.userProfile_donorOnShelfFoodImageImageView4));
        donorOnShelfTextDescriptionTextView = findViewById(R.id.userProfile_donorOnShelfTextDescriptionTextView);
        donorReviewedItemCountTextView = findViewById(R.id.userProfile_donorReviewedItemCountTextView);
        donorReviewedItemLinearLayout = findViewById(R.id.userProfile_donorReviewedItemLinearLayout);

        donorReviewedItemConsumerTextView = findViewById(R.id.userProfile_donorReviewedItemConsumerTextView);
        donorReviewedItemFoodNameTextView = findViewById(R.id.userProfile_donorReviewedItemFoodNameTextView);;
        donorReviewedItemReviewedDateTextView = findViewById(R.id.userProfile_donorReviewedItemReviewedDateTextView);
        donorReviewedItemRatingBar = findViewById(R.id.userProfile_donorReviewedItemRatingBar);

        donorReviewedItemImageShapeableImageViews.add(findViewById(R.id.userProfile_donorReviewedItemFoodImageImageView1));
        donorReviewedItemImageShapeableImageViews.add(findViewById(R.id.userProfile_donorReviewedItemFoodImageImageView2));
        donorReviewedItemImageShapeableImageViews.add(findViewById(R.id.userProfile_donorReviewedItemFoodImageImageView3));
        donorReviewedItemImageShapeableImageViews.add(findViewById(R.id.userProfile_donorReviewedItemFoodImageImageView4));
        donorReviewedItemTextDescriptionTextView =
                findViewById(R.id.userProfile_donorReviewedItemTextDescriptionTextView);

    }



    /**
     * Firebase register and login jobs
     */
    private void firebaseRegisterAndLogin(String email, String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this,
                task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "createUserWithEmail:success");
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "createUserWithEmail:failure", task.getException());
                    }
                });

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        fetchDBInfo(userId);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "signInWithEmail:failure", task.getException());
                    }
                });
    }

    private void fetchDBInfo(String userId) {
//
        db.collection(UsersInfo.FIREBASE_COLLECTION_USER_INFO).document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    setUserProfileUI(document);
                    Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                } else {
                    Log.d("TAG", "No such document");
                }
            } else {
                int k = 1;
            }
        });
    }

    private void setUserProfileUI(DocumentSnapshot documentSnapshot) {
        HashMap<String, Object> data = (HashMap<String, Object>) documentSnapshot.getData();

        UsersInfo usersInfo = documentSnapshot.toObject(UsersInfo.class);
        // profile image
        String url = usersInfo.getProfileImage();
        Picasso.get().load(url).into(profileCircleImageView);

        // name
        String name = usersInfo.getName();
        nameTextView.setText(name);

        // gender
        Long gender = usersInfo.getGender();
        if (gender.equals(UsersInfo.FIREBASE_COLLECTION_USER_INFO_GENDER_VALUE_MALE)) {
            genderImageView.setImageResource(R.drawable.icons8_male_96);
        } else {
            genderImageView.setImageResource(R.drawable.icons8_female_96);
        }

        // userId
        String userId = this.userId;
        userIdTextView.setText("@" + userId);

        // birthday
        Timestamp birthday = usersInfo.getBirthday();
        Date dateBirthday = birthday.toDate();
        Date now = new Date();
        long diffInMillies = now.getTime() - dateBirthday.getTime();
        long year = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) / 365;
        ageTextView.setText(Long.toString(year));

        // bio
        String bio = usersInfo.getBio();
        bioTextView.setText(bio);

        // allergy
        ArrayList<Long> allergy = (ArrayList<Long>) usersInfo.getAllergy();
        String allergy_text = "";
        if (allergy != null) {
            for (int i = 0; i < allergy.size(); ++i) {
                allergy_text += Const.ALLERGY_DETAIL.get(allergy.get(i));
                if (i != allergy.size() - 1 && i != allergy.size() - 2) {
                    allergy_text += ", ";
                }
                if (i == allergy.size() - 2) {
                    allergy_text += " and ";
                }
            }
        }
        allergyTextView.setText(allergy_text);

        // preference
        Long preferenceIndex = usersInfo.getPreference();
        String preference_text = Const.FOOD_TYPE_DETAIL.get(preferenceIndex);
        preferenceTextView.setText(preference_text);

        // location
        String location = usersInfo.getLocation();
        locationTextView.setText(location);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        // joinDate
        Timestamp joinDate = usersInfo.getJoinDate();
        if (joinDate != null) {
            Date date = joinDate.toDate();
            String joinDateText = simpleDateFormat.format(date);
            joinDateTextView.setText(joinDateText);

        }

        // lastLoginDate
        Timestamp lastLoginDate = usersInfo.getLastLogin();
        if (lastLoginDate != null) {
            Date date = lastLoginDate.toDate();
            String lastLoginDateText = simpleDateFormat.format(date);
            lastLoginDateTextView.setText(lastLoginDateText);

        }
        // -- as Consumer --
        HashMap<String, Object> asConsumer = (HashMap<String, Object>) usersInfo.getAsConsumer();
        // review count
        ArrayList<DocumentReference> reviews =
                (ArrayList<DocumentReference>)asConsumer.get(UsersInfo.FIREBASE_COLLECTION_USER_INFO_SUB_KEY_OF_AS_CONSUMER_REVIEWS);
        Integer reviewsCount = reviews.size();
        String reviewsCountStr = Integer.toString(reviewsCount);
        reviewsCountTextView.setText(reviewsCountStr);
        if (reviewsCount.equals(0)) {
            reviewsLinearLayout.setVisibility(View.GONE);
        } else {
            reviewsLinearLayout.setVisibility(VISIBLE);
            DocumentReference lastReviewRef = reviews.get(reviews.size() - 1);
            lastReviewRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // In review document
                    ReviewInfo reviewInfo = task.getResult().toObject(ReviewInfo.class);
                    DocumentReference food = reviewInfo.getFoodRef();
                    food.get().addOnCompleteListener(foodTask -> {
                        if (foodTask.isSuccessful()) {
                            String foodName = foodTask.getResult().toObject(FoodItemInfo.class).getName();
                            // review food name
                            reviewsFoodNameTextView.setText(foodName);
                        }
                    });

                    DocumentReference donor =
                            reviewInfo.getToUser();
                    donor.get().addOnCompleteListener(donorTask -> {
                        if (donorTask.isSuccessful()) {
                            String donorName = donorTask.getResult().toObject(UsersInfo.class).getName();
                            // review donor name
                            reviewsDonorNameTextView.setText(donorName);
                        }
                    });

                    // review date
                    Timestamp date = reviewInfo.getDate();
                    Date reviewDate = date.toDate();
                    String reviewDateStr = simpleDateFormat.format(reviewDate);
                    reviewsDateTextView.setText(reviewDateStr);

                    // review rating
                    Number rating = reviewInfo.getRating();
                    reviewsRatingBar.setRating(rating.floatValue());

                    // review text review description
                    String textReviewDescription = reviewInfo.getTextReview();
                    reviewsTextReviewTextView.setText(textReviewDescription);

                    // review image reviews
                    reviewsImageShapeableImageViews.forEach(e -> {
                        e.setVisibility(View.INVISIBLE);
                    });
                    ArrayList<String> imageUrls = reviewInfo.getImageReviews();
                    for (int i = 0; i < imageUrls.size() && i < reviewsImageShapeableImageViews.size(); ++i) {
                        reviewsImageShapeableImageViews.get(i).setVisibility(VISIBLE);
                        Picasso.get().load(imageUrls.get(i)).into(reviewsImageShapeableImageViews.get(i));
                    }
                }
            });
        }
        // badge count
        ArrayList<Number> consumerBadges =
                (ArrayList<Number>) asConsumer.get(UsersInfo.FIREBASE_COLLECTION_USER_INFO_SUB_KEY_OF_AS_CONSUMER_BADGES);
        int consumerBadgesCount = consumerBadges.size();
        usersConsumerBadgeCountTextView.setText(Integer.toString(consumerBadgesCount));
        // badges
        createBadgeViews(consumerBadges,usersConsumerBadgeTableLayout);

        // -- as Donor --
        HashMap<String, Object> asDonor = (HashMap<String, Object>) usersInfo.getAsDonor();
        // on shelf
        ArrayList<DocumentReference> itemsOnShelf = (ArrayList<DocumentReference>) asDonor.get(usersInfo.ITEMS_ON_SHELF);
        int itemCount = itemsOnShelf.size();
        donorOnShelfCountTextView.setText(Integer.toString(itemCount));
        if(itemCount == 0){
            donorOnShelfLinearLayout.setVisibility(View.GONE);
        }else {
            donorOnShelfLinearLayout.setVisibility(VISIBLE);
            DocumentReference docRef = itemsOnShelf.get(itemCount-1);
            docRef.get().addOnCompleteListener(foodTask->{
                if (foodTask.isSuccessful()){
                    FoodItemInfo foodItemInfo = foodTask.getResult().toObject(FoodItemInfo.class);
                    donorOnShelfFoodNameTextView.setText(foodItemInfo.getName());
                    donorOnShelfDateOnTextView.setText(simpleDateFormat.format(foodItemInfo.getDateOn().toDate()));
                    donorOnShelfDateExpireTextView.setText(simpleDateFormat.format(foodItemInfo.getDateExpire().toDate()));
                    donorOnShelfTextDescriptionTextView.setText(foodItemInfo.getTextDescription());
                    donorOnShelfFoodImageShapeableImageViews.forEach(e->e.setVisibility(INVISIBLE));
                    for(int i = 0; i < donorOnShelfFoodImageShapeableImageViews.size() && i < foodItemInfo.getImageDescription().size(); ++i){
                        donorOnShelfFoodImageShapeableImageViews.get(i).setVisibility(VISIBLE);
                        Picasso.get().load(foodItemInfo.getImageDescription().get(i)).into(donorOnShelfFoodImageShapeableImageViews.get(i));
                    }
                }
            });
        }

        // reviewed
        ArrayList<DocumentReference> itemsReviewed =
                (ArrayList<DocumentReference>) asDonor.get(usersInfo.ITEMS_REVIEWED);
        int itemReviewedCount = itemsReviewed.size();
        donorReviewedItemCountTextView.setText(Integer.toString(itemReviewedCount));
        if(itemReviewedCount == 0){
            donorReviewedItemLinearLayout.setVisibility(View.GONE);
        }else {
            donorReviewedItemLinearLayout.setVisibility(VISIBLE);
            DocumentReference docRef = itemsReviewed.get(itemReviewedCount-1);
            docRef.get().addOnCompleteListener(reviewedItemTask->{
                if (reviewedItemTask.isSuccessful()){
                    ReviewInfo reviewInfo = reviewedItemTask.getResult().toObject(ReviewInfo.class);
                    reviewInfo.getFromUser().get().addOnCompleteListener(userTask->{
                        if (userTask.isSuccessful()){
                            UsersInfo usersInfo1 = userTask.getResult().toObject(UsersInfo.class);
                            donorReviewedItemConsumerTextView.setText(usersInfo1.getName());
                        }
                    });
                    reviewInfo.getFoodRef().get().addOnCompleteListener(foodTask->{
                        if (foodTask.isSuccessful()){
                            FoodItemInfo foodItemInfo = foodTask.getResult().toObject(FoodItemInfo.class);
                            donorReviewedItemFoodNameTextView.setText(foodItemInfo.getName());
                        }
                    });
                    donorReviewedItemReviewedDateTextView.setText(simpleDateFormat.format(reviewInfo.getDate().toDate()));
                    donorReviewedItemRatingBar.setRating(reviewInfo.getRating().floatValue());
                    donorReviewedItemTextDescriptionTextView.setText(reviewInfo.getTextReview());

                    donorReviewedItemImageShapeableImageViews.forEach(e->e.setVisibility(INVISIBLE));
                    for(int i = 0; i < donorReviewedItemImageShapeableImageViews.size() && i < reviewInfo.getImageReviews().size(); ++i){
                        donorReviewedItemImageShapeableImageViews.get(i).setVisibility(VISIBLE);
                        Picasso.get().load(reviewInfo.getImageReviews().get(i)).into(donorReviewedItemImageShapeableImageViews.get(i));
                    }
                }
            });
        }

        // badge count
        ArrayList<Number> donorBadges =
                (ArrayList<Number>) asConsumer.get(UsersInfo.FIREBASE_COLLECTION_USER_INFO_SUB_KEY_OF_AS_DONOR_BADGES);
        int donorBadgesCount = donorBadges.size();
        usersDonorBadgeCountTextView.setText(Integer.toString(consumerBadgesCount));
        // badges
        createBadgeViews(consumerBadges,usersDonorBadgeTableLayout);



    }


}
