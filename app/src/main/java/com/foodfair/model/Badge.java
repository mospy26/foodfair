package com.foodfair.model;

public class Badge {
    public Long id;
    public String description;
    public int resourceId;

    public Badge(Long id, String description, int resourceId) {
        this.id = id;
        this.description = description;
        this.resourceId = resourceId;
    }
}
