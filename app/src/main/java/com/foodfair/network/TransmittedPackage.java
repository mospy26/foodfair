package com.foodfair.network;

public class TransmittedPackage extends Package {
    public String to_id;
    public String from_id;
    public Package package_content;
    public int send_time;
    // Server don't care the detail business logic, just tell server who you want to talk with
    public TransmittedPackage(String to_id, String from_id, int send_time, Package package_content) {
        super(package_content.message_name);
        this.to_id = to_id;
        this.from_id = from_id;
        this.package_content = package_content;
        this.send_time = send_time;
    }
}
