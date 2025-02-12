package com.foodfair.network;

import com.foodfair.task.MessageUtil;
import com.foodfair.task.UiHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import static com.foodfair.task.MessageUtil.MESSAGE_WS_MESSAGE;

public class FoodFairWSClient extends WebSocketClient {

    public static FoodFairWSClient globalCon;

    private FoodFairWSClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public FoodFairWSClient(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Gson g = new Gson();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Register current user id to ws server
        send(g.toJson(new Register(user.getUid())));

//         Tell the server what(specific package) you want to talk to whom(another id)
//        for (int i = 0; i < 3; i++) {
//            send(g.toJson(new BookFood("123456","123456","888888").buildToMessage("b","b")));
//        }
//        System.out.println("new connection opened");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(String message) {
        UiHandler.getInstance().sendMessage(MessageUtil.createMessage(MESSAGE_WS_MESSAGE,message));
    }

    @Override
    public void onMessage(ByteBuffer message) {
        System.out.println("received ByteBuffer");
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("an error occurred:" + ex);
    }
}