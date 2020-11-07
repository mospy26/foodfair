package com.foodfair.network;

public class BookFood extends Package {
    public String consumer_id;
    public String donor_id;
    public String transaction_id;
    public BookFood(String consumer_id, String donor_id, String transaction_id) {
        super(Package.MESSAGE_NAME_BOOK_A_FOOD);
        this.consumer_id = consumer_id;
        this.donor_id = donor_id;
        this.transaction_id = transaction_id;
    }
}
