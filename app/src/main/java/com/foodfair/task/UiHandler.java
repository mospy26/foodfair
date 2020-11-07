package com.foodfair.task;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.core.app.NotificationCompat;

import com.foodfair.MainActivity;
import com.foodfair.R;
import com.foodfair.network.BookFood;
import com.foodfair.network.Package;
import com.foodfair.network.TransmittedPackage;
import com.foodfair.notification.Notification;
import com.foodfair.utilities.Cache;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import static com.foodfair.task.MessageUtil.MESSAGE_BODY;
import static com.foodfair.task.MessageUtil.MESSAGE_NETWORK_STATUS;
import static com.foodfair.task.MessageUtil.MESSAGE_NOTIFICATION;

public class UiHandler extends Handler {

    static UiHandler instance = null;
    public Context context;
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
            case MESSAGE_NETWORK_STATUS:
                handleNetworkConnectionStatus(Boolean.parseBoolean(msg.getData().getString(MESSAGE_BODY)));
                break;
            case MESSAGE_NOTIFICATION:
                handleNotificationMessage(msg.getData().getString(MESSAGE_BODY));
                break;
            default:
                break;
        }
    }

    private void handleNotificationMessage(String string) {
        Notification notification = new Gson().fromJson(string,Notification.class);
        String title = notification.title;
        String content = notification.content;
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "123";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.home_icon)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void handleNetworkConnectionStatus(boolean currentConnectionStatu) {
        Boolean beforeConnectionStatus =(Boolean)
                Cache.getInstance(context).getStoredObject(context.getResources().getString(R.string.CACHE_KEY_NETWORK_STATUS));
        if (beforeConnectionStatus == null){
            beforeConnectionStatus = true;
        }
        if (beforeConnectionStatus){
            if (!currentConnectionStatu){
                Snackbar.make(((Activity)context).getWindow().getDecorView(),
                        "Lose Network Connection.", Snackbar.LENGTH_LONG).show();
            }
        }
        Cache.getInstance(context).add(context.getResources().getString(R.string.CACHE_KEY_NETWORK_STATUS),
                new Boolean(currentConnectionStatu));

        Gson g = new Gson();
        this.sendMessage(MessageUtil.createMessage(MESSAGE_NOTIFICATION,
                g.toJson(new Notification("New Food Booked","234"))));
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

                Gson g = new Gson();
                this.sendMessage(MessageUtil.createMessage(MESSAGE_NOTIFICATION,
                        g.toJson(new Notification("New Food Booked",notice))));

//                new AlertDialog.Builder(context)
//                        .setTitle("Food booked!")
//                        .setMessage(notice)
//
//                        // Specifying a listener allows you to take an action before dismissing the dialog.
//                        // The dialog is automatically dismissed when a dialog button is clicked.
//                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                // Continue with delete operation
//                            }
//                        })
//
//                        // A null listener allows the button to dismiss the dialog and take no further action.
//                        .setNegativeButton(android.R.string.no, null)
//                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .show();
                break;
            default:
                break;
        }
    }
}
