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
import com.foodfair.utilities.Cache;
import com.foodfair.utilities.Const;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.concurrent.atomic.AtomicReference;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static android.view.View.inflate;

public class ProfileFragment extends Fragment {

    ProfileViewModel profileViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        cache = Cache.getInstance(getContext());
        profileViewModel = new ProfileViewModel();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();


        root = inflater.inflate(R.layout.user_profile, container, false);
        headLayout = findViewById(R.id.header_linearLayout);
//        InitUI();
        viewModelObserverSetup();
        return root;
    }

    View root;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    Cache cache = null;
    Const aConst = Const.getInstance();
    public String userId;
    public CircleImageView profileCircleImageView;
    public TextView nameTextView;
    public TextView typeTextView;
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

    public LinearLayout headLayout;
    public LinearLayout wholeAsConsumer;
    public LinearLayout wholeAsDonor;
    public LinearLayout baseUserLayout;


    private void viewModelObserverSetup() {
        profileViewModel.currentUserInfo.observe(getViewLifecycleOwner(), currentUserInfo -> {
            View userBaseLayout = findViewById(R.id.userProfile_userBaseLayout);
            if (userBaseLayout == null){
                View view = LayoutInflater.from(getContext()).inflate(R.layout.user_profile_baseuser,
                        headLayout, false);
                headLayout.addView(view);
                InitUI();
            }


            profileViewModel.id.setValue(userId);
            profileViewModel.profileImageUrl.setValue(currentUserInfo.getProfileImage());
            profileViewModel.name.setValue(currentUserInfo.getName());
            profileViewModel.type.setValue(currentUserInfo.getStatus());
            profileViewModel.gender.setValue(currentUserInfo.getGender().intValue());
            profileViewModel.birthday.setValue(currentUserInfo.getBirthday());
            profileViewModel.bio.setValue(currentUserInfo.getBio());
            profileViewModel.allergy.setValue((ArrayList<Long>) currentUserInfo.getAllergy());
            profileViewModel.preference.setValue(currentUserInfo.getPreference());
                profileViewModel.location.setValue(currentUserInfo.getLocation().toString());
            profileViewModel.joinDate.setValue(currentUserInfo.getJoinDate());
            profileViewModel.lastLoginDate.setValue(currentUserInfo.getLastLogin());

            // as consumer
            Map<String, Object> asConsumer = currentUserInfo.getAsConsumer();
            if (asConsumer != null) {
                View wholeAsConsumerLayout = findViewById(R.id.userProfile_wholeAsConsumer);
                if (wholeAsConsumerLayout == null){
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.user_profile_consumer,
                            headLayout, false);
                    headLayout.addView(view);
                    InitUI();
                    profileViewModel.asConsumerTotalBadgeCount.setValue(aConst.CONSUMER_BADGES.size());
                }
                ArrayList<DocumentReference> reviews =
                        (ArrayList<DocumentReference>) asConsumer.get(getResources().getString(R.string.FIREBASE_COLLECTION_USER_INFO_SUB_KEY_OF_AS_CONSUMER_REVIEWS));
                profileViewModel.asConsumerTotalReviewCount.setValue(reviews.size());
                ArrayList<Number> consumerBadges =
                        (ArrayList<Number>) asConsumer.get(getResources().getString(R.string.FIREBASE_COLLECTION_USER_INFO_SUB_KEY_OF_AS_CONSUMER_BADGES));
                profileViewModel.asConsumerGotBadgeCount.setValue(consumerBadges.size());
                profileViewModel.asConsumerBadges.setValue(consumerBadges);
            }

            // as donor
            HashMap<String, Object> asDonor = (HashMap<String, Object>) currentUserInfo.getAsDonor();
            if (asDonor != null) {
                View wholeAsDonorLayout = findViewById(R.id.userProfile_wholeAsDonor);
                if (wholeAsDonorLayout == null){
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.user_profile_donor,
                            headLayout, false);
                    headLayout.addView(view);
                    InitUI();
                    profileViewModel.asDonorTotalBadgeCount.setValue(aConst.DONOR_BADGES.size());
                }
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
            }
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
            if (profileImageUrl != null){
                Picasso.get().load(profileImageUrl).into(profileCircleImageView);
            }
        });
        profileViewModel.name.observe(getViewLifecycleOwner(), name -> {
            nameTextView.setText(name);
        });
        profileViewModel.type.observe(getViewLifecycleOwner(), type -> {
            if (type.equals(0L)){
                typeTextView.setText("Restaurant");
            }
            if (type.equals(1L)){
                typeTextView.setText("User");
            }
            if (type.equals(2L)){
                typeTextView.setText("Charity");
            }
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
            if (birthday == null) {
                ageTextView.setText("Not Provided.");
            } else {
                Date dateBirthday = birthday.toDate();
                Date now = new Date();
                long diffInMillies = now.getTime() - dateBirthday.getTime();
                long year = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) / 365;
                ageTextView.setText(Long.toString(year));
            }
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
            createBadgeViews(asConsumerBadges, usersConsumerBadgeTableLayout, true);
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
            donorOnShelfTextDescriptionTextView.setText(asDonorFoodTextDescription);
        });

        // Reviewed
        profileViewModel.asDonorTotalReviewedCount.observe(getViewLifecycleOwner(), asDonorTotalReviewedCount -> {
            donorReviewedItemCountTextView.setText(Integer.toString(asDonorTotalReviewedCount));
            if (asDonorTotalReviewedCount.equals(0)) {
                donorReviewedItemLinearLayout.setVisibility(View.GONE);
            } else {
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
            createBadgeViews(asDonorBadges, usersDonorBadgeTableLayout, false);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchDBInfo(userId);
    }

    private void createBadgeViews(ArrayList<Number> badges, TableLayout tableLayout, boolean consumer) {
        HashMap<Long, Badge> badgesHashMap = aConst.DONOR_BADGES;
        if (consumer) {
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

    <T extends View> T findViewById(int id) {
        return (T) (root.findViewById(id));
    }

    private void InitUI() {
        profileCircleImageView = findViewById(R.id.userProfile_profileCircleImageView);
        nameTextView = findViewById(R.id.userProfile_nameTextView);
        typeTextView = findViewById(R.id.userProfile_typeTextView);
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
        if (reviewsImageShapeableImageViews.isEmpty()){
            if (findViewById(R.id.userProfile_reviewsImageReviewShapeableImageView1) != null){
                reviewsImageShapeableImageViews.add(findViewById(R.id.userProfile_reviewsImageReviewShapeableImageView1));
                reviewsImageShapeableImageViews.add(findViewById(R.id.userProfile_reviewsImageReviewShapeableImageView2));
                reviewsImageShapeableImageViews.add(findViewById(R.id.userProfile_reviewsImageReviewShapeableImageView3));
                reviewsImageShapeableImageViews.add(findViewById(R.id.userProfile_reviewsImageReviewShapeableImageView4));
            }
        }
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
        if (donorOnShelfFoodImageShapeableImageViews.isEmpty()){
            if (findViewById(R.id.userProfile_donorOnShelfFoodImageImageView1) != null){
                donorOnShelfFoodImageShapeableImageViews.add(findViewById(R.id.userProfile_donorOnShelfFoodImageImageView1));
                donorOnShelfFoodImageShapeableImageViews.add(findViewById(R.id.userProfile_donorOnShelfFoodImageImageView2));
                donorOnShelfFoodImageShapeableImageViews.add(findViewById(R.id.userProfile_donorOnShelfFoodImageImageView3));
                donorOnShelfFoodImageShapeableImageViews.add(findViewById(R.id.userProfile_donorOnShelfFoodImageImageView4));
            }
        }
        donorOnShelfTextDescriptionTextView = findViewById(R.id.userProfile_donorOnShelfTextDescriptionTextView);
        donorReviewedItemCountTextView = findViewById(R.id.userProfile_donorReviewedItemCountTextView);
        donorReviewedItemLinearLayout = findViewById(R.id.userProfile_donorReviewedItemLinearLayout);

        donorReviewedItemConsumerTextView = findViewById(R.id.userProfile_donorReviewedItemConsumerTextView);
        donorReviewedItemFoodNameTextView = findViewById(R.id.userProfile_donorReviewedItemFoodNameTextView);
        ;
        donorReviewedItemReviewedDateTextView = findViewById(R.id.userProfile_donorReviewedItemReviewedDateTextView);
        donorReviewedItemRatingBar = findViewById(R.id.userProfile_donorReviewedItemRatingBar);
        if (donorReviewedItemImageShapeableImageViews.isEmpty()){
            if (findViewById(R.id.userProfile_donorReviewedItemFoodImageImageView1) != null){
                donorReviewedItemImageShapeableImageViews.add(findViewById(R.id.userProfile_donorReviewedItemFoodImageImageView1));
                donorReviewedItemImageShapeableImageViews.add(findViewById(R.id.userProfile_donorReviewedItemFoodImageImageView2));
                donorReviewedItemImageShapeableImageViews.add(findViewById(R.id.userProfile_donorReviewedItemFoodImageImageView3));
                donorReviewedItemImageShapeableImageViews.add(findViewById(R.id.userProfile_donorReviewedItemFoodImageImageView4));
            }
        }
        donorReviewedItemTextDescriptionTextView =
                findViewById(R.id.userProfile_donorReviewedItemTextDescriptionTextView);

        wholeAsConsumer = findViewById(R.id.userProfile_wholeAsConsumer);
        wholeAsDonor = findViewById(R.id.userProfile_wholeAsDonor);

    }

    private void fetchDBInfo(String userId) {
        UsersInfo usersInfoCached = (UsersInfo) cache.getStoredObject(userId);
        if (usersInfoCached == null){
            FirebaseFirestore.getInstance().collection(getResources().getString(R.string.FIREBASE_COLLECTION_USER_INFO)).document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        UsersInfo usersInfo = document.toObject(UsersInfo.class);
                        cache.add(userId,usersInfo);
                        setUserProfileUI(usersInfo);
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    int k = 1;
                }
            });
        }else{
            setUserProfileUI(usersInfoCached);
        }
    }
    private void setConsumerReviewFoodInfoUI(ReviewInfo reviewInfo){
        DocumentReference food = reviewInfo.getFoodRef();
        String foodId = food.getId();
        FoodItemInfo foodItemInfoCached = (FoodItemInfo) cache.getStoredObject(foodId);
        if (foodItemInfoCached == null){
            food.get().addOnCompleteListener(foodTask -> {
                if (foodTask.isSuccessful()) {
                    FoodItemInfo foodItemInfo =foodTask.getResult().toObject(FoodItemInfo.class);
                    cache.add(foodId,foodItemInfo);
                    profileViewModel.consumerReviewFoodInfo.setValue(foodItemInfo);
                }
            });
        }else {
            profileViewModel.consumerReviewFoodInfo.setValue(foodItemInfoCached);
        }
    }
    private void setDonorReviewFoodInfoUI(ReviewInfo reviewInfo) {
        DocumentReference documentReference = reviewInfo.getFoodRef();
        String foodId = documentReference.getId();
        FoodItemInfo foodItemInfoCached = (FoodItemInfo)cache.getStoredObject(foodId);
        if (foodItemInfoCached == null){
            documentReference.get().addOnCompleteListener(foodTask -> {
                if (foodTask.isSuccessful()) {
                    FoodItemInfo foodItemInfo = foodTask.getResult().toObject(FoodItemInfo.class);
                    cache.add(foodId,foodItemInfo);
                    profileViewModel.donorReviewedFoodInfo.setValue(foodItemInfo);
                }
            });
        }else {
            profileViewModel.donorReviewedFoodInfo.setValue(foodItemInfoCached);
        }
    }
    private void setDonorReviewUserInfoUI(ReviewInfo reviewInfo){
        DocumentReference userRef = reviewInfo.getFromUser();
        String userId = userRef.getId();
        UsersInfo usersInfoCached = (UsersInfo)cache.getStoredObject(userId);
        if (usersInfoCached == null){
            userRef.get().addOnCompleteListener(userTask -> {
                if (userTask.isSuccessful()) {
                    UsersInfo usersInfo = userTask.getResult().toObject(UsersInfo.class);
                    cache.add(userId,usersInfo);
                    profileViewModel.donorReviewedUserInfo.setValue(usersInfo);
                }
            });
        }else{
            profileViewModel.donorReviewedUserInfo.setValue(usersInfoCached);
        }
    }
    private void setConsumerReviewDonorUserInfoUI(ReviewInfo reviewInfo){
        DocumentReference donor =
                reviewInfo.getToUser();
        String donorId = donor.getId();
        UsersInfo donorInfoCached = (UsersInfo) cache.getStoredObject(donorId);
        if (donorInfoCached == null){
            donor.get().addOnCompleteListener(donorTask -> {
                if (donorTask.isSuccessful()) {
                    UsersInfo donorInfo = donorTask.getResult().toObject(UsersInfo.class);
                    cache.add(donorId,donorInfo);
                    profileViewModel.consumerReviewDonorUserInfo.setValue(donorInfo);
                }
            });
        }else{
            profileViewModel.consumerReviewDonorUserInfo.setValue(donorInfoCached);
        }
    }
    private void setUserProfileUI(UsersInfo usersInfo) {
        profileViewModel.currentUserInfo.setValue(usersInfo);
        // -- as Consumer --
        HashMap<String, Object> asConsumer = (HashMap<String, Object>) usersInfo.getAsConsumer();
        if (asConsumer != null) {
            // review count
            ArrayList<DocumentReference> reviews =
                    (ArrayList<DocumentReference>) asConsumer.get(getResources().getString(R.string.FIREBASE_COLLECTION_USER_INFO_SUB_KEY_OF_AS_CONSUMER_REVIEWS));
            if (reviews.size() > 0) {
                DocumentReference lastReviewRef = reviews.get(reviews.size() - 1);
                String reviewId = lastReviewRef.getId();
                ReviewInfo reviewInfoCached = (ReviewInfo)cache.getStoredObject(reviewId);
                if (reviewInfoCached == null){
                    lastReviewRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // In review document
                            ReviewInfo reviewInfo = task.getResult().toObject(ReviewInfo.class);
                            cache.add(reviewId,reviewInfo);
                            profileViewModel.consumerReviewInfo.setValue(reviewInfo);
                            setConsumerReviewFoodInfoUI(reviewInfo);
                            setConsumerReviewDonorUserInfoUI(reviewInfo);
                        }
                    });
                }else{
                    profileViewModel.consumerReviewInfo.setValue(reviewInfoCached);
                    setConsumerReviewFoodInfoUI(reviewInfoCached);
                    setConsumerReviewDonorUserInfoUI(reviewInfoCached);
                }
            }
        }


        // -- as Donor --
        HashMap<String, Object> asDonor = (HashMap<String, Object>) usersInfo.getAsDonor();
        if (asDonor != null) {
            // on shelf
            ArrayList<DocumentReference> itemsOnShelf =
                    (ArrayList<DocumentReference>) asDonor.get(getResources().getString(R.string.ITEMS_ON_SHELF));
            if (itemsOnShelf.size() > 0) {
                DocumentReference docRef = itemsOnShelf.get(itemsOnShelf.size() - 1);
                String itemId = docRef.getId();
                FoodItemInfo itemInfoCached = (FoodItemInfo) cache.getStoredObject(itemId);
                if (itemInfoCached == null){
                    docRef.get().addOnCompleteListener(foodTask -> {
                        if (foodTask.isSuccessful()) {
                            FoodItemInfo foodItemInfo = foodTask.getResult().toObject(FoodItemInfo.class);
                            cache.add(itemId,foodItemInfo);
                            profileViewModel.donorOnShelfFoodInfo.setValue(foodItemInfo);
                        }
                    });
                }else {
                    profileViewModel.donorOnShelfFoodInfo.setValue(itemInfoCached);
                }
            }

            // reviewed
            ArrayList<DocumentReference> itemsReviewed =
                    (ArrayList<DocumentReference>) asDonor.get(getResources().getString(R.string.ITEMS_REVIEWED));
            if (itemsReviewed.size() > 0) {
                DocumentReference docRef = itemsReviewed.get(itemsReviewed.size() - 1);
                String itemId = docRef.getId();
                ReviewInfo reviewInfoCached = (ReviewInfo) cache.getStoredObject(itemId);
                if (reviewInfoCached == null){
                    docRef.get().addOnCompleteListener(reviewedItemTask -> {
                        if (reviewedItemTask.isSuccessful()) {
                            ReviewInfo reviewInfo = reviewedItemTask.getResult().toObject(ReviewInfo.class);
                            cache.add(itemId,reviewInfo);
                            profileViewModel.donorReviewedInfo.setValue(reviewInfo);

                            setDonorReviewUserInfoUI(reviewInfoCached);
                            setDonorReviewFoodInfoUI(reviewInfoCached);


                        }
                    });
                }else {
                    profileViewModel.donorReviewedInfo.setValue(reviewInfoCached);
                    setDonorReviewUserInfoUI(reviewInfoCached);
                    setDonorReviewFoodInfoUI(reviewInfoCached);
                }

            }
        }
    }


}
