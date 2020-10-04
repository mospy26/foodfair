package com.foodfair.snippet.googlemap;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.foodfair.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

public class GoogleMapActivity extends FragmentActivity {

    LatLng mLocation = new LatLng(-33.86785, 151.20732);
    float mZoomLevel = 18f;
    private SupportMapFragment mStaticMapFragment;
    private SupportMapFragment mDynamicMapFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.snippet_googlemap);

        // Configure by xml (static)
        mStaticMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.static_map);
        mStaticMapFragment.getMapAsync(googleMap -> {
            googleMap.addMarker(new MarkerOptions().position(mLocation).title("Marker in Sydney"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(mLocation));
//        mMap.setPadding(200,200,200,200);
            googleMap.getUiSettings().setMapToolbarEnabled(false);
            googleMap.getUiSettings().setMapToolbarEnabled(false);
        });

        // Configure by xml (dynamic)
        mDynamicMapFragment =
                SupportMapFragment.newInstance(new GoogleMapOptions().liteMode(false).mapType(GoogleMap.MAP_TYPE_HYBRID).camera(CameraPosition.builder().zoom(mZoomLevel).target(mLocation).build()));
        FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.dynamic_map, mDynamicMapFragment);
        fragmentTransaction.commit();
        mDynamicMapFragment.getMapAsync(googleMap -> {
            googleMap.getUiSettings().setMapToolbarEnabled(true);
        });
    }
}
