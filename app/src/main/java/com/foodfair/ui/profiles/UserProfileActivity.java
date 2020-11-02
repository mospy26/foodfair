package com.foodfair.ui.profiles;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import androidx.lifecycle.MutableLiveData;

import com.foodfair.R;
import com.foodfair.model.Badge;
import com.foodfair.model.FoodItemInfo;
import com.foodfair.model.UsersInfo;
import com.foodfair.model.ReviewInfo;
import com.foodfair.utilities.Const;
import com.foodfair.utilities.Utility;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class UserProfileActivity extends AppCompatActivity {
    UserProfileViewModel userProfileViewModel;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    Const aConst = Const.getInstance();
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
    public TextView usersConsumerBadgeTotalCountTextView;
    public TableLayout usersConsumerBadgeTableLayout;
    public TextView usersDonorBadgeCountTextView;
    public TextView usersDonorBadgeTotalCountTextView;
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
        userProfileViewModel = new UserProfileViewModel();
        InitUI();
        userId = "yXnhEl9OBqgKqHLAPMPV";
        userProfileViewModel.id.setValue(userId);
        userProfileViewModel.asConsumerTotalBadgeCount.setValue(aConst.CONSUMER_BADGES.size());
        userProfileViewModel.asDonorTotalBadgeCount.setValue(aConst.DONOR_BADGES.size());
        viewModelObserverSetup();
    }

    private void viewModelObserverSetup() {
        userProfileViewModel.currentUserInfo.observe(this, currentUserInfo -> {
            userProfileViewModel.profileImageUrl.setValue(currentUserInfo.getProfileImage());
            userProfileViewModel.name.setValue(currentUserInfo.getName());
            userProfileViewModel.gender.setValue(currentUserInfo.getGender().intValue());
//            userProfileViewModel.id.setValue(userId);
            userProfileViewModel.birthday.setValue(currentUserInfo.getBirthday());
            userProfileViewModel.bio.setValue(currentUserInfo.getBio());
            userProfileViewModel.allergy.setValue((ArrayList<Long>) currentUserInfo.getAllergy());
            userProfileViewModel.preference.setValue(currentUserInfo.getPreference());
            userProfileViewModel.location.setValue(currentUserInfo.getLocation());
            userProfileViewModel.joinDate.setValue(currentUserInfo.getJoinDate());
            userProfileViewModel.lastLoginDate.setValue(currentUserInfo.getLastLogin());

            // as consumer
            Map<String,Object> asConsumer = currentUserInfo.getAsConsumer();
            ArrayList<DocumentReference> reviews =
                    (ArrayList<DocumentReference>) asConsumer.get(getResources().getString(R.string.FIREBASE_COLLECTION_USER_INFO_SUB_KEY_OF_AS_CONSUMER_REVIEWS));
            userProfileViewModel.asConsumerTotalReviewCount.setValue(reviews.size());

            ArrayList<Number> consumerBadges =
                    (ArrayList<Number>) asConsumer.get(getResources().getString(R.string.FIREBASE_COLLECTION_USER_INFO_SUB_KEY_OF_AS_CONSUMER_BADGES));
            userProfileViewModel.asConsumerGotBadgeCount.setValue(consumerBadges.size());
            userProfileViewModel.asConsumerBadges.setValue(consumerBadges);

            // as donor
            HashMap<String, Object> asDonor = (HashMap<String, Object>) currentUserInfo.getAsDonor();
            ArrayList<DocumentReference> itemsOnShelf =
                    (ArrayList<DocumentReference>) asDonor.get(getResources().getString(R.string.ITEMS_ON_SHELF));
            userProfileViewModel.asDonorTotalOnShelfCount.setValue(itemsOnShelf.size());
            ArrayList<DocumentReference> itemsReviewed =
                    (ArrayList<DocumentReference>) asDonor.get(getResources().getString(R.string.ITEMS_REVIEWED));
            userProfileViewModel.asDonorTotalReviewedCount.setValue(itemsReviewed.size());

            ArrayList<Number> donorBadges =
                    (ArrayList<Number>) asDonor.get(getResources().getString(R.string.FIREBASE_COLLECTION_USER_INFO_SUB_KEY_OF_AS_DONOR_BADGES));
            userProfileViewModel.asDonorGotBadgeCount.setValue(donorBadges.size());
            userProfileViewModel.asDonorBadges.setValue(donorBadges);

        });
        userProfileViewModel.consumerReviewInfo.observe(this, consumerReviewInfo -> {
            userProfileViewModel.asConsumerReviewDate.setValue(consumerReviewInfo.getDate());
            userProfileViewModel.asConsumerReviewRating.setValue(consumerReviewInfo.getRating());
            userProfileViewModel.asConsumerReviewText.setValue(consumerReviewInfo.getTextReview());
            userProfileViewModel.asConsumerReviewImageUrls.setValue(consumerReviewInfo.getImageReviews());
        });
        userProfileViewModel.consumerReviewDonorUserInfo.observe(this, consumerReviewDonorUserInfo -> {
            userProfileViewModel.asConsumerReviewDonorName.setValue(consumerReviewDonorUserInfo.getName());
        });
        userProfileViewModel.consumerReviewFoodInfo.observe(this, consumerReviewFoodInfo -> {
            userProfileViewModel.asConsumerReviewFoodName.setValue(consumerReviewFoodInfo.getName());
        });
        userProfileViewModel.donorOnShelfFoodInfo.observe(this, donorOnShelfFoodInfo -> {
            userProfileViewModel.asDonorFoodName.setValue(donorOnShelfFoodInfo.getName());
            userProfileViewModel.asDonorFoodDateOn.setValue(donorOnShelfFoodInfo.getDateOn());
            userProfileViewModel.asDonorFoodDateExpire.setValue(donorOnShelfFoodInfo.getDateExpire());
            userProfileViewModel.asDonorFoodImageUrls.setValue(donorOnShelfFoodInfo.getImageDescription());
            userProfileViewModel.asDonorFoodTextDescription.setValue(donorOnShelfFoodInfo.getTextDescription());
        });
        userProfileViewModel.donorReviewedInfo.observe(this, donorReviewedInfo -> {
            userProfileViewModel.asDonorReviewedDate.setValue(donorReviewedInfo.getDate());
            userProfileViewModel.asDonorReviewedRating.setValue(donorReviewedInfo.getRating());
            userProfileViewModel.asDonorReviewedImageUrls.setValue(donorReviewedInfo.getImageReviews());
            userProfileViewModel.asDonorReviewedText.setValue(donorReviewedInfo.getTextReview());
        });
        userProfileViewModel.donorReviewedUserInfo.observe(this, donorReviewedUserInfo -> {
            userProfileViewModel.asDonorReviewedConsumerName.setValue(donorReviewedUserInfo.getName());
        });
        userProfileViewModel.donorReviewedFoodInfo.observe(this, donorReviewedFoodInfo -> {
            userProfileViewModel.asDonorReviewedFoodName.setValue(donorReviewedFoodInfo.getName());
        });

        // ---------
        userProfileViewModel.profileImageUrl.observe(this, profileImageUrl -> {
            Picasso.get().load(profileImageUrl).into(profileCircleImageView);
        });
        userProfileViewModel.name.observe(this, name -> {
            nameTextView.setText(name);
        });

        userProfileViewModel.gender.observe(this, gender -> {
            // gender
            if (gender.equals(getResources().getInteger(R.integer.FIREBASE_COLLECTION_USER_INFO_GENDER_VALUE_MALE))) {
                genderImageView.setImageResource(R.drawable.icons8_male_96);
            } else {
                genderImageView.setImageResource(R.drawable.icons8_female_96);
            }
        });

        userProfileViewModel.id.observe(this, id -> {
            userIdTextView.setText("@" + id);
        });

        userProfileViewModel.birthday.observe(this, birthday -> {
            Date dateBirthday = birthday.toDate();
            Date now = new Date();
            long diffInMillies = now.getTime() - dateBirthday.getTime();
            long year = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) / 365;
            ageTextView.setText(Long.toString(year));
        });

        userProfileViewModel.bio.observe(this, bio -> {
            bioTextView.setText(bio);
        });

        userProfileViewModel.allergy.observe(this, allergy -> {
            String allergy_text = "";
            if (allergy != null) {
                for (int i = 0; i < allergy.size(); ++i) {
                    allergy_text += aConst.ALLERGY_DETAIL.get(allergy.get(i));
                    if (i != allergy.size() - 1 && i != allergy.size() - 2) {
                        allergy_text += ", ";
                    }
                    if (i == allergy.size() - 2) {
                        allergy_text += " and ";
                    }
                }
            }
            allergyTextView.setText(allergy_text);
        });

        userProfileViewModel.preference.observe(this, preference -> {
            String preference_text = aConst.FOOD_TYPE_DETAIL.get(preference);
            preferenceTextView.setText(preference_text);
        });

        userProfileViewModel.location.observe(this, location -> {
            locationTextView.setText(location);
        });

        userProfileViewModel.joinDate.observe(this, joinDate -> {
            if (joinDate != null) {
                Date date = joinDate.toDate();
                String joinDateText = simpleDateFormat.format(date);
                joinDateTextView.setText(joinDateText);
            }
        });

        userProfileViewModel.lastLoginDate.observe(this, lastLoginDate -> {
            if (lastLoginDate != null) {
                Date date = lastLoginDate.toDate();
                String lastLoginDateText = simpleDateFormat.format(date);
                lastLoginDateTextView.setText(lastLoginDateText);
            }
        });

        // As Consumer
        // Review
        userProfileViewModel.asConsumerTotalReviewCount.observe(this, asConsumerTotalReviewCount -> {
            String reviewsCountStr = Integer.toString(asConsumerTotalReviewCount);
            reviewsCountTextView.setText(reviewsCountStr);
            if (asConsumerTotalReviewCount.equals(0)) {
                reviewsLinearLayout.setVisibility(View.GONE);
            } else {
                reviewsLinearLayout.setVisibility(VISIBLE);
            }
        });

        userProfileViewModel.asConsumerReviewDonorName.observe(this, asConsumerReviewDonorName -> {
            reviewsDonorNameTextView.setText(asConsumerReviewDonorName);
        });

        userProfileViewModel.asConsumerReviewFoodName.observe(this, asConsumerReviewFoodName -> {
            reviewsFoodNameTextView.setText(asConsumerReviewFoodName);
        });

        userProfileViewModel.asConsumerReviewDate.observe(this, asConsumerReviewDate -> {
            Date reviewDate = asConsumerReviewDate.toDate();
            String reviewDateStr = simpleDateFormat.format(reviewDate);
            reviewsDateTextView.setText(reviewDateStr);
        });

        userProfileViewModel.asConsumerReviewRating.observe(this, asConsumerReviewRating -> {
            reviewsRatingBar.setRating(asConsumerReviewRating.floatValue());
        });

        userProfileViewModel.asConsumerReviewImageUrls.observe(this, asConsumerReviewImageUrls -> {
            reviewsImageShapeableImageViews.forEach(e -> {
                e.setVisibility(View.INVISIBLE);
            });
            for (int i = 0; i < asConsumerReviewImageUrls.size() && i < reviewsImageShapeableImageViews.size(); ++i) {
                reviewsImageShapeableImageViews.get(i).setVisibility(VISIBLE);
                Picasso.get().load(asConsumerReviewImageUrls.get(i)).into(reviewsImageShapeableImageViews.get(i));
            }
        });

        userProfileViewModel.asConsumerReviewText.observe(this, asConsumerReviewText -> {
            reviewsTextReviewTextView.setText(asConsumerReviewText);
        });

        // Badge
        userProfileViewModel.asConsumerGotBadgeCount.observe(this, asConsumerGotBadgeCount -> {
            usersConsumerBadgeCountTextView.setText(Integer.toString(asConsumerGotBadgeCount));
        });

        userProfileViewModel.asConsumerTotalBadgeCount.observe(this, asConsumerTotalBadgeCount -> {
            usersConsumerBadgeTotalCountTextView.setText(Integer.toString(asConsumerTotalBadgeCount));
        });

        userProfileViewModel.asConsumerBadges.observe(this, asConsumerBadges -> {
            createBadgeViews(asConsumerBadges, usersConsumerBadgeTableLayout,true);
        });

        // As Donor
        // On the shelf
        userProfileViewModel.asDonorTotalOnShelfCount.observe(this, asDonorTotalOnShelfCount -> {
            donorOnShelfCountTextView.setText(Integer.toString(asDonorTotalOnShelfCount));
            if (asDonorTotalOnShelfCount == 0) {
                donorOnShelfLinearLayout.setVisibility(View.GONE);
            } else {
                donorOnShelfLinearLayout.setVisibility(VISIBLE);
            }
        });

        userProfileViewModel.asDonorFoodName.observe(this, asDonorFoodName -> {
            donorOnShelfFoodNameTextView.setText(asDonorFoodName);
        });

        userProfileViewModel.asDonorFoodDateOn.observe(this, asDonorFoodDateOn -> {
            donorOnShelfDateOnTextView.setText(simpleDateFormat.format(asDonorFoodDateOn.toDate()));
        });

        userProfileViewModel.asDonorFoodDateExpire.observe(this, asDonorFoodDateExpire -> {
            donorOnShelfDateExpireTextView.setText(simpleDateFormat.format(asDonorFoodDateExpire.toDate()));
        });

        userProfileViewModel.asDonorFoodImageUrls.observe(this, asDonorFoodImageUrls -> {
            donorOnShelfFoodImageShapeableImageViews.forEach(e -> e.setVisibility(INVISIBLE));
            for (int i = 0; i < donorOnShelfFoodImageShapeableImageViews.size() && i < asDonorFoodImageUrls.size(); ++i) {
                donorOnShelfFoodImageShapeableImageViews.get(i).setVisibility(VISIBLE);
                Picasso.get().load(asDonorFoodImageUrls.get(i)).into(donorOnShelfFoodImageShapeableImageViews.get(i));
            }
        });

        userProfileViewModel.asDonorFoodTextDescription.observe(this, asDonorFoodTextDescription -> {
            donorReviewedItemConsumerTextView.setText(asDonorFoodTextDescription);
        });

        // Reviewed
        userProfileViewModel.asDonorTotalReviewedCount.observe(this, asDonorTotalReviewedCount -> {
            donorReviewedItemCountTextView.setText(Integer.toString(asDonorTotalReviewedCount));
            if (asDonorTotalReviewedCount.equals(0)){
                donorReviewedItemLinearLayout.setVisibility(View.GONE);
            }else {
                donorReviewedItemLinearLayout.setVisibility(VISIBLE);
            }
        });

        userProfileViewModel.asDonorReviewedConsumerName.observe(this, asDonorReviewedConsumerName -> {
            donorReviewedItemConsumerTextView.setText(asDonorReviewedConsumerName);
        });

        userProfileViewModel.asDonorReviewedFoodName.observe(this, asDonorReviewedFoodName -> {
            donorReviewedItemFoodNameTextView.setText(asDonorReviewedFoodName);
        });

        userProfileViewModel.asDonorReviewedDate.observe(this, asDonorReviewedDate -> {
            donorReviewedItemReviewedDateTextView.setText(simpleDateFormat.format(asDonorReviewedDate.toDate()));
        });

        userProfileViewModel.asDonorReviewedRating.observe(this, asDonorReviewedRating -> {
            donorReviewedItemRatingBar.setRating(asDonorReviewedRating.floatValue());
        });

        userProfileViewModel.asDonorReviewedImageUrls.observe(this, asDonorReviewedImageUrls -> {
            donorReviewedItemImageShapeableImageViews.forEach(e -> e.setVisibility(INVISIBLE));
            for (int i = 0; i < donorReviewedItemImageShapeableImageViews.size() && i < asDonorReviewedImageUrls.size(); ++i) {
                donorReviewedItemImageShapeableImageViews.get(i).setVisibility(VISIBLE);
                Picasso.get().load(asDonorReviewedImageUrls.get(i)).into(donorReviewedItemImageShapeableImageViews.get(i));
            }
        });

        userProfileViewModel.asDonorReviewedText.observe(this, asDonorReviewedText -> {
            donorReviewedItemTextDescriptionTextView.setText(asDonorReviewedText);
        });

        // Badge
        userProfileViewModel.asDonorGotBadgeCount.observe(this, asDonorGotBadgeCount -> {
            usersDonorBadgeCountTextView.setText(Integer.toString(asDonorGotBadgeCount));
        });

        userProfileViewModel.asDonorTotalBadgeCount.observe(this, asDonorTotalBadgeCount -> {
            usersDonorBadgeTotalCountTextView.setText(Integer.toString(asDonorTotalBadgeCount));
        });

        userProfileViewModel.asDonorBadges.observe(this, asDonorBadges -> {
            createBadgeViews(asDonorBadges, usersDonorBadgeTableLayout,false);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRegisterAndLogin();
    }

    private void createBadgeViews(ArrayList<Number> badges, TableLayout tableLayout, boolean consumer) {
        HashMap<Long, Badge> badgesHashMap = aConst.DONOR_BADGES;
        if(consumer){
            badgesHashMap = aConst.CONSUMER_BADGES;
        }
        View testView = LayoutInflater.from(this).inflate(R.layout.badg_row, null);
        int childCount = ((ViewGroup) testView).getChildCount();
        int count = badges.size();
        int single = count % childCount;
        int lines = count / childCount;
        Iterator<Number> iterator = badges.iterator();
        for (int i = 0; i < lines; ++i) {
            View view = LayoutInflater.from(this).inflate(R.layout.badg_row,
                    tableLayout, false);
            tableLayout.addView(view);
            for (int index = 0; index < ((ViewGroup) view).getChildCount(); index++) {
                CircleImageView imageView = (CircleImageView) ((ViewGroup) view).getChildAt(index);
                Number num = iterator.next();
                imageView.setImageResource(badgesHashMap.get(num).resourceId);
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
                Number num = iterator.next();
                imageView.setImageResource(badgesHashMap.get(num).resourceId);
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
        usersConsumerBadgeTotalCountTextView = findViewById(R.id.userProfile_usersConsumerBadgeTotalCountTextView);
        usersConsumerBadgeTableLayout = findViewById(R.id.userProfile_usersConsumerBadgeTableLayout);
        usersDonorBadgeCountTextView = findViewById(R.id.userProfile_usersDonorBadgeCountTextView);
        usersDonorBadgeTotalCountTextView = findViewById(R.id.userProfile_usersDonorBadgeTotalCountTextView);
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
        donorReviewedItemFoodNameTextView = findViewById(R.id.userProfile_donorReviewedItemFoodNameTextView);
        ;
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
    private void firebaseRegisterAndLogin() {
        String email = getResources().getString(R.string.firebase_email);
        String password = getResources().getString(R.string.firebase_password);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.isSignInWithEmailLink(email)) {
            fetchDBInfo(userId);
        } else {
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
    }

    private void fetchDBInfo(String userId) {
        FirebaseFirestore.getInstance().collection(getResources().getString(R.string.FIREBASE_COLLECTION_USER_INFO)).document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    UsersInfo usersInfo = document.toObject(UsersInfo.class);
                    setUserProfileUI(usersInfo);
                    Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                } else {
                    Log.d("TAG", "No such document");
                }
            } else {
                int k = 1;
            }
        });
    }

    private void setUserProfileUI(UsersInfo usersInfo) {
        userProfileViewModel.currentUserInfo.setValue(usersInfo);
        // -- as Consumer --
        HashMap<String, Object> asConsumer = (HashMap<String, Object>) usersInfo.getAsConsumer();
        // review count
        ArrayList<DocumentReference> reviews =
                (ArrayList<DocumentReference>) asConsumer.get(getResources().getString(R.string.FIREBASE_COLLECTION_USER_INFO_SUB_KEY_OF_AS_CONSUMER_REVIEWS));
        if (reviews.size() > 0) {
            DocumentReference lastReviewRef = reviews.get(reviews.size() - 1);
            lastReviewRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // In review document
                    ReviewInfo reviewInfo = task.getResult().toObject(ReviewInfo.class);
                    userProfileViewModel.consumerReviewInfo.setValue(reviewInfo);
                    DocumentReference food = reviewInfo.getFoodRef();
                    food.get().addOnCompleteListener(foodTask -> {
                        if (foodTask.isSuccessful()) {
                            userProfileViewModel.consumerReviewFoodInfo.setValue(foodTask.getResult().toObject(FoodItemInfo.class));
                        }
                    });
                    DocumentReference donor =
                            reviewInfo.getToUser();
                    donor.get().addOnCompleteListener(donorTask -> {
                        if (donorTask.isSuccessful()) {
                            userProfileViewModel.consumerReviewDonorUserInfo.setValue(donorTask.getResult().toObject(UsersInfo.class));
                        }
                    });
                }
            });
        }

        // -- as Donor --
        HashMap<String, Object> asDonor = (HashMap<String, Object>) usersInfo.getAsDonor();
        // on shelf
        ArrayList<DocumentReference> itemsOnShelf =
                (ArrayList<DocumentReference>) asDonor.get(getResources().getString(R.string.ITEMS_ON_SHELF));
        if (itemsOnShelf.size() > 0) {
            DocumentReference docRef = itemsOnShelf.get(itemsOnShelf.size() - 1);
            docRef.get().addOnCompleteListener(foodTask -> {
                if (foodTask.isSuccessful()) {
                    userProfileViewModel.donorOnShelfFoodInfo.setValue(foodTask.getResult().toObject(FoodItemInfo.class));
                }
            });
        }

        // reviewed
        ArrayList<DocumentReference> itemsReviewed =
                (ArrayList<DocumentReference>) asDonor.get(getResources().getString(R.string.ITEMS_REVIEWED));
        if (itemsReviewed.size() > 0) {
            DocumentReference docRef = itemsReviewed.get(itemsReviewed.size() - 1);
            docRef.get().addOnCompleteListener(reviewedItemTask -> {
                if (reviewedItemTask.isSuccessful()) {
                    userProfileViewModel.donorReviewedInfo.setValue(reviewedItemTask.getResult().toObject(ReviewInfo.class));
                    userProfileViewModel.donorReviewedInfo.getValue().getFromUser().get().addOnCompleteListener(userTask -> {
                        if (userTask.isSuccessful()) {
                            userProfileViewModel.donorReviewedUserInfo.setValue(userTask.getResult().toObject(UsersInfo.class));
                        }
                    });
                    userProfileViewModel.donorReviewedInfo.getValue().getFoodRef().get().addOnCompleteListener(foodTask -> {
                        if (foodTask.isSuccessful()) {
                            userProfileViewModel.donorReviewedFoodInfo.setValue(foodTask.getResult().toObject(FoodItemInfo.class));
                        }
                    });
                }
            });
        }
    }
}
