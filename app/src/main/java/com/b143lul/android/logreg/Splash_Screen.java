package com.b143lul.android.logreg;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class Splash_Screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash__screen);
        if (!areStepsBeingCounted()) {

            // Mark Service as Started
            Preferences.setServiceRun(this, false);

            // Start Step Counting service
            Intent serviceIntent = new Intent(this, PedometerService.class);
            startService(serviceIntent);
        }

        Thread th = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Intent obj = new Intent(Splash_Screen.this, Signup.class);
                    startActivity(obj);
                }
            }

        };
        th.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private boolean areStepsBeingCounted() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.b143lul.android.logreg.PedometerService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

