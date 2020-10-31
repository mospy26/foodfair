package com.foodfair.model;

import com.google.firebase.Timestamp;

import java.util.List;
import java.util.Map;

public class UsersInfo {

    public static final String FIELD_ALLERGY = "allergy";
    public static final String FIELD_AS_CONSUMER = "as-consumer";
    public static final String FIELD_AS_DONOR = "as-donor";
    public static final String FIELD_BIO = "bio";
    public static final String FIELD_BIRTHDAY = "birthday";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_GENDER = "gender";
    public static final String FIELD_JOIN_DATE = "join-date";
    public static final String FIELD_LAST_LOGIN = "last-login";
    public static final String FIELD_LOCATION = "location";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_PASSWORD_HASH = "password-hash";
    public static final String FIELD_PREFERENCE = "preference";
    public static final String FIELD_PROFILE_IMAGE = "profile-image";
    public static final String FIELD_STATUS = "status";

    private List<Long> allergy;
    private Map<String, Object> asConsumer;
    private Map<String, Object> asDonor;
    private String bio;
    private Timestamp birthday;
    private String email;
    private Long gender;
    private Timestamp joinDate;
    private Timestamp lastLogin;
    private String location;
    private String name;
    private String passwordHash;
    private Long preference;
    private String profileImage;
    private Long status;

    public static final String FIREBASE_COLLECTION_USER_INFO = "usersInfo";
    public static final String ITEMS_ON_SHELF = "itemsOnShelf";
    public static final String ITEMS_REVIEWED = "itemsReviewed";
    public static final Long FIREBASE_COLLECTION_USER_INFO_GENDER_VALUE_MALE = 1L;
    public static final String FIREBASE_COLLECTION_USER_INFO_SUB_KEY_OF_AS_CONSUMER_REVIEWS = "reviews";
    public static final String FIREBASE_COLLECTION_USER_INFO_SUB_KEY_OF_AS_CONSUMER_BADGES = "badges";
    public static final String FIREBASE_COLLECTION_USER_INFO_SUB_KEY_OF_AS_DONOR_BADGES = "badges";


    public List<Long> getAllergy() {
        return allergy;
    }

    public void setAllergy(List<Long> allergy) {
        this.allergy = allergy;
    }

    public Map<String, Object> getAsConsumer() {
        return asConsumer;
    }

    public void setAsConsumer(Map<String, Object> asConsumer) {
        this.asConsumer = asConsumer;
    }

    public Map<String, Object> getAsDonor() {
        return asDonor;
    }

    public void setAsDonor(Map<String, Object> asDonor) {
        this.asDonor = asDonor;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Timestamp getBirthday() {
        return birthday;
    }

    public void setBirthday(Timestamp birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getGender() {
        return gender;
    }

    public void setGender(Long gender) {
        this.gender = gender;
    }

    public Timestamp getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Timestamp joinDate) {
        this.joinDate = joinDate;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Long getPreference() {
        return preference;
    }

    public void setPreference(Long preference) {
        this.preference = preference;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }
}
