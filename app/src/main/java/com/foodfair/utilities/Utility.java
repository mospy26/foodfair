package com.foodfair.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.util.Base64;
import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Utility {
    /**
     * Convert a bitmap to a base64 string
     *
     * @param bitmap bitmap object
     * @return base64-encoded string
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Convert a base64 string to a bitmap
     *
     * @param base64 base64-encoded string
     * @return bitmap object
     */
    public static Bitmap base64ToBitmap(String base64) {
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return bitmap;
    }

    /**
     * reduces the size of the image
     * @param image
     * @param maxSize
     * @return
     */
    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public static String timeStampToDateString(Timestamp timestamp) {
        return timeStampToDateString(timestamp, null);
    }

    public static String timeStampToDateString (Timestamp timestamp, String format) {
        if(format == null || format.isEmpty()){
            format = "dd-MMM-yyyy";
        }
        SimpleDateFormat sfd = new SimpleDateFormat(format);
        if(timestamp == null) return null;
        return sfd.format(timestamp.toDate());
    }

    /**
     *  get current time in a string format
     */

    public static String getCurrentTimeStr() { return getCurrentTimeStr(null); }

    public static String getCurrentTimeStr(String pattern) {
        if(pattern == null || pattern.isEmpty()){
            pattern = "yyyyMMdd_HHmmss";
        }
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(new Date());
    }


    /**
     *  Convert the dateTimeString into timestamp
     */
    public static Timestamp parseDateTime(String dateTimeStr, String pattern) {

        SimpleDateFormat inputFormat = new SimpleDateFormat(pattern);
        Date d = null;
        try {
            //convert string to date
            d = inputFormat.parse(dateTimeStr);
        } catch (ParseException e) {
            System.out.println("Date Format Not Supported");
            e.printStackTrace();
        }
        return new Timestamp(d);

    }

    public static String getSuburb(double lat, double lon, Context context) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        addresses = geocoder.getFromLocation(lat, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();
        return city;
    }


}
