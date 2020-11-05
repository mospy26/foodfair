package com.foodfair.ui.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.foodfair.MainActivity;
import com.foodfair.R;
import com.foodfair.model.UsersInfo;
import com.foodfair.utilities.Cache;
import com.foodfair.utilities.Const;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SetUp extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    Button preferredMeat;
    Button allergen;
    Button map;
    UsersInfo user;
    String userID;
    Cache cache;
    String returnedCoordinates;
    FloatingActionButton next;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup__signup);
        cache = Cache.getInstance(this);

        sharedPreferences = getSharedPreferences("foodfair", MODE_PRIVATE);
        preferredMeat = findViewById(R.id.SetUpPreferredMeat);
        allergen = findViewById(R.id.SetUpAllergen);
        map = findViewById(R.id.SetUpMap);
        next = findViewById(R.id.SetUpDoneNext);

        if (cache.get("user") == null) {
            fetchUser();
        }
        else {
            user = (UsersInfo) cache.get("user").getData();
            getUserID();
            setupPreferred();
            setUpAllergen();
            setUpMap();
        }
    }

    private void fetchUser() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = firebaseUser.getUid();

        if (userID == null) {
            getUserID();
        }

        FirebaseFirestore.getInstance().collection(getResources().getString(R.string.FIREBASE_COLLECTION_USER_INFO)).document(userID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    user = document.toObject(UsersInfo.class);
                }
                setupPreferred();
                setUpAllergen();
                setUpMap();
                setUpNext();
            }
        });
    }

    private void getUserID() {
        userID = sharedPreferences.getString("firebasekey", null);
        if (userID == null) {
            Intent i = new Intent(SetUp.this, Login.class);
            startActivity(i);
            finish();
        }
    }

    private void setupPreferred() {
        preferredMeat.setOnClickListener(v -> {
            String _preferredMeatList = sharedPreferences.getString(
                    getString(R.string.config_preferred_meat), "");
            Log.d("Preferred meat", _preferredMeatList);
            ArrayList<String> checkedItemStrings = new ArrayList<>();
            List<String> itemList = new ArrayList<String>();
            for(Map.Entry<Long,String> entry: Const.getInstance().FOOD_TYPE_DETAIL.entrySet()){
                itemList.add(entry.getValue());
            }
            // String[] allItems = getResources().getStringArray(R.array.setting_meat_preferences);
            String[] allItems = new String[itemList.size()];
            for(int i=0; i<itemList.size(); i++){
                allItems[i] = itemList.get(i);
            }
            boolean[] checkedItems = new boolean[allItems.length];
            String[] _preferredMeatArray = _preferredMeatList.split(", ");
            // Insert list of checked items into the array list
            if(_preferredMeatList != null && !_preferredMeatList.isEmpty()
                    && !_preferredMeatList.equals(getString(R.string.setting_all))){
                for(String _preferredMeat: _preferredMeatArray) {
                    checkedItemStrings.add(_preferredMeat.trim());
                }
                for(int i=0; i < allItems.length; i++){
                    checkedItems[i] =
                            Arrays.stream(_preferredMeatArray).anyMatch(allItems[i]::contains);
                }

            } else {
                int i = 0;
                for(String _preferredMeat: allItems) {
                    checkedItemStrings.add(_preferredMeat);
                    checkedItems[i] = true;
                    i++;
                }
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.setting_preferred_meat)
                    .setMultiChoiceItems(allItems, checkedItems,
                            (dialog, which, isChecked) -> {
                                if(isChecked){
                                    checkedItemStrings.add(allItems[which]);
                                } else {
                                    checkedItemStrings.remove(allItems[which]);
                                }

                            })
                    .setPositiveButton(R.string.ok,
                            (dialog, which) -> {
                                // Save into shared preference
                                Collections.sort(checkedItemStrings);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                String updatedList = checkedItemStrings.stream().collect(
                                        Collectors.joining(", "));
                                editor.putString(getString(R.string.config_preferred_meat), updatedList);
//                                preferredMeatList.setText(updatedList);
                                if(checkedItemStrings.size() == allItems.length ||
                                        checkedItemStrings.size() == 0) {
//                                    preferredMeatList.setText(getString(R.string.setting_all));
                                    editor.putString(getString(R.string.config_preferred_meat),
                                            getString(R.string.setting_all));
                                }
                                editor.apply();
                                ArrayList<Long> pref = new ArrayList<>();
                                for (String value : checkedItemStrings) {
                                    pref.addAll(Const.getInstance().FOOD_TYPE_DETAIL
                                        .entrySet()
                                        .stream()
                                        .filter(entry -> value.equals(entry.getValue()))
                                        .map(Map.Entry::getKey).collect(Collectors.toList()));
                                    if (pref.size() != 1) {
                                        Toast.makeText(getBaseContext(), "Please choose only one preference", Toast.LENGTH_LONG).show();
                                    }
                                }
                                user.setPreference(pref.get(0));
                                cache.add("user", user);
                                updateUserPreference(user);
                            })
                    .setNegativeButton(R.string.cancel,
                            (dialog, which) -> {
                                // Do nothing
                            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    private void setUpAllergen() {
        allergen.setOnClickListener(v -> {
            String _allergiesList = sharedPreferences.getString(
                    getString(R.string.config_allergies), "");
            ArrayList<String> checkedItemStrings = new ArrayList<>();
            List<String> itemList = new ArrayList<String>();
            for(Map.Entry<Long,String> entry: Const.getInstance().ALLERGY_DETAIL.entrySet()){
                itemList.add(entry.getValue());
            }
//            String[] allItems = getResources().getStringArray(R.array.setting_allergies);
            String[] allItems = new String[itemList.size()];
            for(int i=0; i<itemList.size(); i++){
                allItems[i] = itemList.get(i);
            }
            boolean[] checkedItems = new boolean[allItems.length];
            String[] _allergiesArray = _allergiesList.split(", ");
            // Insert list of checked items into the array list
            if(_allergiesList != null && !_allergiesList.isEmpty()
                    && !_allergiesList.equals(getString(R.string.setting_none))){
                for(String _allergies: _allergiesArray) {
                    checkedItemStrings.add(_allergies.trim());
                }
                for(int i=0; i < allItems.length; i++){
                    checkedItems[i] =
                            Arrays.stream(_allergiesArray).anyMatch(allItems[i]::contains);
                }

            } else {
                int i = 0;
                for(String _allergies: allItems) {
                    checkedItems[i] = false;
                    i++;
                }
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.setting_allergies)
                    // .setMultiChoiceItems(R.array.setting_allergies, checkedItems,
                    .setMultiChoiceItems(allItems, checkedItems,
                            (dialog, which, isChecked) -> {
                                if(isChecked){
                                    checkedItemStrings.add(allItems[which]);
                                } else {
                                    checkedItemStrings.remove(allItems[which]);
                                }

                            })
                    .setPositiveButton(R.string.ok,
                            (dialog, which) -> {
                                // Save into shared preference
                                Collections.sort(checkedItemStrings);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                String updatedList = checkedItemStrings.stream().collect(
                                        Collectors.joining(", "));
                                editor.putString(getString(R.string.config_allergies), updatedList);
//                                allergiesList.setText(updatedList);
                                if(checkedItemStrings.size() == 0) {
//                                    allergiesList.setText(getString(R.string.setting_none));
                                    editor.putString(getString(R.string.config_allergies),
                                            getString(R.string.setting_none));
                                }
                                editor.apply();
                                ArrayList<Long> allergies = new ArrayList<>();
                                for (String value : checkedItemStrings) {
                                    allergies.addAll(Const.getInstance().ALLERGY_DETAIL
                                            .entrySet()
                                            .stream()
                                            .filter(entry -> value.equals(entry.getValue()))
                                            .map(Map.Entry::getKey).collect(Collectors.toList()));
                                }
                                user.setAllergy(allergies);
                                cache.add("user", user);
                                updateUserAllergy(user);
                            })
                    .setNegativeButton(R.string.cancel,
                            (dialog, which) -> {
                                // Do nothing
                            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    private void setUpMap() {
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SetUp.this, SetUpMap.class);
                startActivityForResult(i, 0);
            }
        });
    }

    private void setUpNext() {
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user.getLocation() == null || user.getLocation().equals("") ||
                        user.getAllergy() == null || user.getPreference() == null) {
                    Toast.makeText(SetUp.this, "Please enter details before proceeding", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent i = new Intent(SetUp.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

    private void updateUserPreference(UsersInfo user) {
        FirebaseFirestore.getInstance().collection(getResources().getString(R.string.FIREBASE_COLLECTION_USER_INFO)).document(userID).update("preference", user.getPreference())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("User preference update", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("User preference update", "Error writing document", e);
                    }
                });
    }

    private void updateUserAllergy(UsersInfo user) {
        Log.e("WTH", userID + "");
        FirebaseFirestore.getInstance().collection(getResources().getString(R.string.FIREBASE_COLLECTION_USER_INFO)).document(userID).update("allergy", user.getAllergy())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("User allergy update", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("User allergy update", "Error writing document", e);
                    }
                });
    }

    private void updateUserLocation(UsersInfo user) {
        FirebaseFirestore.getInstance().collection(getResources().getString(R.string.FIREBASE_COLLECTION_USER_INFO)).document(userID).update("location", user.getLocation())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("User location update", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("User location update", "Error writing document", e);
                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                returnedCoordinates = data.getStringExtra("result");
                user.setLocation(returnedCoordinates);
                updateUserLocation(user);
            }
        }
    }
}
