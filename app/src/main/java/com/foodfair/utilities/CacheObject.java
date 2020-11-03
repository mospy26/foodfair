package com.foodfair.utilities;

import java.io.Serializable;
import java.util.Date;

public class CacheObject implements Serializable {
    private Object data;
    private Date date;
    private int ttl;


    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }
}
