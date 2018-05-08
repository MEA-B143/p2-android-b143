package com.b143lul.android.logreg;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class Pedometer extends AppCompatActivity {
    private TextView tvSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer);
        tvSteps = (TextView) findViewById(R.id.tvSteps);

        //MessageReceiver receiver = new MessageReceiver(new Message());

        if (!areStepsBeingCounted()) {

            // Mark Service as Started
            Preferences.setServiceRun(this, false);

            // Start Step Counting service
            Intent serviceIntent = new Intent(this, PedometerService.class);
            startService(serviceIntent);
        }

        tvSteps.setText(Preferences.getStepCount(Pedometer.this));
    }
/*
    public class Message {
        public void displayMessage(int resultCode, Bundle resultData) {

        }
*/

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
