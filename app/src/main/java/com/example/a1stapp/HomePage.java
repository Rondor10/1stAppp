package com.example.a1stapp;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import guy4444.smartrate.SmartRate;

public class HomePage extends AppCompatActivity {
    private LinkedList<String> sharedHobbies = new LinkedList<>();
    private Button btn, btn_toSet;
    private String hobbies;
    private GoogleMap mMap;
    FloatingActionButton fab;
    private TextView foundUsersCount;
    private LatLng marker_currentUser;
    private Location loc_currentUser;
    private User host = new User(), guest; // Current user && New User

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        host.setKey(FirebaseAuth.getInstance().getCurrentUser().getUid());
        findViews();
        initViews();
        usersReferences();
        ///MAP - STUFF.
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
        mMap.clear();
        mMap.setInfoWindowAdapter(new MyMarkerAdapter(HomePage.this));
        //marker_currentUser = new LatLng(host.getLatitude(), host.getLongitude());
        marker_currentUser = new LatLng(32.070810, 34.812880); //GIVATAYIM, IL - FOR EXAMPLE

        List<String> currentUser_hobbies = new LinkedList<>();
        for (Map.Entry<String, Boolean> entry : host.getCategories().entrySet()) {
            if (entry.getValue()) {
                currentUser_hobbies.add(entry.getKey());
            }
        }
        if (marker_currentUser.latitude == 0 && marker_currentUser.longitude == 0) {
            mMap.addMarker((new MarkerOptions().position(new LatLng(0, 0)).title("YOUR PHONE NUMBER: " + host.getTel()).snippet("YOUR CHOSEN DISTANCE: " + host.getDistance() + " km." + "\n" + "YOUR HOBBIES: " + currentUser_hobbies.toString() + "\n").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))));
        } else {
            mMap.addMarker((new MarkerOptions().position(marker_currentUser).title("identity check").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))));
        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTitle().equals("identity check")) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        loc_currentUser = new Location(LocationManager.GPS_PROVIDER);
        loc_currentUser.setLatitude(marker_currentUser.latitude);
        loc_currentUser.setLongitude(marker_currentUser.longitude);
        final int[] cnt = {0};
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //dataSnapshot.getChildrenCount();
                    guest = ds.getValue(User.class);
                    if (!guest.getKey().equals(host.getKey())) {
                        Location loc_NewUser = new Location(LocationManager.GPS_PROVIDER);
                        loc_NewUser.setLatitude(guest.getLatitude());
                        loc_NewUser.setLongitude(guest.getLongitude());
                        double distance_2users = loc_currentUser.distanceTo(loc_NewUser) / 1000;
                        distance_2users *= 10;
                        distance_2users = (double) Math.round(distance_2users);
                        distance_2users /= 10;
                        Log.d("OFER", "Phone number: " + guest.getTel() + ", max distance: " + guest.getDistance() + ", actual distance: " + distance_2users + ", categories: " + guest.getCategories());
                        sharedHobbies.clear();
                        sharedHobbies = findingSharedHobbies(guest);
                        String hostBirthday = host.getBirthday();
                        String month_host = hostBirthday.substring(0,2); //0.2 , 3.5, 6
                        String day_host = hostBirthday.substring(3,5);
                        String year_host = hostBirthday.substring(6);
                        String guestBirthday = guest.getBirthday();
                        String month_guest = guestBirthday.substring(0,2); //0.2 , 3.5, 6
                        String day_guest = guestBirthday.substring(3,5);
                        String year_guest = guestBirthday.substring(6);

                        int m;
                        if(month_host.indexOf(0) == 0)
                            m = Integer.parseInt(month_host.substring(1));
                        else
                            m = Integer.parseInt(month_host);
                        int d;
                        if(day_host.indexOf(0) == 0)
                            d = Integer.parseInt(day_host.substring(1));
                        else
                            d = Integer.parseInt(day_host);
                        int y = Integer.parseInt(year_host);
                        int hostAge = getAge(y, m, d);

                        int m1;
                        if(month_host.indexOf(0) == 0)
                            m1 = Integer.parseInt(month_guest.substring(1));
                        else
                            m1 = Integer.parseInt(month_guest);
                        int d1;
                        if(day_host.indexOf(0) == 0)
                            d1 = Integer.parseInt(day_guest.substring(1));
                        else
                            d1 = Integer.parseInt(day_guest);
                        int y1 = Integer.parseInt(year_guest);
                        int guestAge = getAge(y1, m1, d1);

                        boolean flag1 = (distance_2users <= host.getDistance() && distance_2users <= guest.getDistance()); //נק'הנחה:המרחק ביני לבינו נמצא בטווח המרחקים של שנינו.
                        boolean flag2 = (sharedHobbies.size() > 0); //נק' הנחה: יש לנו תחביבים משותפים.
                        boolean flag3 = (host.getGenderWanted().equals(guest.getGender()) && (host.getGender().equals(guest.getGenderWanted()) || guest.getGenderWanted().equals("Flexible"))); //נק' הנחה: אני רוצה את מגדר הלקוח, והוא רוצה את מגדרי(ספציפית או שהוא גמיש)
                        boolean flag4 = (host.getGenderWanted().equals("Flexible") && (host.getGender().equals(guest.getGenderWanted()) || guest.getGenderWanted().equals("Flexible"))); //נק' הנחה: אני גמיש מגדרית, והוא רוצה את מגדרי(ספציפית או שהוא גמיש גם)
                        boolean flag5 = ((host.getMinAge() <= guestAge && host.getMaxAge() >= guestAge) && (guest.getMinAge() <= hostAge && guest.getMaxAge() >= hostAge)); //נק' הנחה: אני בטווח הגילאים שלו, והוא בטווח הגילאים שלי
                        Log.d("OFER", host.getGenderWanted() + " " + guest.getGender());
                        if (flag1 && flag2 && (flag3 || flag4) && flag5) {
                            cnt[0] = cnt[0] + 1;
                            hobbies = sharedHobbies.toString();
                            hobbies = hobbies.replace("[", "");
                            hobbies = hobbies.replace("]", "");
                            hobbies = hobbies.toLowerCase();
                            if(sharedHobbies.size() > 3)
                                mMap.addMarker((new MarkerOptions().position(new LatLng(guest.getLatitude(), guest.getLongitude())).title("Name: " + guest.getFirstName() + " " + guest.getLastName()).snippet("Gender: " + guest.getGender() + "\n" + "Age: " + guestAge + "\n" + "Distance: " + distance_2users + " km" + "\n" + "Shared hobbies: " + sharedHobbies.get(0).toLowerCase() + ", " + sharedHobbies.get(1).toLowerCase() + ", " + sharedHobbies.get(2).toLowerCase() + ", etc.")));
                            if(sharedHobbies.size() <= 3)
                                mMap.addMarker((new MarkerOptions().position(new LatLng(guest.getLatitude(), guest.getLongitude())).title("Name: " + guest.getFirstName() + " " + guest.getLastName()).snippet("Gender: " + guest.getGender() + "\n" + "Age: " + guestAge + "\n" + "Distance: " + distance_2users + " km" + "\n" + "Shared hobbies: " + hobbies + ".")));
                        }
                    }
            }
                if (cnt[0] == 0) {
                    foundUsersCount.setTextSize(11);
                    foundUsersCount.setText("We haven't found you any users nearby, we're sorry for the inconvenience. ");

                } else {
                    foundUsersCount.setText("We have found for you " + cnt[0] + " users nearby.");
                }
                foundUsersCount.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("ERROR MSG");
            }
        };

        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(eventListener);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker_currentUser, 11.8f));
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(HomePage.this, ChatActivity.class);
                startActivity(intent);
            }
        });
        btn.setVisibility(View.GONE);
    }

    private void usersReferences() {
        FirebaseDatabase
                .getInstance()
                .getReference()
                .child("users")
                .child(host.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User temp = dataSnapshot.getValue(User.class);
                        if (temp != null && temp.getKey().equals(host.getKey())) {
                            host = dataSnapshot.getValue(User.class);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private LinkedList<String> findingSharedHobbies(User guest) {

        List<String> currentUser_hobbies = new LinkedList<>();
        List<String> newUser_hobbies = new LinkedList<>();

        for (Map.Entry<String, Boolean> entry : host.getCategories().entrySet()) {
            if (entry.getValue()) {
                currentUser_hobbies.add(entry.getKey());
            }
        }

        for (Map.Entry<String, Boolean> entry : guest.getCategories().entrySet()) {
            if (entry.getValue()) {
                newUser_hobbies.add(entry.getKey());
            }
        }

        LinkedList<String> shared = intersection(currentUser_hobbies, newUser_hobbies);
        return shared;
    }

    private LinkedList<String> intersection(List<String> list1, List<String> list2) {

        LinkedList<String> sharedCategories = new LinkedList<>();
        for (String category : list1) {
            if (list2.contains(category)) {
                sharedCategories.add(category);
            }
        }
        return sharedCategories;
    }

    private void findViews() {
        btn_toSet = findViewById(R.id.btn_toset);
        btn = findViewById(R.id.btn_mapControl);
        foundUsersCount = findViewById(R.id.foundUsersCount);
        fab = findViewById(R.id.fab);
    }

    private void initViews() {
        //Update MAP
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMarkers();
            }
        });
        //Move to Settings.class
        btn_toSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Settings.class);
                startActivity(intent);
            }
        });
        //Floating action bar - rate app button.
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateFunction();
            }
        });
    }

    private void rateFunction() {
        SmartRate.Rate(HomePage.this
                , "Rate Us"
                , "Tell others what you think about this app"
                , "Continue"
                , "Please take a moment and rate us on Google Play"
                , "click here"
                , "Cancel"
                , "Thanks for the feedback"
                , Color.parseColor("#257EC5")
                , 1
        );
    }

    public int getAge(int _year, int _month, int _day) {

        GregorianCalendar cal = new GregorianCalendar();
        int y, m, d, a;
        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(_year, _month, _day);
        a = y - cal.get(Calendar.YEAR);
        if ((m < cal.get(Calendar.MONTH))
                || ((m == cal.get(Calendar.MONTH)) && (d < cal
                .get(Calendar.DAY_OF_MONTH)))) {
            --a;
        }
        return a;
    }
}

