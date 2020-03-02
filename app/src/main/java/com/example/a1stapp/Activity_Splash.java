package com.example.a1stapp;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Activity_Splash extends AppCompatActivity {

    int RC_SIGN_IN = 1234;
    private FirebaseUser user_Fb;
    User newUser;
    private DatabaseReference mDatabase;
    private ImageView splash_IMG_logo;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // To remove status bar - put before setContentView()!
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        relativeLayout = findViewById(R.id.relativeLayout);
        splash_IMG_logo = findViewById(R.id.splash_IMG_logo);
        splash_IMG_logo.setAlpha(0f);
        splash_IMG_logo.animate()
                .setDuration(1000)
                .translationY(0)
                .alpha(1.0f)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        StartProcess();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
    }

    private void openHomePage() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
        finish();
    }  //Opens MAP

    private void StartProcess()   //Starting process
    {
        user_Fb = FirebaseAuth.getInstance().getCurrentUser();

        if (user_Fb != null) {
            Log.d("OFER", "user: " + user_Fb.getPhoneNumber());
            openHomePage();
        } else {
            Log.d("OFER", "user: null");
            login();
        }
    }

    private void login() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());
        startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("OFER", "requestCode: " + requestCode);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK)
            {   // Successfully signed in
                user_Fb = FirebaseAuth.getInstance().getCurrentUser();
                String phoneNumber = user_Fb.getPhoneNumber();
                Log.d("OFER", "NEW USER PHONE NUMBER: " + phoneNumber);
                Log.d("OFER", "NEW USER ID: " + user_Fb.getUid());
                addNewUser(phoneNumber);
                relativeLayout.setBackgroundColor(getResources().getColor(R.color.white));
                splash_IMG_logo.setVisibility(View.GONE);
                openHomePage();

            }
            else
            {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    private void addNewUser(String phoneNumber)
    {
        newUser = new User(phoneNumber);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(user_Fb.getUid()).setValue(newUser);
    }
}

