package com.b143lul.android.logreg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("CLOSE")) {

            // Mark Service as Stopped
            Preferences.setServiceRun(context, false);

            // Stop Service
            Intent stopIntent = new Intent(context, PedometerService.class);
            context.stopService(stopIntent);
        } else if (intent.getAction().equals("RESET")) {

            // Reset Step Counter
            Preferences.resetStepCount(context);

        } else if (intent.getAction().equals("SHARE")) {

            // Close Notification Drawer
            context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

            // Share Intent
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "I walked " + Preferences.getStepCount(context) + " steps!");

            // Chooser Intent
            Intent shareChooser = Intent.createChooser(shareIntent, "Share with");
            shareChooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(shareChooser);
        }
    }
}
