package com.foodfair;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PostFoodActivity extends AppCompatActivity {

    // developer-defined request codes
    private static final int READ_PHOTO_REQ_CODE = 101;
    private static final int OPEN_CAMERA_REQ_CODE = 102;
    private static final int CAMERA_STORAGE_REQUEST_CODE = 111;

    private int year, month, day;
    private TextView tvDate;


    MarshmallowPermission marshmallowPermission = new MarshmallowPermission(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // for accessing the android date picker
        year = Calendar.getInstance().get(Calendar.YEAR);
        month = Calendar.getInstance().get(Calendar.MONTH)+1;
        day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) ;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postfood);
        tvDate = (TextView) findViewById(R.id.tvExpiryDate);
    }

    public void onAddPhotoClick(View view) {
        if (marshmallowPermission.checkPhotoCameraPermission(CAMERA_STORAGE_REQUEST_CODE)) {
            // already has permission, open the media choice dialog
            chooseMedia(PostFoodActivity.this);
        };
    }

    private void chooseMedia(Context context) {
        final String opt1 = "Choose from Album";
        final String opt2 = "Take a Photo";
        final String opt3 = "Cancel";
        final CharSequence[] options = {opt1, opt2, opt3};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);


        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals(opt1)) {
                    // user chooses to read from the albums
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, READ_PHOTO_REQ_CODE);


                } else if (options[item].equals(opt2)) {
                    // user chooses to take a photo
                    Intent takePicture = new Intent(
                            android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, OPEN_CAMERA_REQ_CODE);

                } else if (options[item].equals(opt3)) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // do nothing
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {

        switch (requestCode) {
            case CAMERA_STORAGE_REQUEST_CODE: {
                boolean isGranted = true;
                // check if all the request permissions have been granted
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        isGranted = false;
                    }
                }

                if (isGranted) {
                    chooseMedia(PostFoodActivity.this);
                } else {
                    Toast.makeText(getApplicationContext(), "External Storage & Camera Access"
                                    + "permission needed Please allow in App Settings for additional functionality.",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }


    public void onPickDateClick(View view) {
        int mYearParam = year;
        int mMonthParam = month-1;
        int mDayParam = day;

        DatePickerDialog datePickerDialog = new DatePickerDialog(PostFoodActivity.this,
                new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int yearSelected,
                                      int monthSelected, int daySelected) {
                month = monthSelected + 1;
                year = yearSelected;
                day = daySelected;
                tvDate.setText(day + "/" + month + "/" + year);
            }

        }, mYearParam, mMonthParam, mDayParam);

        datePickerDialog.show();
    }

    public void onSubmitFoodClick(View view) {
    }
}
