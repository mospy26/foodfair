package com.foodfair.network;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

public class Package {
    public int message_name;
    Package(int message_name){
        this.message_name = message_name;
    }

    public static final int MESSAGE_NAME_REGISTER = 1;
    public static final int MESSAGE_NAME_BOOK_A_FOOD = 2;

    public TransmittedPackage buildToMessage(String to_id, String from_id){
        return new TransmittedPackage(to_id,from_id,123,this);
    }
    public static Object buildFromMessage(String message, Class clazz){
        Gson g = new Gson();
        TransmittedPackage tMessage = new Gson().fromJson(message, TransmittedPackage.class);
        Object packageContent = g.fromJson(new JsonParser().parse(message).getAsJsonObject().get("package_content"),clazz);
        tMessage.package_content = (Package) packageContent;
        return tMessage;
    }
}

