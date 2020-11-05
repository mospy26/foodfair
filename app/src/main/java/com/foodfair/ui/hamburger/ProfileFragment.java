package com.foodfair.ui.hamburger;

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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.foodfair.R;
import com.foodfair.model.Badge;
import com.foodfair.model.FoodItemInfo;
import com.foodfair.model.ReviewInfo;
import com.foodfair.model.UsersInfo;
import com.foodfair.utilities.Const;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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

public class ProfileFragment extends Fragment {

    ProfileViewModel profileViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = new ProfileViewModel();
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        userId = user.getUid();
        userId = "yXnhEl9OBqgKqHLAPMPV";
        profileViewModel.id.setValue(userId);
        profileViewModel.asConsumerTotalBadgeCount.setValue(aConst.CONSUMER_BADGES.size());
        profileViewModel.asDonorTotalBadgeCount.setValue(aConst.DONOR_BADGES.size());

        root = inflater.inflate(R.layout.user_profile, container, false);
        InitUI();
        viewModelObserverSetup();
//        final TextView textView = root.findViewById(R.id.text_profile);
//        profileViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }
    View root;
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


    private void viewModelObserverSetup() {
        profileViewModel.currentUserInfo.observe(getViewLifecycleOwner(), currentUserInfo -> {
            profileViewModel.profileImageUrl.setValue(currentUserInfo.getProfileImage());
            profileViewModel.name.setValue(currentUserInfo.getName());
            profileViewModel.gender.setValue(currentUserInfo.getGender().intValue());
//            userProfileViewModel.id.setValue(userId);
            profileViewModel.birthday.setValue(currentUserInfo.getBirthday());
            profileViewModel.bio.setValue(currentUserInfo.getBio());
            profileViewModel.allergy.setValue((ArrayList<Long>) currentUserInfo.getAllergy());
            profileViewModel.preference.setValue(currentUserInfo.getPreference());
            profileViewModel.location.setValue(currentUserInfo.getLocation().toString());
            profileViewModel.joinDate.setValue(currentUserInfo.getJoinDate());
            profileViewModel.lastLoginDate.setValue(currentUserInfo.getLastLogin());

            // as consumer
            Map<String,Object> asConsumer = currentUserInfo.getAsConsumer();
            ArrayList<DocumentReference> reviews =
                    (ArrayList<DocumentReference>) asConsumer.get(getResources().getString(R.string.FIREBASE_COLLECTION_USER_INFO_SUB_KEY_OF_AS_CONSUMER_REVIEWS));
            profileViewModel.asConsumerTotalReviewCount.setValue(reviews.size());

            ArrayList<Number> consumerBadges =
                    (ArrayList<Number>) asConsumer.get(getResources().getString(R.string.FIREBASE_COLLECTION_USER_INFO_SUB_KEY_OF_AS_CONSUMER_BADGES));
            profileViewModel.asConsumerGotBadgeCount.setValue(consumerBadges.size());
            profileViewModel.asConsumerBadges.setValue(consumerBadges);

            // as donor
            HashMap<String, Object> asDonor = (HashMap<String, Object>) currentUserInfo.getAsDonor();
            ArrayList<DocumentReference> itemsOnShelf =
                    (ArrayList<DocumentReference>) asDonor.get(getResources().getString(R.string.ITEMS_ON_SHELF));
            profileViewModel.asDonorTotalOnShelfCount.setValue(itemsOnShelf.size());
            ArrayList<DocumentReference> itemsReviewed =
                    (ArrayList<DocumentReference>) asDonor.get(getResources().getString(R.string.ITEMS_REVIEWED));
            profileViewModel.asDonorTotalReviewedCount.setValue(itemsReviewed.size());

            ArrayList<Number> donorBadges =
                    (ArrayList<Number>) asDonor.get(getResources().getString(R.string.FIREBASE_COLLECTION_USER_INFO_SUB_KEY_OF_AS_DONOR_BADGES));
            profileViewModel.asDonorGotBadgeCount.setValue(donorBadges.size());
            profileViewModel.asDonorBadges.setValue(donorBadges);

        });
        profileViewModel.consumerReviewInfo.observe(getViewLifecycleOwner(), consumerReviewInfo -> {
            profileViewModel.asConsumerReviewDate.setValue(consumerReviewInfo.getDate());
            profileViewModel.asConsumerReviewRating.setValue(consumerReviewInfo.getRating());
            profileViewModel.asConsumerReviewText.setValue(consumerReviewInfo.getTextReview());
            profileViewModel.asConsumerReviewImageUrls.setValue(consumerReviewInfo.getImageReviews());
        });
        profileViewModel.consumerReviewDonorUserInfo.observe(getViewLifecycleOwner(), consumerReviewDonorUserInfo -> {
            profileViewModel.asConsumerReviewDonorName.setValue(consumerReviewDonorUserInfo.getName());
        });
        profileViewModel.consumerReviewFoodInfo.observe(getViewLifecycleOwner(), consumerReviewFoodInfo -> {
            profileViewModel.asConsumerReviewFoodName.setValue(consumerReviewFoodInfo.getName());
        });
        profileViewModel.donorOnShelfFoodInfo.observe(getViewLifecycleOwner(), donorOnShelfFoodInfo -> {
            profileViewModel.asDonorFoodName.setValue(donorOnShelfFoodInfo.getName());
            profileViewModel.asDonorFoodDateOn.setValue(donorOnShelfFoodInfo.getDateOn());
            profileViewModel.asDonorFoodDateExpire.setValue(donorOnShelfFoodInfo.getDateExpire());
            profileViewModel.asDonorFoodImageUrls.setValue(donorOnShelfFoodInfo.getImageDescription());
            profileViewModel.asDonorFoodTextDescription.setValue(donorOnShelfFoodInfo.getTextDescription());
        });
        profileViewModel.donorReviewedInfo.observe(getViewLifecycleOwner(), donorReviewedInfo -> {
            profileViewModel.asDonorReviewedDate.setValue(donorReviewedInfo.getDate());
            profileViewModel.asDonorReviewedRating.setValue(donorReviewedInfo.getRating());
            profileViewModel.asDonorReviewedImageUrls.setValue(donorReviewedInfo.getImageReviews());
            profileViewModel.asDonorReviewedText.setValue(donorReviewedInfo.getTextReview());
        });
        profileViewModel.donorReviewedUserInfo.observe(getViewLifecycleOwner(), donorReviewedUserInfo -> {
            profileViewModel.asDonorReviewedConsumerName.setValue(donorReviewedUserInfo.getName());
        });
        profileViewModel.donorReviewedFoodInfo.observe(getViewLifecycleOwner(), donorReviewedFoodInfo -> {
            profileViewModel.asDonorReviewedFoodName.setValue(donorReviewedFoodInfo.getName());
        });

        // ---------
        profileViewModel.profileImageUrl.observe(getViewLifecycleOwner(), profileImageUrl -> {
            Picasso.get().load(profileImageUrl).into(profileCircleImageView);
        });
        profileViewModel.name.observe(getViewLifecycleOwner(), name -> {
            nameTextView.setText(name);
        });

        profileViewModel.gender.observe(getViewLifecycleOwner(), gender -> {
            // gender
            if (gender.equals(getResources().getInteger(R.integer.FIREBASE_COLLECTION_USER_INFO_GENDER_VALUE_MALE))) {
                genderImageView.setImageResource(R.drawable.icons8_male_96);
            } else {
                genderImageView.setImageResource(R.drawable.icons8_female_96);
            }
        });

        profileViewModel.id.observe(getViewLifecycleOwner(), id -> {
            userIdTextView.setText("@" + id);
        });

        profileViewModel.birthday.observe(getViewLifecycleOwner(), birthday -> {
            Date dateBirthday = birthday.toDate();
            Date now = new Date();
            long diffInMillies = now.getTime() - dateBirthday.getTime();
            long year = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) / 365;
            ageTextView.setText(Long.toString(year));
        });

