package com.b143lul.android.logreg;

<<<<<<< HEAD
<<<<<<< HEAD
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
=======
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
>>>>>>> b8e8e68394cecbd6d6b165bc2b76576e02d93396

public class Pedometer extends AppCompatActivity {
    private TextView tvSteps;

<<<<<<< HEAD
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

=======
=======
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

>>>>>>> b8e8e68394cecbd6d6b165bc2b76576e02d93396
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
<<<<<<< HEAD
>>>>>>> b8e8e68394cecbd6d6b165bc2b76576e02d93396
=======
>>>>>>> b8e8e68394cecbd6d6b165bc2b76576e02d93396
    }
}


