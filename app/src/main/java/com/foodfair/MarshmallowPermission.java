package com.foodfair;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import java.util.ArrayList;
import java.util.List;

public class MarshmallowPermission {

    Activity activity;

    public MarshmallowPermission(Activity activity) {
        this.activity = activity;
    }


    public boolean checkPhotoCameraPermission(int requestCode) {
        String[] targetPermission = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };

        // find which permissions are needed
        List<String> listPermNeeded = new ArrayList<>();
        for (String perm : targetPermission) {
            if (ContextCompat.checkSelfPermission(activity, perm) !=
                    PackageManager.PERMISSION_GRANTED) {
                listPermNeeded.add(perm);
            }
        }

        // Ask for missing permissions
        if (!listPermNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity,
                    listPermNeeded.toArray(new String[listPermNeeded.size()]),
                    requestCode);
            return false;
        }

        return true;
    }

}
