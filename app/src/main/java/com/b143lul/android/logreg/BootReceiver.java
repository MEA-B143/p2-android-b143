package com.b143lul.android.logreg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        // Clear Steps
        Preferences.clearStepCount(context);

        // Check if the Step Counter service was running?
        if (Preferences.shouldServiceRun(context))
        {
            // Start Step Counter service
            Intent myIntent = new Intent(context, PedometerService.class);
            context.startService(myIntent);
        }
    }
}