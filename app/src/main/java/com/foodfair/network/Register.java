package com.foodfair.network;

public class Register extends Package {
    String id;
    Register(String id) {
        super(Package.MESSAGE_NAME_REGISTER);
        this.id = id;
    }
}
