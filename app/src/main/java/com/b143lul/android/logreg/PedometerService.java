package com.b143lul.android.logreg;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

public class PedometerService extends IntentService {

    public PedometerService() {
        super("Pedometer Service");
    }

    private SensorManager mSensorManager;
    private Sensor mStepCounterSensor;
    private Sensor mStepDetectorSensor;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate();
        Log.v("Pedometer", "Pedometer service has started.");

        mSensorManager = (SensorManager)
                getSystemService(Context.SENSOR_SERVICE);
        mStepCounterSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetectorSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
    }

    @Override
    public void onHandleIntent(Intent intent){
        ResultReceiver receiver = intent.getParcelableExtra("receiver");
    }

    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        float[] values = event.values;
        int value = -1;

        if (values.length > 0) {
            value = (int) values[0];
        }
        if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            Bundle bundle = new Bundle();
            bundle.putString("message","Steps counted");
            receiver.send(value, bundle);
        }

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
