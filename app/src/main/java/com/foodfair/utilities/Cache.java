package com.foodfair.utilities;

import java.util.Date;
import java.util.HashMap;

public class Cache {
    private HashMap<String, CacheObject> cache;

    public static Cache instance;

    public static Cache getInstance() {
        if (instance == null) {
            instance = new Cache();
        }
        return instance;
    }

    private Cache() {
        cache = new HashMap<>();
    }

    public static void add(String key, Cache cache) {

    }
}
