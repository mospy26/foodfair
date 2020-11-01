package com.foodfair;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.foodfair.model.UsersInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostFoodActivity extends AppCompatActivity {

    // developer-defined request codes
    private static final int READ_PHOTO_REQ_CODE = 101;
    private static final int OPEN_CAMERA_REQ_CODE = 102;
    private static final int CAMERA_STORAGE_REQUEST_CODE = 111;
    private static final String TAG = MapViewActivity.class.getSimpleName();

    private int year, month, day;
    File mFile;
    private TextView tvDate;
    private ImageView ivAddImage;

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
        ivAddImage = (ImageView) findViewById(R.id.ivAddImage);

        // initialise Firebase
        mFirestore = FirebaseFirestore.getInstance();
        mFireStorage = FirebaseStorage.getInstance();
        mStorageRef = mFireStorage.getReference();


        // Firestore register and login
        mAuth = FirebaseAuth.getInstance();
        firebaseRegisterAndLogin(mFirebaseEmail, mFirebasePassword);

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
                    // set file name
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                            Locale.getDefault()).format(new Date());
                    String photoFileName = "IMG_" + timeStamp + ".jpg";

                    // Create a photo file reference
                    Uri file_uri = getFileUri(photoFileName);

                    // Add extended data to the intent
                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT, file_uri);


                    // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
                    // So as long as the result is not null, it's safe to use the intent.
                    if (takePicture.resolveActivity(getPackageManager()) != null) {
                        // Start the image capture intent to take photo
                        startActivityForResult(takePicture, OPEN_CAMERA_REQ_CODE);
                    } else {
                        Log.d(TAG, "onTakePhotoClick: resolveActivity is null");

                    }

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


        if (requestCode == OPEN_CAMERA_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                ivAddImage.setVisibility(View.GONE);
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(mFile.getAbsolutePath());

                // Load the taken image into a preview
                ivAddImage.setImageBitmap(takenImage);
                ivAddImage.setVisibility(View.VISIBLE);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken AAA!",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == READ_PHOTO_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                ivAddImage.setVisibility(View.GONE);
                Uri photoUri = data.getData();
                // Do something with the photo based on Uri
                Bitmap selectedImage;
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(
                            this.getContentResolver(), photoUri);

                    // Load the selected image into a preview
                    ivAddImage.setImageBitmap(selectedImage);
                    ivAddImage.setVisibility(View.VISIBLE);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
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



    // Returns the Uri for a photo/media stored on disk given the fileName and type
    public Uri getFileUri(String fileName) {
        Uri fileUri = null;
        String authority = "com.foodfair.fileProvider";
        try {
            String typestr = "/images/"; //default to images type

            // Get safe storage directory depending on type
            File mediaStorageDir = new File(this.getExternalFilesDir(null).getAbsolutePath(),
                    typestr+fileName);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.getParentFile().exists() && !mediaStorageDir.getParentFile().mkdirs()) {
                Log.d(TAG, "failed to create directory");
            }

            // Create the file target for the media based on filename
            // this only creates a link of variable file to the full file path
            // usually we will need file.createNewFile() to actually create one
            mFile = new File(mediaStorageDir.getParentFile().getPath() + File.separator + fileName);

            // Wrap File object into a content provider, required for API >= 24
            // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
            if (Build.VERSION.SDK_INT >= 24) {
                fileUri = FileProvider.getUriForFile(
                        this.getApplicationContext(),
                        authority, mFile);
            } else {
                fileUri = Uri.fromFile(mediaStorageDir);
            }
        } catch (Exception ex) {
            Log.d("getFileUri", ex.getStackTrace().toString());
        }
        return fileUri;
    }

    public void onSubmitFoodClick(View view) {
        // post food information to Firebase



    }
}
