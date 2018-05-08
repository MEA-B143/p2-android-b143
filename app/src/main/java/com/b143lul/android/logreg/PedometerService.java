package com.b143lul.android.logreg;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.Toast;

public class PedometerService extends Service implements SensorEventListener {

    private static final Integer NOTIFICATION_ID = 7837;
    private Notification.Builder mBuilder;
    private NotificationManager mNotificationManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        // Get Notification Manager
        mBuilder = new Notification.Builder(this);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Setup Step Counter
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            Toast.makeText(this, "Started Counting Steps", Toast.LENGTH_LONG).show();
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Device not Compatible!", Toast.LENGTH_LONG).show();
            this.stopSelf();
        }

        // Setup First Notification
        updateNotification(true);

        // Setup Shared Preference Change Listener
        SharedPreferences sharedPreferences = getSharedPreferences("stepcounter_prefs", MODE_PRIVATE);
        SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                // Update Notification Bar
                updateNotification(false);
            }
        };
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        // Restart the service if its killed
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // Record Step Count
        Preferences.setStepCount(this, (int) event.values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void updateNotification(boolean firstTime) {

        // Update Step Count
        mBuilder.setContentTitle(Preferences.getStepCount(this) + " steps taken");

        Notification notification;

        // First Time Setup
        if (firstTime) {
            mBuilder.setSmallIcon(R.drawable.ic_notifications_black_24dp);
            //mBuilder.setPriority(-1);
            mBuilder.setContentText("Step Counter - Counting");
            mBuilder.setOngoing(true);
            mBuilder.setAutoCancel(false);
            mBuilder.setOnlyAlertOnce(true);
            //mBuilder.setVisibility(Notification.VISIBILITY_SECRET);

            // Share Button
            Intent shareIntent = new Intent(this, NotificationReceiver.class);
            shareIntent.setAction("SHARE");
            PendingIntent pShareIntent = PendingIntent.getBroadcast(this, 0, shareIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.addAction(1, "Share", pShareIntent);

            // Reset Button
            Intent resetIntent = new Intent(this, NotificationReceiver.class);
            resetIntent.setAction("RESET");
            PendingIntent pResetIntent = PendingIntent.getBroadcast(this, 0, resetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.addAction(2, "Reset", pResetIntent);

            // Close Button
            Intent closeIntent = new Intent(this, NotificationReceiver.class);
            closeIntent.setAction("CLOSE");
            PendingIntent pCloseIntent = PendingIntent.getBroadcast(this, 0, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.addAction(3, "Close", pCloseIntent);

            notification = mBuilder.build();

            // Set Service to run in the Foreground
            startForeground(NOTIFICATION_ID, notification);
        } else {
            notification = mBuilder.build();
        }

        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

}
