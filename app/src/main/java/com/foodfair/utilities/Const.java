package com.foodfair.utilities;

import java.util.HashMap;

public class Const {
    public static Const instance;
    static {
        instance = new Const();
    }
    public final HashMap<Long, String> ALLERGY_DETAIL = new HashMap<Long, String>() {{
        put(0L, "egg");
        put(1L, "milk");
        put(2L, "bass");
        put(3L, "flounder");
        put(4L, "cod");
        put(5L, "crab");
        put(6L, "lobster");
        put(7L, "shrimp");
        put(8L, "almonds");
        put(9L, "walnuts");
        put(10L, "pecans");
        put(11L, "peanuts");
        put(12L, "wheat");
        put(13L, "soybeans");
        put(14L, "pork");
        put(15L, "lamb");
        put(16L, "beef");
    }};

    public final HashMap<Long, String> FOOD_TYPE_DETAIL = new HashMap<Long, String>() {{
        put(1L, "ramen");
        put(2L, "burger");
        put(3L, "pizza");
        put(4L, "roll");
        put(5L, "chinese dish");
        put(6L, "tart");
        put(7L, "cake");
        put(8L, "beverage");
        put(9L, "roast");
    }};

    public static Const getInstance(){
        return instance;
    }
}
