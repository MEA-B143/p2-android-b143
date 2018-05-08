package com.b143lul.android.logreg;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

public class Pedometer extends AppCompatActivity {

    TextView steps;


    Button startgame;


    String countedStep;
    String DetectedStep;

    static final String State_Count = "Counter";
    static final String State_Detect = "Detector";

    boolean isServiceStopped;

    private Intent intent;
    private static final String TAG = "SensorEvent";

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer);

        intent = new Intent(this, PedometerService.class);
        init();

    }

    public void init() {


        startgame = (Button)findViewById(R.id.startgame);
        startgame.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // start Service.
                startService(new Intent(getBaseContext(), PedometerService.class));
                // register our BroadcastReceiver by passing in an IntentFilter. * identifying the message that is broadcasted by using static string "BROADCAST_ACTION".
                registerReceiver(broadcastReceiver, new IntentFilter(PedometerService.BROADCAST_ACTION));
                isServiceStopped = false;
            }
        });

        steps = (TextView)findViewById(R.id.steps);

    }
    protected void onPause() {
        super.onPause();
    }

    protected void onResume() {
        super.onResume();

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // call updateUI passing in our intent which is holding the data to display.
            updateViews(intent);
        }
    };
    private void updateViews(Intent intent) {
        // retrieve data out of the intent.
        countedStep = intent.getStringExtra("Counted_Step");
        DetectedStep = intent.getStringExtra("Detected_Step");
        Log.d(TAG, String.valueOf(countedStep));
        Log.d(TAG, String.valueOf(DetectedStep));

        steps.setText('"' + String.valueOf(countedStep) + '"' + " Steps Counted");

    }
}


