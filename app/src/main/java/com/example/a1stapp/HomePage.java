package com.example.a1stapp;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import java.util.LinkedList;

public class HomePage extends AppCompatActivity {

    LinkedList<String> categories = new LinkedList<String>();
    double newUser_distance, newUser_latitude, newUser_longitude, currentUser_distance, currentUser_latitude, currentUser_longitude, distance_2users;
    String newUser_tel, newUser_Uid, currentUser_Uid;
    private Button btn, btn_toSet;
    private GoogleMap mMap;
    private FirebaseUser user_Fb;
    FirebaseDatabase database;
    DatabaseReference usersRef;
    private LatLng loc_currentUser;
    private Location current, newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_page);


        user_Fb = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        currentUser_Uid = user_Fb.getUid();
        usersRef = database.getReference().child("users");

        findViews();
        initViews();
        usersReferences();

        /// MAP - STUFF.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
            }
        });

    }

    public void createMarkers() {
        loc_currentUser = new LatLng(currentUser_latitude, currentUser_longitude);
        mMap.addMarker(new MarkerOptions().position(loc_currentUser).title("Hi, it's you."));

        current = new Location(LocationManager.GPS_PROVIDER);
        current.setLatitude(loc_currentUser.latitude);
        current.setLongitude(loc_currentUser.longitude);

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    newUser_Uid = ds.getKey();
                    if(!newUser_Uid.equals(currentUser_Uid)) //checking that he isn't the current user.
                    {
                        newUser_tel = ds.child("tel").getValue(String.class);
                        newUser_longitude = ds.child("longitude").getValue(double.class);
                        newUser_latitude = ds.child("latitude").getValue(double.class);

                        newUser = new Location(LocationManager.GPS_PROVIDER);
                        newUser.setLatitude(newUser_latitude);
                        newUser.setLongitude(newUser_longitude);

                        distance_2users = current.distanceTo(newUser)/1000;
                        distance_2users = roundDistance(distance_2users);
                        Log.d("OFER", "Distance of 2 users: " + distance_2users);
                        newUser_distance = ds.child("distance").getValue(double.class);

                        boolean flag1 = (distance_2users <= newUser_distance && distance_2users <= currentUser_distance);
                        if (flag1) {
                            usersRef.child(newUser_Uid).child("categories").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dsp) {
                                    LinkedList<String> categories_newUser = new LinkedList<String>();
                                    for (DataSnapshot postSnapshot : dsp.getChildren()) {
                                        Log.d("OFER", "User ID: " + newUser_Uid);
                                        String category = postSnapshot.getValue(String.class);
                                        if (categories.contains(category)) {
                                            categories_newUser.add(category);
                                        }
                                    }

                                    if (categories_newUser.size() > 0) {
                                        Log.d("OFER", newUser_Uid + " is good.");
                                        mMap.addMarker(new MarkerOptions().position(new LatLng(newUser_latitude, newUser_longitude)).title("PHONE NUMBER: " + newUser_tel + ", DISTANCE: " + distance_2users + ", CATEGORIES: " + categories_newUser.toString()));
                                    }
                                    Log.d("OFER", "User ID: " + newUser_Uid + ", Category list: " + categories_newUser.toString());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    throw databaseError.toException(); // never ignore errors
                                }
                            });
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        usersRef.addListenerForSingleValueEvent(eventListener);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc_currentUser, 13.0f));
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }

    private double roundDistance(double distance)
    {
        distance *= 10;
        distance = (double) Math.round(distance);
        distance /= 10;
        return  distance;
    }

    private void usersReferences()
    {
        usersRef.child(currentUser_Uid).child("latitude").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUser_latitude =  dataSnapshot.getValue(double.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        usersRef.child(currentUser_Uid).child("longitude").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUser_longitude = dataSnapshot.getValue(double.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        usersRef.child(currentUser_Uid).child("distance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUser_distance = dataSnapshot.getValue(double.class);
            }

            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        usersRef.child(currentUser_Uid).child("categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categories.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String category = postSnapshot.getValue(String.class);
                    categories.add(category);
                    Log.d("OFER", "Current User: Category = " + category);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void findViews()
    {
        btn_toSet = findViewById(R.id.btn_toset);
        btn = findViewById(R.id.btn_mapControl);

    }
    private void initViews()
    {
        btn_toSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Settings.class);
                startActivity(intent);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMarkers();
            }
        });
    }

}
