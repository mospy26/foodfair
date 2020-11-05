package com.foodfair.ui.foodpages;

import com.foodfair.R;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.FileProvider;

import com.foodfair.model.FoodItemInfo;
import com.foodfair.ui.foodpages.MapViewActivity;
import com.foodfair.utilities.Utility;
import com.foodfair.utilities.Const;
import com.foodfair.utilities.MarshmallowPermission;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


public class PostFoodActivity extends AppCompatActivity {


    // !!!!!! Assume we have user ID!!!
    private static final String UID = "yXnhEl9OBqgKqHLAPMPV";

    // developer-defined request codes
    private static final int READ_PHOTO_REQ_CODE = 101;
    private static final int OPEN_CAMERA_REQ_CODE = 102;
    private static final int CAMERA_STORAGE_REQUEST_CODE = 111;
    private static final String TAG = MapViewActivity.class.getSimpleName();
    Const aConst = Const.getInstance();

    private int year, month, day;
    File mFile;
    private TextView tvDate;
    private ImageView ivAddImage;
    private EditText etFoodName;
    private EditText etType;
    private EditText etAllergen;
    private EditText etQuantity;
    private EditText etDescription;

    Bitmap mTakenImage;
    FoodItemInfo mFoodItemInfo = new FoodItemInfo();

    /**
     * Firebase authentication instance
     */
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String mFirebaseEmail = "daydayhappy@fakemail.com";
    private String mFirebasePassword = "i_am_Password";
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
        etFoodName = (EditText) findViewById(R.id.etFoodName);
        etAllergen = (EditText) findViewById(R.id.etAllergen);
        etDescription = (EditText) findViewById(R.id.etDescription);
        etQuantity = (EditText) findViewById(R.id.etQuantity);
        etType = (EditText) findViewById(R.id.etType);

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
                mTakenImage = BitmapFactory.decodeFile(mFile.getAbsolutePath());

