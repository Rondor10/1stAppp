package com.example.a1stapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a1stapp.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

import java.util.HashMap;
import java.util.Map;

public class Settings extends AppCompatActivity {

    private final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private MultiSelectSpinner mSpinner;
    int MY_SIGN = 0;
    private String firstName = "", lastName = "", wantedGender = "";
    private RangeSeekBar SeekBarRange;
    private Button btn_save;
    private TextView txt, textview, titleMsg, min;
    private RadioGroup radioGroup_wantedGender;
    private ImageButton facebook_btn, instagram_btn, goToChat_btn;
    private SeekBar seekBar;
    private User host = new User();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        host.setKey(FirebaseAuth.getInstance().getCurrentUser().getUid());
        host.setTel(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        usersReferences();
        findViews();
        if (ActivityCompat.checkSelfPermission(Settings.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Settings.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Settings.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
        else {
            // Write you code here if permission already given.
        }
        updateLocation(); //Keeping location - even if button isn't clicked.
        initViews();
    }

    private void save() {
        host.getCategories().clear();
        String selectedCategories = mSpinner.getSelectedItemsAsString();
        insertData(selectedCategories);
        String check = "";
        if(radioGroup_wantedGender.getCheckedRadioButtonId() == -1) { //Check if wanted partner's gender is selected.
            check += "You must select your desirable partner's gender. ";
        }

        if(MY_SIGN == 0)
            check += "You Must Choose An Age Range. ";

        if(host.getDistance() == 0) { //Check if distance is selected.
            check += "You must choose a distance. ";
        }

        int i = 0;
        for (Map.Entry<String, Boolean> entry : host.getCategories().entrySet()) {
            if(entry.getValue()) {
                i++;
            }
        }
        if(i == 0) { //Check if hobbies selected.
            check += "You must choose at least a single hobby.";
        }
        if(!check.isEmpty()) {
            Toast.makeText(getApplicationContext(), check, Toast.LENGTH_LONG).show();
        }
        else {
            host.setGenderWanted(wantedGender);
            FirebaseDatabase.getInstance().getReference().child("users").child(host.getKey()).setValue(host);
            Intent intent = new Intent(this, HomePage.class);
            startActivity(intent);
        }
    }

    private void usersReferences() {
        FirebaseDatabase
                .getInstance()
                .getReference()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int cntUsers = dataSnapshot.child("usersCNT").getValue(int.class);
                        textview.setText("Welcome to our community of " + cntUsers + " people!");
                        User temp = dataSnapshot.child("users").child(host.getKey()).getValue(User.class);
                        if (temp != null && temp.getKey().equals(host.getKey())) {
                            host = dataSnapshot.child("users").child(host.getKey()).getValue(User.class);
                            host.setDistance(0);
                            firstName = host.getFirstName();
                            lastName = host.getLastName();
                            titleMsg.setText("Hi " + firstName +  " " + lastName + ", Let's move!");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void updateLocation() {
        //Updating Location
        Location loc1 = getLocation(Settings.this);
        if (loc1 != null) {
            double latitude = loc1.getLatitude();
            double longitude = loc1.getLongitude();
            host.setLatitude(latitude);
            host.setLongitude(longitude);
        }
    }

    private Location getLocation(Context context) {
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

    private void insertData(String str) {
        host.getCategories().put(CATEGORY.FOOTBALL.name(), str.contains("FOOTBALL"));
        host.getCategories().put(CATEGORY.GYM.name(), str.contains("GYM"));
        host.getCategories().put(CATEGORY.MOVIE.name(), str.contains("MOVIE"));
        host.getCategories().put(CATEGORY.SWIMMING.name(), str.contains("SWIMMING"));
        host.getCategories().put(CATEGORY.SHOPPING.name(), str.contains("SHOPPING"));
        host.getCategories().put(CATEGORY.BASKETBALL.name(), str.contains("BASKETBALL"));
        host.getCategories().put(CATEGORY.SNOOKER.name(), str.contains("SNOOKER"));
        host.getCategories().put(CATEGORY.CONCERT.name(), str.contains("CONCERT"));
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

    public void onSelectWantedGender(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.wantedMale:
                if (checked) {
                    wantedGender = "Male";
                    break;
                }
            case R.id.wantedFemale:
                if (checked) {
                    wantedGender = "Female";
                    break;
                }
            case R.id.wantedOther:
                if (checked) {
                    wantedGender = "Other";
                    break;
                }
            case R.id.wantedFlexible:
                if (checked) {
                    wantedGender = "Flexible";
                    break;
                }
        }
    }

    private void findViews() {

        facebook_btn = findViewById(R.id.facebook_btn);
        instagram_btn = findViewById(R.id.instagram_btn);
        titleMsg = findViewById(R.id.titleMsg);
        seekBar = findViewById(R.id.seekBar);
        mSpinner = findViewById(R.id.mSpinner);
        min = findViewById(R.id.min);
        SeekBarRange= findViewById(R.id.SeekBarRange);
        btn_save = findViewById(R.id.btn_save);
        txt = findViewById(R.id.txt);
        textview = findViewById(R.id.textview);
        radioGroup_wantedGender = findViewById(R.id.radioGroup_wantedGender);
        goToChat_btn = findViewById(R.id.goToChat_btn);
    }

    private void initViews() {
        ///Open Facebook
        facebook_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pageId = "";
                String pageUrl = "https://www.facebook.com/" + pageId;
                try {
                    ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo("com.facebook.katana", 0);
                    if (applicationInfo.enabled) {
                        int versionCode = getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
                        String url;
                        if (versionCode >= 3002850) {
                            url = "fb://facewebmodal/f?href=" + pageUrl;
                        } else {
                            url = "fb://page/" + pageId;
                        }
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    } else {
                        throw new Exception("Facebook is disabled");
                    }
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(pageUrl)));
                }
            }
        });
        ///Open Instagram
        instagram_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://instagram.com/_u/ron_dor10");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                likeIng.setPackage("com.instagram.android");
                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/_u/ron_dor10")));
                }
            }
        });

        ///////Range Seek Bar
        SeekBarRange.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                min.setText(minValue + " - " + + maxValue);
                host.setMinAge(minValue);
                host.setMaxAge(maxValue);
                MY_SIGN = 1;
            }
        });
        ///////Seek Bar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double value = 0.5 * (progress + 1);
                txt.setText(value + " km");
                host.setDistance(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        ////Spinner
        final String[] select_Categories = {
                "Select your favorite hobbies", "FOOTBALL", "GYM", "MOVIE", "SWIMMING",
                "SHOPPING", "BASKETBALL", "SNOOKER", "CONCERT"};
        mSpinner.setItems(select_Categories);
        //Applying the Listener on the Button click & get username,gender if not exist
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        goToChat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.this, ChatActivity.class);
                startActivity(intent);
            }
        });
    }

    private void status(boolean status){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(host.getKey());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online", status);
        reference.updateChildren(hashMap);
    }

    protected void onResume() {
        super.onResume();
        status(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        status(false);
    }
}



