package com.foodfair.utilities;

import com.foodfair.R;
import com.foodfair.model.Badge;

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

    public final HashMap<Long, Badge> CONSUMER_BADGES = new HashMap<Long, Badge>(){{
        put(100L, new Badge(100L,"Receive donation for the first time.", R.drawable.consume_1));
        put(101L, new Badge(101L,"Receive donation for more than 5 times.", R.drawable.consume_5));
    }};
    public final HashMap<Long, Badge> DONOR_BADGES = new HashMap<Long, Badge>(){{
        put(200L, new Badge(200L,"Donate for the first time.", R.drawable.donate_1));
        put(201L, new Badge(201L,"Donate for more than 5 times.", R.drawable.donate_5));
    }};

    public final HashMap<String, Long> TRANSACTION_STATUS = new HashMap<String, Long>(){{
        put("Booked", 0L);
        put("Cancelled", 1L);
        put("Success", 2L);
    }};
    public static Const getInstance(){
        return instance;
    }
}
