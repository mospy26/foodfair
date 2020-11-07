package com.foodfair.task;

import android.os.Bundle;
import android.os.Message;

import java.io.Serializable;

/**
 * MessageUtil.java
 * <p>
 * Message's definition and creation class,
 * used to communicate to UI handler
 *
 * @author Huashuai Cai
 * @version 1.0
 * @since 2020-10-10
 */
public class MessageUtil {
    public static final int MESSAGE_WS_MESSAGE = 1;
    public static final int MESSAGE_NETWORK_STATUS = 2;
    public static final String MESSAGE_BODY = "MESSAGE_BODY";
    public static Message createMessage(int id, String data) {
        Bundle bundle = new Bundle();
        bundle.putString(MESSAGE_BODY, data);
        Message message = new Message();
        message.what = id;
        message.setData(bundle);
        return message;
    }
}
