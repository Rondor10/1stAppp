package com.example.a1stapp;

import java.util.LinkedList;

public class User
{
    public LinkedList<String> categories;
    public double distance;
    public double latitude;
    public double longitude;
    public String tel;

    public User(String tel)
    {
        categories = new LinkedList<>();
        distance = latitude = longitude = -1;
        this.tel = tel;
    }

}