package com.foodfair.ui.hamburger;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.foodfair.R;
import com.foodfair.databinding.FragmentSettingBinding;
import com.foodfair.model.UsersInfo;
import com.foodfair.utilities.Const;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class SettingFragment extends Fragment {

    private SettingViewModel settingViewModel;

    private FirebaseFirestore mFirestore;
    private Query query;

    SharedPreferences sharedPreferences;
    Float convertedToKm;

    TextView searchDistance;
    SeekBar searchSeekBar;
    Button btnResetPassword;
    Button btnSignOut;
    LinearLayout preferredMeat;
    LinearLayout allergies;
    TextView preferredMeatList;
    TextView allergiesList;
    SwitchCompat newFood;
    SwitchCompat itemBookedCancelled;
    SwitchCompat newReview;
    ImageView profilePhoto;
    TextView profileInfo;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingViewModel =
                ViewModelProviders.of(this).get(SettingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_setting, container, false);
        final TextView textView = root.findViewById(R.id.text_settings);
        settingViewModel.getText().observe(getViewLifecycleOwner(), s -> textView.setText(s));

        searchDistance = root.findViewById(R.id.search_distance);
        searchSeekBar = root.findViewById(R.id.search_seekbar);
        btnResetPassword = root.findViewById(R.id.btn_reset_password);
        btnSignOut = root.findViewById(R.id.btn_sign_out);
        preferredMeat = root.findViewById(R.id.linearLayout_preferred_meat);
        allergies = root.findViewById(R.id.linearLayout_allergies);
        preferredMeatList = root.findViewById(R.id.food_pref_meat_list);
        allergiesList = root.findViewById(R.id.food_pref_allergies);
        newFood = root.findViewById(R.id.switch_new_food);
        itemBookedCancelled = root.findViewById(R.id.switch_item_booked_cancelled);
        newReview = root.findViewById(R.id.switch_new_review);
        profilePhoto = root.findViewById(R.id.profile_image);
        profileInfo = root.findViewById(R.id.profile_info);

        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        if(sharedPreferences != null){

            // search radius bar
            convertedToKm = sharedPreferences.getFloat(
                    getString(R.string.config_distance), -1.0f);
            if(convertedToKm != -1.0f){
                int currentProgress = (int) ((convertedToKm - 2) * 4);
                searchSeekBar.setProgress(currentProgress);
                searchDistance.setText(String.format(Locale.US," %.2f Km", convertedToKm));
            }

            // preferred meat list
            String _preferredMeatList = sharedPreferences.getString(
                    getString(R.string.config_preferred_meat), "");
            if(_preferredMeatList != null && !_preferredMeatList.isEmpty()) {
                preferredMeatList.setText(_preferredMeatList);
            } else {
                preferredMeatList.setText(getString(R.string.setting_all));
            }

            // allergies list
            String _allergiesList = sharedPreferences.getString(
                    getString(R.string.config_allergies), "");
            if(_allergiesList != null && !_preferredMeatList.isEmpty()) {
                allergiesList.setText(_allergiesList);
            } else {
                allergiesList.setText(getString(R.string.setting_none));
            }

            // push notification settings
            newFood.setChecked(sharedPreferences.getBoolean(
                    getString(R.string.config_new_food),true));
            itemBookedCancelled.setChecked(sharedPreferences.getBoolean(
                    getString(R.string.config_item_booked_cancelled),true));
            newReview.setChecked(sharedPreferences.getBoolean(
                    getString(R.string.config_new_review),true));

        }

        // Load profile information
        // TODO: Get proper user id
        String userId = "yXnhEl9OBqgKqHLAPMPV";
        mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection(
                getResources().getString(R.string.FIREBASE_COLLECTION_USER_INFO))
                .document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UsersInfo usersInfo = documentSnapshot.toObject(UsersInfo.class);
                Picasso.get().load(usersInfo.getProfileImage())
                        .into(profilePhoto);
                profileInfo.setText("\n" + usersInfo.getName() + "\n\n" + usersInfo.getBio());
            }
        });

        searchSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Change the distance on screen
                // Min = 2 Km, Max = 10 Km
                // Steps = 0.25, progress is 0-32
                convertedToKm = (progress / 4.0f) + 2.0f;
                searchDistance.setText(String.format(Locale.US," %.2f Km", convertedToKm));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Probably not needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Save and update search range
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putFloat(getString(R.string.config_distance), convertedToKm);
                editor.apply();

                Toast toast = Toast.makeText(getActivity(),
                        String.format(Locale.US,
                                "TODO: Update refresh search results"
                                , convertedToKm), Toast.LENGTH_SHORT);
                toast.show();

            }
        });

        btnResetPassword.setOnClickListener(v -> {
            Toast toast = Toast.makeText(getActivity(),
                    "TODO: Send password reset request to Firebase", Toast.LENGTH_SHORT);
            toast.show();
        });

        btnSignOut.setOnClickListener(v -> {
            Toast toast = Toast.makeText(getActivity(),
                    "TODO: Sign out", Toast.LENGTH_SHORT);
            toast.show();
        });

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

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.setting_preferred_meat)
                    //.setMultiChoiceItems(R.array.setting_meat_preferences, checkedItems,
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
                            preferredMeatList.setText(updatedList);
                            if(checkedItemStrings.size() == allItems.length ||
                            checkedItemStrings.size() == 0) {
                                preferredMeatList.setText(getString(R.string.setting_all));
                                editor.putString(getString(R.string.config_preferred_meat),
                                        getString(R.string.setting_all));
                            }
                            editor.apply();
                            Toast toast = Toast.makeText(getActivity(),
                                    "TODO: Update search result", Toast.LENGTH_SHORT);
                            toast.show();
                        })
                    .setNegativeButton(R.string.cancel,
                        (dialog, which) -> {
                        // Do nothing
                        });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        allergies.setOnClickListener(v -> {
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

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                                allergiesList.setText(updatedList);
                                if(checkedItemStrings.size() == 0) {
                                    allergiesList.setText(getString(R.string.setting_none));
                                    editor.putString(getString(R.string.config_allergies),
                                            getString(R.string.setting_none));
                                }
                                editor.apply();
                                Toast toast = Toast.makeText(getActivity(),
                                        "TODO: Update search result", Toast.LENGTH_SHORT);
                                toast.show();
                            })
                    .setNegativeButton(R.string.cancel,
                            (dialog, which) -> {
                                // Do nothing
                            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        newFood.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.config_new_food), isChecked);
            editor.apply();
        });

        itemBookedCancelled.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.config_item_booked_cancelled), isChecked);
            editor.apply();
        });

        newReview.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.config_new_review), isChecked);
            editor.apply();
        });

        return root;
    }


}