                // Load the taken image into a preview
                ivAddImage.setImageBitmap(mTakenImage);
                ivAddImage.setVisibility(View.VISIBLE);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken AAA!",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == READ_PHOTO_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                ivAddImage.setVisibility(View.GONE);
                Uri photoUri = data.getData();
                mFile = new File(photoUri.getPath());
                // Do something with the photo based on Uri
                try {
                    mTakenImage = MediaStore.Images.Media.getBitmap(
                            this.getContentResolver(), photoUri);

                    // Load the selected image into a preview
                    ivAddImage.setImageBitmap(mTakenImage);
                    ivAddImage.setVisibility(View.VISIBLE);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
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


    /**
     * On submit, we want to parse the user input data
     * and then post it onto firebase
     * @param view
     */
    public void onSubmitFoodClick(View view) {
        // request to post food onto firebase
        ArrayList<Long> allergenLongList = new ArrayList<Long>();
        final int status = 1;
        Long typeLong;
        Timestamp dateExpiry;

        // get the current timestamp
        Timestamp dateOn;
        String currentTimeStr = Utility.getCurrentTimeStr();


        String foodNameStr = etFoodName.getText().toString().trim().toLowerCase();
        String allergenStr = etAllergen.getText().toString().trim().toLowerCase();
        String quanStr = etQuantity.getText().toString().trim();
        String descStr = etDescription.getText().toString().trim().toLowerCase();
        // !!!!type is to be changed to a drop down button
        String typeStr = etType.getText().toString().trim().toLowerCase();
        String dateExpiryStr = tvDate.getText().toString();


        allergenStringToLongList(allergenStr, allergenLongList);
        typeLong = typeStringToLong(typeStr);


        // first check if fields are empty
        List<EditText> etList = Arrays.asList(etFoodName, etType, etAllergen,
                etQuantity, etDescription);

        if (ivAddImage.getDrawable() == null) {
            Toast.makeText(this, "Please add a food image", Toast.LENGTH_SHORT).show();
            return;
        }


        if (checkEachField(etList)) {
            // if all fields are filled

            dateExpiry = Utility.parseDateTime(dateExpiryStr, "dd/MM/yyyy");
            dateOn = Utility.parseDateTime(currentTimeStr, aConst.DATE_TIME_PATTERN);
            int quantity = Integer.parseInt(quanStr);

            // get donor reference
            String userTableStr = getResources().getString(R.string.FIREBASE_COLLECTION_USER_INFO);
            DocumentReference donorRef = mFirestore.document(userTableStr + "/" + UID);

            // upload image onto storage and retrieve the url
            String fileName = mFile.getName();
            StorageReference foodRef = mStorageRef.child("food/" + fileName);

            uploadImageOntoStorage(foodRef, mTakenImage);

            populateFoodItemInfo(allergenLongList, quantity, dateExpiry, dateOn, donorRef,
                    null, foodNameStr, status, descStr, typeLong, mFoodItemInfo);

        }

    }


    public boolean checkEachField(List<EditText> etList) {
        for (EditText editText: etList) {
            String textInput = editText.getText().toString().trim();
            String field = "";
            if (editText.getId() == R.id.etFoodName) {
                field = "Food Name";
            } else if (editText.getId() == R.id.etType) {
                field = "Type";
            } else if (editText.getId() == R.id.etAllergen) {
                field = "Allergen";
            } else if (editText.getId() == R.id.etDescription) {
                field = "Description";
            } else if (editText.getId() == R.id.etQuantity) {
                field = "Quantity";
            }

            // check if any of the field is empty
            if (TextUtils.isEmpty(textInput)) {
                Toast.makeText(this, field + " cannot be empty", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    public void postFoodToFirebase(FoodItemInfo foodItemInfo) {

        String foodTableStr = getResources().getString(R.string.FIREBASE_COLLECTION_FOOD_ITEM_INFO);
        CollectionReference foodCollect = mFirestore.collection(foodTableStr);
        foodCollect.document().set(foodItemInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });


    }

    /**
     *  looks up the Const hashset table to find out the corresponding type integers
     */
    public Long typeStringToLong(String typeString) {

        Set<Long> keys = getKeys(aConst.FOOD_TYPE_DETAIL, typeString.trim());
        if (!keys.isEmpty()) {
            return(keys.iterator().next());
        }
        return null;
    }


    /**
     *  looks up the Const hashset table to find out the corresponding allergen integers
     */
    public void allergenStringToLongList(String allergenString, ArrayList<Long> longList) {

        String[] allergenList = allergenString.split("[,]", 0);
        for (String allerg : allergenList) {
            Set<Long> keys = getKeys(aConst.ALLERGY_DETAIL, allerg.trim());
            if (!keys.isEmpty()) {
                Long allergLong = keys.iterator().next();
                longList.add(allergLong);
            }
        }
    }

    /**
     *  given value, get the key of the map
     */
    public <K, V> Set<K> getKeys(Map<K, V> map, V value) {
        Set<K> keys = new HashSet<>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }


    public void populateFoodItemInfo(ArrayList<Long> allergyInfo, Integer quantity, Timestamp dateExpire,
                                     Timestamp dateOn, DocumentReference donorRef, ArrayList<String> imageDescription,
                                     String name, Integer status, String textDescription, Long type,
                                     FoodItemInfo foodItemInfo) {

        foodItemInfo.setType(type);
        foodItemInfo.setTextDescription(textDescription);
        foodItemInfo.setCount(quantity);
        foodItemInfo.setName(name);
        foodItemInfo.setAllergyInfo(allergyInfo);
        foodItemInfo.setDateExpire(dateExpire);
        foodItemInfo.setStatus(status);
        foodItemInfo.setDonorRef(donorRef);
        foodItemInfo.setImageDescription(imageDescription);
        foodItemInfo.setDateOn(dateOn);

    }

    /**
     *  Upload the bitmap image onto Firebase storage
     */
    public void uploadImageOntoStorage(StorageReference foodRef, Bitmap mTakenImage) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mTakenImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = foodRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // successfully updated the image

                foodRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // successfully retrieve the download URI
                        ArrayList<String> uriList = new ArrayList<>();
                        uriList.add(uri.toString());

                        mFoodItemInfo.setImageDescription(uriList);
                        postFoodToFirebase(mFoodItemInfo);

                        Log.d(TAG, "URI success");
                    }
                });
                Log.d(TAG, "Successfully uploaded image onto storage");
            }
        });


    }
}
