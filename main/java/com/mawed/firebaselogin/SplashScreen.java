package com.mawed.firebaselogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_SCREEN_RUN_TIME_OUT = 500 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(() -> {

            Intent MainUI = new Intent(SplashScreen.this, MainUI.class);
            Intent HomeScreen = new Intent(SplashScreen.this,MainActivity.class);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                // User is signed in
                startActivity(MainUI);
            } else {
                // No user is signed in
                 startActivity(HomeScreen);
            }

            finish();
        },SPLASH_SCREEN_RUN_TIME_OUT);
    }
}