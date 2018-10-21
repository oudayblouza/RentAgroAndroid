package com.esprit.rentagro.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.PackageManager;
import android.os.Handler;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Space;
import android.widget.Toast;

import com.esprit.rentagro.R;
import com.jaeger.library.StatusBarUtil;
import com.victor.loading.newton.NewtonCradleLoading;

public class SplashActivity extends AppCompatActivity {


    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        NewtonCradleLoading newtonCradleLoading; newtonCradleLoading = (NewtonCradleLoading)findViewById(R.id.newton_cradle_loading);
        newtonCradleLoading.start();
        StatusBarUtil.setTransparent(SplashActivity.this);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
            SharedPreferences sharedPreferences = getSharedPreferences("userpref", SplashActivity.this.MODE_PRIVATE);
                if (sharedPreferences.getInt("id", 0) != 0) {

                    Intent intent = new Intent(SplashActivity.this, test.class);
                    startActivity(intent);
                } else {


                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);


                }
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}
