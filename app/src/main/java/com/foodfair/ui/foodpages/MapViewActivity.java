package com.foodfair.ui.foodpages;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.foodfair.utilities.MarshmallowPermission;
import com.foodfair.R;
import com.foodfair.model.FoodItemInfo;
import com.foodfair.model.UsersInfo;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MapViewActivity extends FragmentActivity implements OnMapReadyCallback {


    private static final String TAG = MapViewActivity.class.getSimpleName();
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final long MAX_DOWNLOAD_SIZE = 1024 * 1024;

    private boolean mLocationPermissionGranted;
    private CameraPosition mCameraPosition;
    private GoogleMap mMap;

    /**
     * Firebase authentication instance
     */
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String mFirebaseEmail = "daydayhappy@fakemail.com";
    private String mFirebasePassword = "i_am_Password";
    private String userTableStr = "usersInfo";
    private FirebaseFirestore mFirestore;
    FirebaseStorage mFireStorage;
    StorageReference mStorageRef;
    private CollectionReference mUserCollect;
    private List<UsersInfo> mUsersInfoList;



    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    MarshmallowPermission marshmallowPermission = new MarshmallowPermission(this);

    // A default location (Sydney, Australia)
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);


    // Last-known location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation = new Location("");

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        setContentView(R.layout.activity_map_view);
        setUpMapIfNeeded();

        // initialise Firebase
        mFirestore = FirebaseFirestore.getInstance();
        mFireStorage = FirebaseStorage.getInstance();
        mStorageRef = mFireStorage.getReference();


        // Firestore register and login
        mAuth = FirebaseAuth.getInstance();
        firebaseRegisterAndLogin(mFirebaseEmail, mFirebasePassword);


    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {

            // Construct a FusedLocationProviderClient.
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * Map is ready. Check location permission, get user location and add nearby food marker
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Prompt the user for permission.
        mLocationPermissionGranted = marshmallowPermission
                .checkLocationPermission(PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        updateLocationUI();
        getDeviceLocation();


        // retrieve nearby donor info
        mUserCollect = mFirestore.collection(userTableStr);
        mUserCollect.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mUsersInfoList = task.getResult().toObjects(UsersInfo.class);
                            findNearbyDonors();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    
    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true); // false to disable my location button
                mMap.getUiSettings().setZoomControlsEnabled(true); // false to disable zoom controls
                mMap.getUiSettings().setCompassEnabled(true); // false to disable compass
                mMap.getUiSettings().setRotateGesturesEnabled(true); // false to disable rotate gesture
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                mLocationPermissionGranted = marshmallowPermission
                        .checkLocationPermission(PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);;
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /**
     * Gets the current location of the device, and positions the map's camera.
     * On success, retrieve nearby food from firebase
     */
    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Obtain the current location of the device
                            mLastKnownLocation = task.getResult();
                            String currentOrDefault = "Current";

                            if (mLastKnownLocation != null) {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                                // successfully retrieve user location


                            } else {
                                Log.d(TAG, "Current location is null. Using defaults.");
                                currentOrDefault = "Default";
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                                mMap.getUiSettings().setMyLocationButtonEnabled(false);

                                // Set current location to the default location
                                mLastKnownLocation = new Location("");
                                mLastKnownLocation.setLatitude(mDefaultLocation.latitude);
                                mLastKnownLocation.setLongitude(mDefaultLocation.longitude);
                            }

                            // Show location details on the location TextView
                            String msg = currentOrDefault + " Location: " +
                                    Double.toString(mLastKnownLocation.getLatitude()) + ", " +
                                    Double.toString(mLastKnownLocation.getLongitude());

                            // Add a marker for my current location on the map
                            MarkerOptions marker = new MarkerOptions().position(
                                    new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()))
                                    .title("I am here");
                            mMap.addMarker(marker);
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /**
     * Firebase register and login jobs
     */
    private void firebaseRegisterAndLogin(String email, String password) {
      mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this,
              new OnCompleteListener<AuthResult>() {
                  @Override
                  public void onComplete(@NonNull Task<AuthResult> task) {
                      if (task.isSuccessful()) {
                          // Sign in success, update UI with the signed-in user's information
                          Log.d("TAG", "createUserWithEmail:success");
                      } else {
                          // If sign in fails, display a message to the user.
                          Log.w("TAG", "createUserWithEmail:failure", task.getException());
                      }
                  }
              });

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                        }

                    }
                });
    }


    /**
     * find nearby donors
     */
    private void findNearbyDonors() {

        double deviceLon = mLastKnownLocation.getLongitude();
        double deviceLat = mLastKnownLocation.getLatitude();
        double userLon;
        double userLat;
        double maxDist = 999999999;
        DocumentReference foodItemRef = null;

        Map<String, Object> donors;

        for (UsersInfo users : mUsersInfoList) {
            try {
                donors = users.getAsDonor();
                userLon = (double) donors.get("lon");
                userLat = (double) donors.get("lat");

                double dist = Math.abs(calcDist(userLat, userLon, deviceLat, deviceLon));

                if (dist <= maxDist ) {
                    // mark the this user on the map
                    final LatLng nearbyUser = new LatLng(userLat, userLon);

                    // first retrieve his post food reference
                    ArrayList<DocumentReference> foodItemList =
                            (ArrayList<DocumentReference>) donors.get("itemsOnShelf");

                    if (!foodItemList.isEmpty()) {
                        foodItemRef = (DocumentReference) foodItemList.get(0);
                    }

                    foodItemRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            // retrieved the foodItemInfo collection, get the image Uri
                            FoodItemInfo foodItemInfo = documentSnapshot.toObject(FoodItemInfo.class);
                            ArrayList<String> imgs = foodItemInfo.getImageDescription();
                            String title = foodItemInfo.getName();
                            String description = foodItemInfo.getTextDescription();

                            if (!imgs.isEmpty()) {
                                String firstImg = imgs.get(0);
                                // add the food image onto the map
                                addMarkerToMap(nearbyUser, firstImg, title, description);
                            }
                        }
                    });

                }

            } catch (NullPointerException e) {
                Log.d(TAG, "Longitude and latitude information invalid");

            }
        }

    }




    public static int calcDist(double latA, double lonA, double latB, double lonB) {

        double theDistance = (Math.sin(Math.toRadians(latA)) *
                Math.sin(Math.toRadians(latB)) +
                Math.cos(Math.toRadians(latA)) *
                        Math.cos(Math.toRadians(latB)) *
                        Math.cos(Math.toRadians(lonA - lonB)));

        return new Double((Math.toDegrees(Math.acos(theDistance))) * 69.09*1.6093).intValue();
    }


    public Bitmap resizeMapIcons(String iconName,int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),
                getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    public void addMarkerToMap(final LatLng userPos, String imgUrl,
                               final String foodTitle, final String foodDescription) {

        //List<Target> targets = new ArrayList<Target>();
        Target mapTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {

                mMap.addMarker(new MarkerOptions()
                        .title(foodTitle)
                        .snippet(foodDescription)
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                        .position(userPos));
                //vendorMarker.add(marker);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable drawable) {
                Log.d(TAG, "Failed loading food image onto the map");
            }

            @Override
            public void onPrepareLoad(Drawable drawable) {
            }
        };

        Picasso.get().load(imgUrl).resize(200, 200).into(mapTarget);

    }

}