package com.foodfair.task;

import android.app.AlertDialog;
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

    public UiHandler(Looper looper) {
        super(looper);
    }

    public static UiHandler createHandler(Looper looper) {
        if (instance != null) return instance;
        UiHandler uiHandler = new UiHandler(looper);
        instance = uiHandler;
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
                break;
            default:
                break;
        }
    }
}
