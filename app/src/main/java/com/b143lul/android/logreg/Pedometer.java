package com.b143lul.android.logreg;

import android.content.Context;
<<<<<<< HEAD
=======
import android.content.Intent;
>>>>>>> b8e8e68394cecbd6d6b165bc2b76576e02d93396
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

<<<<<<< HEAD
public class Pedometer extends AppCompatActivity implements SensorEventListener {
    private TextView tvSteps;
    private SensorManager mSensorManager;
    private Sensor mStepCounterSensor;
    private Sensor mStepDetectorSensor;

=======
public class Pedometer extends AppCompatActivity {
    private TextView tvSteps;

    @Override
>>>>>>> b8e8e68394cecbd6d6b165bc2b76576e02d93396
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer);
        tvSteps = (TextView) findViewById(R.id.tvSteps);

<<<<<<< HEAD
        mSensorManager = (SensorManager)
                getSystemService(Context.SENSOR_SERVICE);
        mStepCounterSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetectorSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
    }
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        float[] values = event.values;
        int value = -1;

        if (values.length > 0) {
            value = (int) values[0];
        }

        if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            tvSteps.setText("Step Counter Detected : " + value);
        } else if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            // For test only. Only allowed value is 1.0 i.e. for step taken
            tvSteps.setText("Step Detector Detected : " + value);
        }
    }
    protected void onResume() {

        super.onResume();

        mSensorManager.registerListener(this, mStepCounterSensor,

                SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mStepDetectorSensor,

                SensorManager.SENSOR_DELAY_FASTEST);

    }

    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this, mStepCounterSensor);
        mSensorManager.unregisterListener(this, mStepDetectorSensor);
    }
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

=======
        MessageReceiver receiver = new MessageReceiver(new Message());

        Intent intent = new Intent(this, PedometerService.class);
        intent.putExtra("receiver",receiver);
        startService(intent);
    }

    public class Message {
        public void displayMessage(int resultCode, Bundle resultData) {

        }
>>>>>>> b8e8e68394cecbd6d6b165bc2b76576e02d93396
    }
}
