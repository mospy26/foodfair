package com.foodfair.snippet.qrcode;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.foodfair.R;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRCodeGeneratorActivity extends AppCompatActivity {

    int mWidth = 900;
    int mHeight = 900;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create parent RelativeLayout
        RelativeLayout relParent = new RelativeLayout(this);
        RelativeLayout.LayoutParams relParentParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        relParent.setLayoutParams(relParentParam);

        // Create child ImageView
        ImageView imgView = new ImageView(this);
        RelativeLayout.LayoutParams imgViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        imgView.setLayoutParams(imgViewParams);
        imgView.setImageResource(android.R.drawable.ic_menu_add);
        imgViewParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        // Add child ImageView to parent RelativeLayout
        relParent.addView(imgView);

        // Set parent RelativeLayout to your screen
        setContentView(relParent, relParentParam);

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap("content", BarcodeFormat.QR_CODE,mWidth,mHeight);

            int w = (int) (bitmap.getWidth() * 0.8);
            int h = (int) (bitmap.getHeight() * 0.8);
            int cropWidth = w >= h ? h : w;
            Bitmap newBitmap =  Bitmap.createBitmap(bitmap, (bitmap.getWidth() - cropWidth) / 2,
                    (bitmap.getHeight() - cropWidth) / 2, cropWidth, cropWidth);

            imgView.setImageBitmap(newBitmap);
        } catch(Exception e) {

        }
    }
}
