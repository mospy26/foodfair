package com.foodfair.ui.hamburger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.foodfair.R;

public class ManageFoodFragment extends Fragment {

    private ManageFoodViewModel manageFoodViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        manageFoodViewModel =
                ViewModelProviders.of(this).get(ManageFoodViewModel.class);
        View root = inflater.inflate(R.layout.fragment_manage_food, container, false);
        final TextView textView = root.findViewById(R.id.text_manage_food);
        manageFoodViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}