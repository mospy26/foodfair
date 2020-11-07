package com.foodfair.task;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.foodfair.network.BookFood;
import com.foodfair.network.Package;
import com.foodfair.network.TransmittedPackage;
import com.google.gson.Gson;

import static com.foodfair.task.MessageUtil.MESSAGE_BODY;

public class UiHandler extends Handler {

    static UiHandler instance = null;
    Context context;
    public UiHandler(Looper looper) {
        super(looper);
    }

    public static UiHandler createHandler(Looper looper, Context context) {
        if (instance != null) return instance;
        UiHandler uiHandler = new UiHandler(looper);
        instance = uiHandler;
        instance.context = context;
        return instance;
    }
    public static UiHandler getInstance() {
        return instance;
    }
    // This method will run on UI thread
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            // Our communication protocol for passing a string to the UI thread
            // Update GridView
            case MessageUtil.MESSAGE_WS_MESSAGE:
                handleWSMessage(msg);
                break;
            default:
                break;
        }
    }

    private void handleWSMessage(Message msg) {
        String jsonMessage = msg.getData().getString(MESSAGE_BODY);
        TransmittedPackage tMessage = new Gson().fromJson(jsonMessage, TransmittedPackage.class);
        // UI updating
        switch (tMessage.message_name){
            case Package.MESSAGE_NAME_BOOK_A_FOOD:
                TransmittedPackage tMWizDetail  = (TransmittedPackage) Package.buildFromMessage(jsonMessage, BookFood.class);
                BookFood bookFood = (BookFood) tMWizDetail.package_content;
               String notice =  String.format("Direct to food management panel, or show a dialog: consumer: %s, " +
                    "transaction: %s," +
                        " donor: %s", bookFood.consumer_id, bookFood.transaction_id,bookFood.donor_id);
//                NotificationManager NM;
//                NM=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

                new AlertDialog.Builder(context)
                        .setTitle("Food booked!")
                        .setMessage(notice)

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                break;
            default:
                break;
        }
    }
}
