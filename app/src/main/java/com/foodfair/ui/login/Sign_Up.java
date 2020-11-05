package com.foodfair.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.foodfair.R;
import com.foodfair.model.User;
import com.foodfair.model.UsersInfo;
import com.foodfair.utilities.LoadingDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Sign_Up extends AppCompatActivity implements LocationListener {

    Context context = this;
    private EditText name;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private EditText bio;
    private Spinner gender;
    private Spinner status;
    private Button signUpBtn;
    private final String[] GENDERS = {"Male", "Female", "Other", "Prefer to not say"};
    private final String[] STATUS = {"Restaurant", "User", "Charity"};

    private LoadingDialog loadingDialog = new LoadingDialog(this);
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private Location location;
    private LocationManager locationManager;
    private String userId;


    private String nameValue;
    private String passwordValue;
    private String emailValue;
    private String bioValue;
    private int genderValue;
    private int statusValue;
    private int preferenceValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign__up);

        initUI();
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        signUpBtn = findViewById(R.id.SignUpBtn);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String validate = validateData();
                if (validate != null) {
                    Toast.makeText(Sign_Up.this, validate, Toast.LENGTH_LONG);
                }
                
                createUserAccount(email.getText().toString(), password.getText().toString());
                loadingDialog.startLoadingAnimationg();

                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.dismissDialog();
                    }
                }, 2000);

                Intent intent = new Intent(context, Login.class);
                startActivity(intent);

            }
        });
    }

    private String validateData() {

        String nameValue = name.getText().toString().trim();

        if (nameValue.equals("")) {
            return "Name cannot be empty";
        }

        String emaiValue = email.getText().toString().trim();

        if (Patterns.EMAIL_ADDRESS.matcher(emaiValue).matches()) {
            return "Email cannot be empty";
        }

        String passwordValue = password.getText().toString().trim();
        String confirmPasswordValue = confirmPassword.getText().toString().trim();

        if (passwordValue != confirmPasswordValue) {
            password.setText("");
            confirmPassword.setText("");
            passwordValue = null;
            confirmPasswordValue = null;
            return "Passwords do not match";
        }

        String bioValue = bio.getText().toString().trim();
        // TODO get gender etc and check

    }

    private void initUI() {
        name = findViewById(R.id.SignUpName);
        email = findViewById(R.id.SignUpEmail);
        password = findViewById(R.id.SignUpPassword);
        confirmPassword = findViewById(R.id.ConfirmPassword);
        gender = findViewById(R.id.SignUpGender);
        status = findViewById(R.id.SignUpStatus);
        bio = findViewById(R.id.SignUpBio);

        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, GENDERS);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(aa);

        ArrayAdapter statusAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, STATUS);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status.setAdapter(statusAdapter);


    }

    public void createUserAccount(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                userId = firebaseAuth.getUid();

                // store it globally for access
                sharedPreferences = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("firebasekey", userId);
                Log.d("Sign Up", "Sign Up Successful");
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
            return;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (location != null) {
            Log.v("Location Changed", location.getLatitude() + " and " + location.getLongitude());
            locationManager.removeUpdates(this);
        }
        else {
            this.location = location;
            saveLocation();
        }
    }

    private void saveLocation() {
        DocumentReference documentReference = db.collection("UsersInfo").document(userId);
//        Map<String, Object> users = new HashMap<>();
        UsersInfo user = new UsersInfo();
        user.setLocation(new GeoPoint(location.getLatitude(), location.getLongitude()));
//        users.put("User", user);
        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Sign Up", "Successfully Stored user information");
            }
        });
    }
}