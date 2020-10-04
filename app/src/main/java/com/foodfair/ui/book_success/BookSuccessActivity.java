package com.foodfair.ui.book_success;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.foodfair.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Date;

public class BookSuccessActivity extends AppCompatActivity {
    // Model
    private BookSuccessViewModel mBookSuccessViewModel;
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
    CountDownTimer mCountDownTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_success);
        InitUI();
        InitListener();
        mBookSuccessViewModel = new BookSuccessViewModel(BitmapFactory.decodeResource(getResources(),
                R.drawable.foodsample));
        ViewModelObserverSetup();
    }

    private void InitListener() {
        mBackButton.setOnClickListener(view->{
            finish();
        });
    }

    private void InitUI() {
        // Configure by xml (dynamic)
        GoogleMapOptions googleMapOptions = new GoogleMapOptions();
        googleMapOptions.camera(CameraPosition.builder().zoom(mZoomLevel).target(mLocation).build());
        googleMapOptions.liteMode(true);

        mDynamicMapFragment =
                SupportMapFragment.newInstance(googleMapOptions);
        FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.success_book_map, mDynamicMapFragment);
        fragmentTransaction.commit();
        mDynamicMapFragment.getMapAsync(googleMap -> {
            // Todo: adapt to different dpi device
            mGoogleMap = googleMap;
            mGoogleMap.setPadding(0, mPaddingTop, 0, 0);
            mGoogleMap.getUiSettings().setMapToolbarEnabled(true);
            mMarker = mGoogleMap.addMarker(new MarkerOptions().position(mLocation).title("Fetching Point"));

            mBookSuccessViewModel.getLocationLatLng().observe(this, latLng -> {
                if (mMarker != null) {
                    mMarker.remove();
                }
                mMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).title("Fetching Point"));
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            });
        });

        mDonorNameTextView = findViewById(R.id.success_donorNameTextView);
        mFoodNameTextView = findViewById(R.id.success_foodNameTextView);
        mPickupLocationTextView = findViewById(R.id.success_pickupLocationTextView);
        mCountdownTextView = findViewById(R.id.success_countdownTextView);
        mQRCodeImageView = findViewById(R.id.success_qrCodeImageView);
        mFoodImageView = findViewById(R.id.success_foodImageView);
        mBackButton = findViewById(R.id.success_backButton);
    }

    private void ViewModelObserverSetup() {
        // Not all live data is here, pickup location is not here
        // todo: why this?
        mBookSuccessViewModel.getDonorName().observe(this, donorName -> {
            mDonorNameTextView.setText(donorName);
        });

        mBookSuccessViewModel.getFoodBitmap().observe(this, foodBitmap -> {
            mFoodImageView.setImageBitmap(foodBitmap);
        });

        mBookSuccessViewModel.getFoodName().observe(this, foodName -> {
            mFoodNameTextView.setText(foodName);
        });

        mBookSuccessViewModel.getPickupLocation().observe(this, pickupLocation -> {
            mPickupLocationTextView.setText(pickupLocation);
        });

        mBookSuccessViewModel.getQRCodeContent().observe(this, qrCodeContent -> {
            // Set QRCode image
            try {
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.encodeBitmap("content", BarcodeFormat.QR_CODE, mWidth, mHeight);
                mQRCodeImageView.setImageBitmap(cropBitmapToSquare(bitmap, mQRCodeCropRatio));
            } catch (WriterException e) {
                e.printStackTrace();
            }
        });

        mBookSuccessViewModel.getDeadlineTimeStampToPickup().observe(this, deadlineTimeStampToPickup -> {
            if (mCountDownTimer != null) {
                mCountDownTimer.cancel();
            }

            mCountDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long remainSeconds = deadlineTimeStampToPickup - new Date().getTime()/1000;
                    mCountdownTextView.setText(secondsToTime(remainSeconds));
                    if (remainSeconds <= 0) {
                        onFinish();
                    }
                }

                @Override
                public void onFinish() {
                    // Todo tell firebase it is invalid
                    finish();
                }
            };
            mCountDownTimer.start();
        });
    }

    Bitmap cropBitmapToSquare(Bitmap bitmapToCrop, float ratio) {
        int w = (int) (bitmapToCrop.getWidth() * ratio);
        int h = (int) (bitmapToCrop.getHeight() * ratio);
        int cropWidth = w >= h ? h : w;
        return Bitmap.createBitmap(bitmapToCrop, (bitmapToCrop.getWidth() - cropWidth) / 2,
                (bitmapToCrop.getHeight() - cropWidth) / 2, cropWidth, cropWidth);
    }

    private String secondsToTime(long longSeconds) {
        long hours = longSeconds / 3600;
        int remainder = (int) (longSeconds - hours * 3600);
        int mins = remainder / 60;
        remainder = remainder - mins * 60;
        int secs = remainder;
        return String.format("%02d", hours) + ":" + String.format("%02d", mins) + ":" + String.format("%02d", secs);
    }
}
