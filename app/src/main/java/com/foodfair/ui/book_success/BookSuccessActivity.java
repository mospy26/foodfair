package com.foodfair.ui.book_success;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.foodfair.model.FoodItemInfo;
import com.foodfair.model.FooditemTransaction;
import com.foodfair.model.UsersInfo;
import com.foodfair.R;
import com.foodfair.task.UiHandler;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BookSuccessActivity extends AppCompatActivity {
    static HashMap<String, BookSuccessViewModel> modelInstances = new HashMap<>();

    // Model
    private BookSuccessViewModel mBookSuccessViewModel;
    String transactionId;
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


        this.transactionId = getIntent().getStringExtra("transactionId");
//        String transactionId = "HYcbU5p0vJKAJXI5AOCF";
//        this.transactionId = transactionId;
        if (modelInstances.containsKey(transactionId)) {
            mBookSuccessViewModel = modelInstances.get(transactionId);
            viewModelObserverSetup();
        }

//        mBookSuccessViewModel = new BookSuccessViewModel(BitmapFactory.decodeResource(getResources(),
//                R.drawable.foodsample));
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRegisterAndLogin();
    }

    private void firebaseRegisterAndLogin() {
        fetchDBInfo(transactionId);
//        String email = getResources().getString(R.string.firebase_email);
//        String password = getResources().getString(R.string.firebase_password);
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        if (auth.isSignInWithEmailLink(email)) {
//            fetchDBInfo(transactionId);
//        } else {
//            auth.signInWithEmailAndPassword(email, password)
//                    .addOnCompleteListener(this, task -> {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            fetchDBInfo(transactionId);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w("TAG", "signInWithEmail:failure", task.getException());
//                        }
//                    });
//        }
    }

    private void fetchDBInfo(String transactionId) {
        FirebaseFirestore.getInstance().collection(getResources().getString(R.string.FIREBASE_COLLECTION_TRANSACTION_INFO)).document(transactionId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                FooditemTransaction fooditemTransaction = document.toObject(FooditemTransaction.class);
                if (document.exists()) {
                    setUserSuccessBookUI(fooditemTransaction);
                    Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                } else {
                    Log.d("TAG", "No such document");
                }
            } else {
                int k = 1;
            }
        });
    }

    private void setUserSuccessBookUI(FooditemTransaction fooditemTransaction) {
        BookSuccessViewModel bookSuccessViewModel = new BookSuccessViewModel();
        bookSuccessViewModel.mDeadlineTimeStampToPickup.setValue(fooditemTransaction.getAliveRecord());
        bookSuccessViewModel.mQRCodeContent.setValue(transactionId);
        bookSuccessViewModel.mTransactionStartDate.setValue(fooditemTransaction.getOpenDate());
        bookSuccessViewModel.mTransactionLiveSeconds.setValue(fooditemTransaction.getAliveRecord().longValue());
        bookSuccessViewModel.mDeadlineTimeStampToPickup.setValue(fooditemTransaction.getOpenDate().toDate().getTime()/1000 + fooditemTransaction.getAliveRecord().intValue());

        DocumentReference donorRef = fooditemTransaction.getDonor();
        donorRef.get().addOnCompleteListener(donorTask->{
            if (donorTask.isSuccessful()){
                UsersInfo donor = donorTask.getResult().toObject(UsersInfo.class);
                bookSuccessViewModel.mDonorName.setValue(donor.getName());
                Map<String,Object> asDonor = donor.getAsDonor();
                Double lat = 23d;
                Double lon = 23d;
                if (asDonor != null){
                    lat =(Double)asDonor.get(getResources().getString(R.string.FIREBASE_COLLECTION_USER_INFO_SUB_KEY_OF_AS_DONOR_LAT));
                    lon = (Double)asDonor.get(getResources().getString(R.string.FIREBASE_COLLECTION_USER_INFO_SUB_KEY_OF_AS_DONOR_LON));
                }
                bookSuccessViewModel.mLocationLatLng.setValue(new LatLng(lat,lon));
                bookSuccessViewModel.mPickupLocation.setValue(donor.getLocation().toString());
            }
            DocumentReference foodRef = fooditemTransaction.getFoodRef();
            foodRef.get().addOnCompleteListener(foodTask->{
                if (foodTask.isSuccessful()){
                    FoodItemInfo foodItemInfo = foodTask.getResult().toObject(FoodItemInfo.class);
                    bookSuccessViewModel.mFoodName.setValue(foodItemInfo.getName());
                    ArrayList<String> urls = foodItemInfo.getImageDescription();
                    if (urls.size() > 0){
                        bookSuccessViewModel.mFoodUrl.setValue(urls.get(0));
                    }
                }
                modelInstances.put(transactionId,bookSuccessViewModel);
                mBookSuccessViewModel = bookSuccessViewModel;
                viewModelObserverSetup();
            });
        });
    }

    private void InitListener() {
        mBackButton.setOnClickListener(view -> {
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

        mDonorNameTextView = findViewById(R.id.success_donorNameTextView);
        mFoodNameTextView = findViewById(R.id.success_foodNameTextView);
        mPickupLocationTextView = findViewById(R.id.success_pickupLocationTextView);
        mCountdownTextView = findViewById(R.id.success_countdownTextView);
        mQRCodeImageView = findViewById(R.id.success_qrCodeImageView);
        mFoodImageView = findViewById(R.id.success_foodImageView);
        mBackButton = findViewById(R.id.success_backButton);
    }

    private void viewModelObserverSetup() {
        // Not all live data is here, pickup location is not here
        // todo: why this?
        mBookSuccessViewModel.getDonorName().observe(this, donorName -> {
            mDonorNameTextView.setText(donorName);
        });

        mBookSuccessViewModel.getFoodUrl().observe(this, foodUrl -> {
            Picasso.get().load(foodUrl).into(mFoodImageView);
        });

        mBookSuccessViewModel.mFoodBitmap.observe(this, foodBitmap -> {
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
                Bitmap bitmap = barcodeEncoder.encodeBitmap(qrCodeContent, BarcodeFormat.QR_CODE, mWidth, mHeight);
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
                    long remainSeconds = deadlineTimeStampToPickup - new Date().getTime() / 1000;
                    mCountdownTextView.setText(secondsToTime(remainSeconds));
                    if (remainSeconds <= 0) {
//                        onFinish();
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

    public static void setBookSuccessViewModel(BookSuccessViewModel bookSuccessViewModel) {
        String transactionId = bookSuccessViewModel.getTransactionId().getValue();
        modelInstances.put(transactionId, bookSuccessViewModel);
    }
    @Override
    protected void onResume() {
        super.onResume();
        UiHandler.getInstance().context = this;
    }
}