        profileViewModel.bio.observe(getViewLifecycleOwner(), bio -> {
            bioTextView.setText(bio);
        });

        profileViewModel.allergy.observe(getViewLifecycleOwner(), allergy -> {
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

        profileViewModel.preference.observe(getViewLifecycleOwner(), preference -> {
            String preference_text = aConst.FOOD_TYPE_DETAIL.get(preference);
            preferenceTextView.setText(preference_text);
        });

        profileViewModel.location.observe(getViewLifecycleOwner(), location -> {
            locationTextView.setText(location);
        });

        profileViewModel.joinDate.observe(getViewLifecycleOwner(), joinDate -> {
            if (joinDate != null) {
                Date date = joinDate.toDate();
                String joinDateText = simpleDateFormat.format(date);
                joinDateTextView.setText(joinDateText);
            }
        });

        profileViewModel.lastLoginDate.observe(getViewLifecycleOwner(), lastLoginDate -> {
            if (lastLoginDate != null) {
                Date date = lastLoginDate.toDate();
                String lastLoginDateText = simpleDateFormat.format(date);
                lastLoginDateTextView.setText(lastLoginDateText);
            }
        });

        // As Consumer
        // Review
        profileViewModel.asConsumerTotalReviewCount.observe(getViewLifecycleOwner(), asConsumerTotalReviewCount -> {
            String reviewsCountStr = Integer.toString(asConsumerTotalReviewCount);
            reviewsCountTextView.setText(reviewsCountStr);
            if (asConsumerTotalReviewCount.equals(0)) {
                reviewsLinearLayout.setVisibility(View.GONE);
            } else {
                reviewsLinearLayout.setVisibility(VISIBLE);
            }
        });

        profileViewModel.asConsumerReviewDonorName.observe(getViewLifecycleOwner(), asConsumerReviewDonorName -> {
            reviewsDonorNameTextView.setText(asConsumerReviewDonorName);
        });

        profileViewModel.asConsumerReviewFoodName.observe(getViewLifecycleOwner(), asConsumerReviewFoodName -> {
            reviewsFoodNameTextView.setText(asConsumerReviewFoodName);
        });

        profileViewModel.asConsumerReviewDate.observe(getViewLifecycleOwner(), asConsumerReviewDate -> {
            Date reviewDate = asConsumerReviewDate.toDate();
            String reviewDateStr = simpleDateFormat.format(reviewDate);
            reviewsDateTextView.setText(reviewDateStr);
        });

        profileViewModel.asConsumerReviewRating.observe(getViewLifecycleOwner(), asConsumerReviewRating -> {
            reviewsRatingBar.setRating(asConsumerReviewRating.floatValue());
        });

        profileViewModel.asConsumerReviewImageUrls.observe(getViewLifecycleOwner(), asConsumerReviewImageUrls -> {
            reviewsImageShapeableImageViews.forEach(e -> {
                e.setVisibility(View.INVISIBLE);
            });
            for (int i = 0; i < asConsumerReviewImageUrls.size() && i < reviewsImageShapeableImageViews.size(); ++i) {
                reviewsImageShapeableImageViews.get(i).setVisibility(VISIBLE);
                Picasso.get().load(asConsumerReviewImageUrls.get(i)).into(reviewsImageShapeableImageViews.get(i));
            }
        });

        profileViewModel.asConsumerReviewText.observe(getViewLifecycleOwner(), asConsumerReviewText -> {
            reviewsTextReviewTextView.setText(asConsumerReviewText);
        });

        // Badge
        profileViewModel.asConsumerGotBadgeCount.observe(getViewLifecycleOwner(), asConsumerGotBadgeCount -> {
            usersConsumerBadgeCountTextView.setText(Integer.toString(asConsumerGotBadgeCount));
        });

        profileViewModel.asConsumerTotalBadgeCount.observe(getViewLifecycleOwner(), asConsumerTotalBadgeCount -> {
            usersConsumerBadgeTotalCountTextView.setText(Integer.toString(asConsumerTotalBadgeCount));
        });

        profileViewModel.asConsumerBadges.observe(getViewLifecycleOwner(), asConsumerBadges -> {
            createBadgeViews(asConsumerBadges, usersConsumerBadgeTableLayout,true);
        });

        // As Donor
        // On the shelf
        profileViewModel.asDonorTotalOnShelfCount.observe(getViewLifecycleOwner(), asDonorTotalOnShelfCount -> {
            donorOnShelfCountTextView.setText(Integer.toString(asDonorTotalOnShelfCount));
            if (asDonorTotalOnShelfCount == 0) {
                donorOnShelfLinearLayout.setVisibility(View.GONE);
            } else {
                donorOnShelfLinearLayout.setVisibility(VISIBLE);
            }
        });

        profileViewModel.asDonorFoodName.observe(getViewLifecycleOwner(), asDonorFoodName -> {
            donorOnShelfFoodNameTextView.setText(asDonorFoodName);
        });

        profileViewModel.asDonorFoodDateOn.observe(getViewLifecycleOwner(), asDonorFoodDateOn -> {
            donorOnShelfDateOnTextView.setText(simpleDateFormat.format(asDonorFoodDateOn.toDate()));
        });

        profileViewModel.asDonorFoodDateExpire.observe(getViewLifecycleOwner(), asDonorFoodDateExpire -> {
            donorOnShelfDateExpireTextView.setText(simpleDateFormat.format(asDonorFoodDateExpire.toDate()));
        });

        profileViewModel.asDonorFoodImageUrls.observe(getViewLifecycleOwner(), asDonorFoodImageUrls -> {
            donorOnShelfFoodImageShapeableImageViews.forEach(e -> e.setVisibility(INVISIBLE));
            for (int i = 0; i < donorOnShelfFoodImageShapeableImageViews.size() && i < asDonorFoodImageUrls.size(); ++i) {
                donorOnShelfFoodImageShapeableImageViews.get(i).setVisibility(VISIBLE);
                Picasso.get().load(asDonorFoodImageUrls.get(i)).into(donorOnShelfFoodImageShapeableImageViews.get(i));
            }
        });

        profileViewModel.asDonorFoodTextDescription.observe(getViewLifecycleOwner(), asDonorFoodTextDescription -> {
            donorReviewedItemConsumerTextView.setText(asDonorFoodTextDescription);
        });

        // Reviewed
        profileViewModel.asDonorTotalReviewedCount.observe(getViewLifecycleOwner(), asDonorTotalReviewedCount -> {
            donorReviewedItemCountTextView.setText(Integer.toString(asDonorTotalReviewedCount));
            if (asDonorTotalReviewedCount.equals(0)){
                donorReviewedItemLinearLayout.setVisibility(View.GONE);
            }else {
                donorReviewedItemLinearLayout.setVisibility(VISIBLE);
            }
        });

        profileViewModel.asDonorReviewedConsumerName.observe(getViewLifecycleOwner(), asDonorReviewedConsumerName -> {
            donorReviewedItemConsumerTextView.setText(asDonorReviewedConsumerName);
        });

        profileViewModel.asDonorReviewedFoodName.observe(getViewLifecycleOwner(), asDonorReviewedFoodName -> {
            donorReviewedItemFoodNameTextView.setText(asDonorReviewedFoodName);
        });

        profileViewModel.asDonorReviewedDate.observe(getViewLifecycleOwner(), asDonorReviewedDate -> {
            donorReviewedItemReviewedDateTextView.setText(simpleDateFormat.format(asDonorReviewedDate.toDate()));
        });

        profileViewModel.asDonorReviewedRating.observe(getViewLifecycleOwner(), asDonorReviewedRating -> {
            donorReviewedItemRatingBar.setRating(asDonorReviewedRating.floatValue());
        });

        profileViewModel.asDonorReviewedImageUrls.observe(getViewLifecycleOwner(), asDonorReviewedImageUrls -> {
            donorReviewedItemImageShapeableImageViews.forEach(e -> e.setVisibility(INVISIBLE));
            for (int i = 0; i < donorReviewedItemImageShapeableImageViews.size() && i < asDonorReviewedImageUrls.size(); ++i) {
                donorReviewedItemImageShapeableImageViews.get(i).setVisibility(VISIBLE);
                Picasso.get().load(asDonorReviewedImageUrls.get(i)).into(donorReviewedItemImageShapeableImageViews.get(i));
            }
        });

        profileViewModel.asDonorReviewedText.observe(getViewLifecycleOwner(), asDonorReviewedText -> {
            donorReviewedItemTextDescriptionTextView.setText(asDonorReviewedText);
        });

        // Badge
        profileViewModel.asDonorGotBadgeCount.observe(getViewLifecycleOwner(), asDonorGotBadgeCount -> {
            usersDonorBadgeCountTextView.setText(Integer.toString(asDonorGotBadgeCount));
        });

        profileViewModel.asDonorTotalBadgeCount.observe(getViewLifecycleOwner(), asDonorTotalBadgeCount -> {
            usersDonorBadgeTotalCountTextView.setText(Integer.toString(asDonorTotalBadgeCount));
        });

        profileViewModel.asDonorBadges.observe(getViewLifecycleOwner(), asDonorBadges -> {
            createBadgeViews(asDonorBadges, usersDonorBadgeTableLayout,false);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseRegisterAndLogin();
    }

    private void createBadgeViews(ArrayList<Number> badges, TableLayout tableLayout, boolean consumer) {
        HashMap<Long, Badge> badgesHashMap = aConst.DONOR_BADGES;
        if(consumer){
            badgesHashMap = aConst.CONSUMER_BADGES;
        }
        tableLayout.removeAllViews();
        View testView = LayoutInflater.from(getContext()).inflate(R.layout.badg_row, null);
        int childCount = ((ViewGroup) testView).getChildCount();
        int count = badges.size();
        int single = count % childCount;
        int lines = count / childCount;
        Iterator<Number> iterator = badges.iterator();
        for (int i = 0; i < lines; ++i) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.badg_row,
                    tableLayout, false);
            tableLayout.addView(view);
            for (int index = 0; index < ((ViewGroup) view).getChildCount(); index++) {
                CircleImageView imageView = (CircleImageView) ((ViewGroup) view).getChildAt(index);
                Number num = iterator.next();
                imageView.setImageResource(badgesHashMap.get(num).resourceId);
            }
        }

        if (single != 0) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.badg_row,
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
    <T extends View> T findViewById(int id){
        return (T)(root.findViewById(id));
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
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
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
        profileViewModel.currentUserInfo.setValue(usersInfo);
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
                    profileViewModel.consumerReviewInfo.setValue(reviewInfo);
                    DocumentReference food = reviewInfo.getFoodRef();
                    food.get().addOnCompleteListener(foodTask -> {
                        if (foodTask.isSuccessful()) {
                            profileViewModel.consumerReviewFoodInfo.setValue(foodTask.getResult().toObject(FoodItemInfo.class));
                        }
                    });
                    DocumentReference donor =
                            reviewInfo.getToUser();
                    donor.get().addOnCompleteListener(donorTask -> {
                        if (donorTask.isSuccessful()) {
                            profileViewModel.consumerReviewDonorUserInfo.setValue(donorTask.getResult().toObject(UsersInfo.class));
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
                    profileViewModel.donorOnShelfFoodInfo.setValue(foodTask.getResult().toObject(FoodItemInfo.class));
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
                    profileViewModel.donorReviewedInfo.setValue(reviewedItemTask.getResult().toObject(ReviewInfo.class));
                    profileViewModel.donorReviewedInfo.getValue().getFromUser().get().addOnCompleteListener(userTask -> {
                        if (userTask.isSuccessful()) {
                            profileViewModel.donorReviewedUserInfo.setValue(userTask.getResult().toObject(UsersInfo.class));
                        }
                    });
                    profileViewModel.donorReviewedInfo.getValue().getFoodRef().get().addOnCompleteListener(foodTask -> {
                        if (foodTask.isSuccessful()) {
                            profileViewModel.donorReviewedFoodInfo.setValue(foodTask.getResult().toObject(FoodItemInfo.class));
                        }
                    });
                }
            });
        }
    }
}
