package com.b143lul.android.logreg;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

        MessageReceiver receiver = new MessageReceiver(new Message());

        Intent intent = new Intent(this, PedometerService.class);
        intent.putExtra("receiver",receiver);
        startService(intent);
    }

    public class Message {
        public void displayMessage(int resultCode, Bundle resultData) {

        }
    }
}
