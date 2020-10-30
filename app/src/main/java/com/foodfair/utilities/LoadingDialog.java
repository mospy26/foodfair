package com.foodfair.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.foodfair.R;


public class LoadingDialog {

    private Activity activity;
    private AlertDialog alertDialog;


    public LoadingDialog(Activity activity){

        this.activity = activity;
    }

    public void startLoadingAnimationg()
    {
        //Declare a builder to start the dialog activity creation
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        //Declare inflator object, to explode layout on screen
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        builder.setView(layoutInflater.inflate(R.layout.custom_dialog,null));
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void dismissDialog()
    {
        alertDialog.dismiss();
    }

}
