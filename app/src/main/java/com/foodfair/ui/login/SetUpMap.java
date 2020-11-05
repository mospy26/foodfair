package com.foodfair.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.foodfair.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class SetUpMap extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker mLastMarker;
    private LatLng prevPoint;
    private FloatingActionButton next;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup__map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        next = findViewById(R.id.SetUpNext);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    boolean result = passBackMarker();
                    if (result) {
                        finish();
                    }
                } catch (JSONException e) {
                    Log.e("JSON error", e.getMessage());
                }
            }
        });

        // Button that saves the location user chose
//        Button saveButton;
//        saveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e("TAG", "User chose: " + mLastMarker.getPosition().toString());
//                // Once the creation mode has ended remove the listener
//                mMap.setOnMapClickListener(null);
//            }
//        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng sydney = new LatLng(-33.852, 151.211);
        googleMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title("Marker").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mMap = googleMap;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                prevPoint = latLng;
                mLastMarker = mMap.addMarker(new MarkerOptions().draggable(true).position(latLng));
            }
        });
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12.0f));


//        setContentView(R.layout.setup__map);
    }

    private boolean passBackMarker() throws JSONException {
        if (prevPoint == null) {
            return false;
        }

        Intent i = new Intent(this, SetUp.class);
        String latlon = new JSONObject()
                        .put("lat", prevPoint.latitude)
                        .put("long", prevPoint.longitude)
                        .toString();
        i.putExtra("result", latlon);
        setResult(RESULT_OK, i);
        return true;
    }
}
