package com.foodfair.ui.book_success;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.foodfair.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class BookSuccessActivity extends AppCompatActivity {
    private SupportMapFragment mDynamicMapFragment;
    private float mZoomLevel = 18f;
    private LatLng mLocation = new LatLng(-33.86785, 151.20732);
    private int mWidth=300;
    private int mHeight=300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_success);

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
            googleMap.setPadding(0,120,0,0);
            googleMap.getUiSettings().setMapToolbarEnabled(true);
            googleMap.addMarker(new MarkerOptions().position(mLocation).title("Fetching Point"));
        });


        // Set Success Icon
        ImageView imageView = findViewById(R.id.success_iconImageView);
        imageView.setImageResource(R.drawable.success_icon);

        // Set QRCode image
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = null;
            bitmap = barcodeEncoder.encodeBitmap("content", BarcodeFormat.QR_CODE,mWidth,mHeight);
            int w = (int) (bitmap.getWidth() * 0.8);
            int h = (int) (bitmap.getHeight() * 0.8);
            int cropWidth = w >= h ? h : w;
            Bitmap newBitmap =  Bitmap.createBitmap(bitmap, (bitmap.getWidth() - cropWidth) / 2,
                    (bitmap.getHeight() - cropWidth) / 2, cropWidth, cropWidth);

            imageView = findViewById(R.id.success_qrCodeImageView);
            imageView.setImageBitmap(newBitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        // Set Food image
        imageView = findViewById(R.id.success_foodImageView);
        imageView.setImageResource(R.drawable.foodsample);

    }
}
