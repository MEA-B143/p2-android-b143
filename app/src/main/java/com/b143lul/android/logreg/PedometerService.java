package com.b143lul.android.logreg;

<<<<<<< HEAD
<<<<<<< HEAD

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
=======
import android.app.Service;
import android.content.Context;
import android.content.Intent;
>>>>>>> b8e8e68394cecbd6d6b165bc2b76576e02d93396
=======
import android.app.Service;
import android.content.Context;
import android.content.Intent;
>>>>>>> b8e8e68394cecbd6d6b165bc2b76576e02d93396
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
<<<<<<< HEAD
<<<<<<< HEAD
import android.os.IBinder;
import android.util.Log;


public class PedometerService extends Service implements SensorEventListener {

    SensorManager sensorManager;
    Sensor stepCounterSensor;
    Sensor stepDetectorSensor;


    int currentStepsDetected;

    int stepCounter;
    int newStepCounter;

    boolean serviceStopped;

    Intent intent;

    private static final String TAG = "StepService";
    public static final String BROADCAST_ACTION = "com.websmithing.elina.mybroadcast";

    private final android.os.Handler handler = new android.os.Handler() {
    };
    int counter = 0;



    @Override
    public void onCreate() {
        super.onCreate();

        intent = new Intent(BROADCAST_ACTION);

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("Service", "Start");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener(this, stepCounterSensor, 0);
        sensorManager.registerListener(this, stepDetectorSensor, 0);

        //currentStepCount = 0;
        currentStepsDetected = 0;
        stepCounter = 0;
        newStepCounter = 0;

        serviceStopped = false;

        // --------------------------------------------------------------------------- \\
        // ___ (3) start handler ___ \\
        /////if (serviceStopped == false) {
        // remove any existing callbacks to the handler
        handler.removeCallbacks(updateBroadcastData);
        // call our handler with or without delay.
        handler.post(updateBroadcastData); // 0 seconds
        /////}
        // ___________________________________________________________________________ \\

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("Service", "Stop");

        serviceStopped = true;

    }

    /** Called when the overall system is running low on memory, and actively running processes should trim their memory usage. */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int countSteps = (int) event.values[0];




            if (stepCounter == 0) {
                stepCounter = (int) event.values[0];
            }
            //figure this out
            newStepCounter = countSteps - stepCounter;
        }


        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            int detectSteps = (int) event.values[0];
            currentStepsDetected += detectSteps; //steps = steps + detectSteps; // This variable will be initialised with the STEP_DETECTOR event value (1), and will be incremented by itself (+1) for as long as steps are detected.
        }

        Log.v("Service Counter", String.valueOf(newStepCounter));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private Runnable updateBroadcastData = new Runnable() {
        public void run() {
            if (!serviceStopped) { // Only allow the repeating timer while service is running (once service is stopped the flag state will change and the code inside the conditional statement here will not execute).
                // Call the method that broadcasts the data to the Activity..
                broadcastSensorValue();
                // Call "handler.postDelayed" again, after a specified delay.
                handler.postDelayed(this, 1000);
            }
        }
    };

    private void broadcastSensorValue() {
        Log.d(TAG, "Data to Activity");
        // add step counter to intent.
        intent.putExtra("Counted_Step_Int", newStepCounter);
        intent.putExtra("Counted_Step", String.valueOf(newStepCounter));
        // add step detector to intent.
        intent.putExtra("Detected_Step_Int", currentStepsDetected);
        intent.putExtra("Detected_Step", String.valueOf(currentStepsDetected));
        // call sendBroadcast with that intent  - which sends a message to whoever is registered to receive it.
        sendBroadcast(intent);
    }
    // ___________________________________________________________________________ \\

=======
=======
>>>>>>> b8e8e68394cecbd6d6b165bc2b76576e02d93396
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

public class PedometerService extends Service implements SensorEventListener {


    private SensorManager mSensorManager;
    private Sensor mStepCounterSensor;
    private Sensor mStepDetectorSensor;

    public void onStartCommand() {

        mSensorManager = (SensorManager)
                getSystemService(Context.SENSOR_SERVICE);
        mStepCounterSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

    }

    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        float[] values = event.values;
        int value = -1;

        if (values.length > 0) {
            value = (int) values[0];
        }
        if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            //Send "value" to activity somehow
            Intent intent = new Intent("YourAction");
            Bundle bundle = new Bundle();
            bundle.putString("StepCount", String.valueOf(value));
            intent.putExtras(bundle);
            LocalBroadcastManager.getInstance(PedometerService.this).sendBroadcast(intent);
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
<<<<<<< HEAD
>>>>>>> b8e8e68394cecbd6d6b165bc2b76576e02d93396
=======
>>>>>>> b8e8e68394cecbd6d6b165bc2b76576e02d93396
}
