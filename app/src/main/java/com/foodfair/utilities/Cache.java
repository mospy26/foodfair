package com.foodfair.utilities;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Cache {
    private HashMap<String, CacheObject> cache;
    private static File cacheDir;

    public static Cache instance;

    public static Cache getInstance(Context context) {
        if (instance == null) {
            instance = new Cache();
            cacheDir = context.getCacheDir();
            cacheDir = new File(cacheDir, "cache");
            if (!new File(cacheDir, "cache").isFile()) {
                try {
                    cacheDir.createNewFile();
                } catch (IOException e) {
                    Log.e("Cache File Create", e.getMessage());
                }
            }
        }
        return instance;
    }

    private Cache() {
        cache = new HashMap<>();
    }

    public void add(String key, Object object) {
        CacheObject o = new CacheObject();
        o.setData(object);
        o.setDate(new Date(System.currentTimeMillis()));
        o.setTtl(500);
        cache.put(key, o);
        persist(key);
    }

    public void remove(String key) {
        cache.remove(key);
    }

    public CacheObject get(String key) {
        return cache.getOrDefault(key, null);
    }

    public Object getStoredObject(String key){
        CacheObject cacheObject = cache.getOrDefault(key, null);
        if (cacheObject == null) return null;
        return cacheObject.getData();
    }

    private void persist(String key) {
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }

        try {
            new FileWriter(cacheDir, false).close();
        } catch (IOException e) {
            Log.e("Cache Write", e.getMessage());
            return;
        }

        try {
            FileOutputStream fout = new FileOutputStream(cacheDir);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(cache);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Cache Write", e.getMessage());
            return;
        }
    }

    private void delete(String key) {

    }

    private void loadCache() {
        try {
            FileInputStream f = new FileInputStream(cacheDir);
            ObjectInputStream s = new ObjectInputStream(f);
            cache = (HashMap<String, CacheObject>) s.readObject();
            s.close();
        } catch (IOException e) {
            Log.e("Cache Read", e.getMessage());
            return;
        } catch (ClassNotFoundException e) {
            Log.e("Cache Read", e.getMessage());
            return;
        }

        // remove expired caches
        Calendar c = Calendar.getInstance();
        ArrayList<String> expired = new ArrayList<String>();

        for (Map.Entry<String, CacheObject> entry : cache.entrySet()) {
            c.setTime(entry.getValue().getDate());
            c.add(Calendar.SECOND, (int) entry.getValue().getTtl());
            Date date = c.getTime();

            if (new Date(System.currentTimeMillis()).compareTo(date) <= 0) {
                expired.add(entry.getKey());
            }
        }

        cache.keySet().removeAll(expired);
    }
}
