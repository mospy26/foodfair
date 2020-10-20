package com.foodfair.ui.profiles;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.foodfair.R;
import com.foodfair.ui.book_success.BookSuccessViewModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class UserProfileActivity extends AppCompatActivity {
    // Model
    private UserProfileViewModel mBookSuccessViewModel;
    // UI
    private SupportMapFragment mDynamicMapFragment;
    private TextView mDonorNameTextView;
    private TextView mFoodNameTextView;
    private TextView mPickupLocationTextView;
    private TextView mCountdownTextView;
    private GoogleMap mGoogleMap;
    private ImageView mQRCodeImageView;
    private ImageView mFoodImageView;
    private Button mBackButton;

    // Configure
    private float mZoomLevel = 18f;
    private LatLng mLocation = new LatLng(-33.86785, 151.20732);
    private int mWidth = 300;
    private int mHeight = 300;
    private int mPaddingTop = 120;
    private float mQRCodeCropRatio = 0.8f;

    // Other
    private Marker mMarker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
    }
}
