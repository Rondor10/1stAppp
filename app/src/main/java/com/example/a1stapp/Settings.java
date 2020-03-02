package com.example.a1stapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.LinkedList;

public class Settings extends AppCompatActivity {

    private final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    CheckBox gym, movie, football;
    Button btn_tomap, btn_save;
    double distance, longitude, latitude;
    TextView txt;
    SeekBar seekBar;
    FirebaseDatabase database;
    DatabaseReference mDatabaseRef;
    LinkedList categories;
    String Uid;
    private FirebaseUser user_Fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        findViews();
        initViews();

        user_Fb = FirebaseAuth.getInstance().getCurrentUser();
        Uid = user_Fb.getUid();
        categories = new LinkedList<String>();
        database = FirebaseDatabase.getInstance();
        mDatabaseRef = database.getReference();

        if (ActivityCompat.checkSelfPermission(Settings.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Settings.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Settings.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
        else {
            // Write you code here if permission already given.
        }
    }

    private void save() {
        categories.clear();
        final StringBuilder result = new StringBuilder();
        result.append("Selected Items:");

        if (gym.isChecked()) {
            result.append("\nGYM");
            categories.add("GYM");
        }
        if (movie.isChecked()) {
            result.append("\nMovie");
            categories.add("Movie");
        }
        if (football.isChecked()) {
            result.append("\nFootball");
            categories.add("Football");
        }
        result.append("\nDistance: " + distance);

        if(!(distance == 0) && !(categories.isEmpty()))
        {
            result.append("\nSaved successfully.");
            Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_LONG).show();
            updateData();
        }
        else if(distance == 0 && categories.isEmpty())
        {
            Toast.makeText(getApplicationContext(), "You must choose at least one category. Also, You must choose distance.", Toast.LENGTH_LONG).show();
        }
        else if(categories.isEmpty())
        {
            Toast.makeText(getApplicationContext(), "You must choose at least one category.", Toast.LENGTH_LONG).show();
        }
        else if(distance == 0)
        {
            Toast.makeText(getApplicationContext(), "You must choose distance.", Toast.LENGTH_LONG).show();
        }
    }

    private void setDistance(double value) {
        txt.setText("km: " + value);
        distance = value;
    }

    private void goToHomePage() {
        Intent intent = new Intent(Settings.this, HomePage.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // The user return to app after location request

            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the
                // contacts-related task you need to do.
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(Settings.this, "Permission denied to get location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateData() {
        ////Updating distance
        mDatabaseRef.child("users").child(Uid).child("distance").setValue(distance);
        //Updating Location
        Location loc1 = getLocation(Settings.this);
        if (loc1 != null) {

            //Updating Longitude
            longitude = loc1.getLongitude();
            mDatabaseRef.child("users").child(Uid).child("longitude").setValue(longitude);

            //Updating Latitude
            latitude = loc1.getLatitude();
            mDatabaseRef.child("users").child(Uid).child("latitude").setValue(latitude);
        }

        //Updating categories
        mDatabaseRef.child("users").child(user_Fb.getUid()).child("categories").setValue(categories);
    }

    public Location getLocation(Context context) {
        // Check location available permissions - if missing return (exit)
        boolean isPermission1Available = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean isPermission2Available = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (!isPermission1Available || !isPermission2Available) {
            return null;  // exit from function
        }

        // get last known location
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        return location;
    }

    private void findViews() {
        btn_tomap = findViewById(R.id.btn_tomap);
        seekBar = findViewById(R.id.seekBar);
        gym = findViewById(R.id.checkbox_gym);
        movie = findViewById(R.id.checkbox_movie);
        football = findViewById(R.id.checkbox_football);
        btn_save = findViewById(R.id.btn_save);
        txt = findViewById(R.id.txt);
    }

    private void initViews() {
        ////////Moving to the map(HomePage.class)
        btn_tomap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToHomePage();
            }
        });

        ///////Seek Bar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double value = 0.5 * (progress + 1);
                setDistance(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Applying the Listener on the Button click
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
    }
}


