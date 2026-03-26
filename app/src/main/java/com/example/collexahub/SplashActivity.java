package com.example.collexahub;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(this::checkSession, SPLASH_TIME);
    }

    private void checkSession() {

        SessionManager sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedin()) {
            startActivity(new Intent(this, Login.class));
            finish();
            return;
        }

        Intent intent = new Intent(this, MainDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}