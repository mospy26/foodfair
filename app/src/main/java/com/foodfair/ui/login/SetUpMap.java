package com.foodfair.ui.login;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.foodfair.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class SetUpMap extends AppCompatActivity implements OnMapReadyCallback {

    private static final int DEFAULT_ZOOM = 15;

    private GoogleMap mMap;
    private Marker mLastMarker;
    private LatLng prevPoint;
    private FloatingActionButton next;
    LocationManager lm;
    LocationResult locationResult;
    Location currentLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LatLng mDefaultLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup__map);
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

        mDefaultLocation = new LatLng(-33.852, 151.211);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        getLocation(this, locationResult);

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

    private boolean getLocation(Context context, LocationResult result) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
        locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    // Obtain the current location of the device
                    currentLocation = task.getResult();
                    String currentOrDefault = "Current";

                    if (currentLocation != null) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(currentLocation.getLatitude(),
                                        currentLocation.getLongitude()), DEFAULT_ZOOM));

                        prevPoint = new LatLng(currentLocation.getLatitude(),
                                currentLocation.getLongitude());
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().draggable(true).position(new LatLng(currentLocation.getLatitude(),
                                currentLocation.getLongitude())));

                        // successfully retrieve user location


                    } else {
                        Log.d("Location GPS", "Current location is null. Using defaults.");
                        currentOrDefault = "Default";
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);

                        // Set current location to the default location

                    }
                }
            }
        });
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        googleMap.addMarker(new MarkerOptions()
                .position(mDefaultLocation)
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
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, 12.0f));


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
