package com.example.a1stapp;

import java.util.HashMap;

public class User {

    private String key = "";
    private String birthday = "";
    private String firstName = "";
    private String lastName = "";
    private String gender = "";
    private String genderWanted = "";
    private int minAge = 0;
    private int maxAge = 0;
    private HashMap<String, Boolean> categories = new HashMap<>();
    private double distance = 0;
    private double latitude = 0;
    private double longitude = 0;
    private String tel = "";

    public User() { }

    public User(String tel, String key) {
        this.tel = tel; this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGenderWanted() {
        return genderWanted;
    }

    public void setGenderWanted(String genderWanted) {
        this.genderWanted = genderWanted;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public HashMap<String, Boolean> getCategories() {
        return categories;
    }

    public void setCategories(HashMap<String, Boolean> categories) {
        this.categories = categories;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}