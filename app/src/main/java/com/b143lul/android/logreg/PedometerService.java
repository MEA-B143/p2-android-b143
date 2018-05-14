package com.b143lul.android.logreg;


import android.app.NotificationChannel;
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
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.b143lul.android.logreg.Login.ID_SHARED_PREF;
import static com.b143lul.android.logreg.Login.LOGGEDIN_SHARED_PREF;
import static com.b143lul.android.logreg.Login.SHARED_PREF_NAME;


public class PedometerService extends Service implements SensorEventListener {

    SensorManager sensorManager;
    Sensor stepCounterSensor;
    Sensor stepDetectorSensor;

    int currentStepsDetected;

    int stepCounter;
    private static int newStepCounter;

    boolean serviceStopped;

    Intent intent;

    private static final String TAG = "StepService";
    public static final String BROADCAST_ACTION = "com.websmithing.b143lul.mybroadcast";

    private String updateURL = "http://b143servertesting.gearhostpreview.com/Update/UpdateStudent.php";
    String scoreText;

    private final android.os.Handler handler = new android.os.Handler() {};
    private final android.os.Handler handler2 = new android.os.Handler() {};

    int id = 0;

    SharedPreferences sharedPreferences;

    String CHANNEL_ID = "randchannellul";

    @Override
    public void onCreate() {
        super.onCreate();

        intent = new Intent(BROADCAST_ACTION);

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("Service", "Start");

        sharedPreferences = PedometerService.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean(LOGGEDIN_SHARED_PREF, false)) {
            id = sharedPreferences.getInt(ID_SHARED_PREF, -1);
        }

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

        handler2.removeCallbacks(updateServerData);
        handler2.post(updateServerData);

        createNotificationChannel();

        final Handler handler3 = new Handler();
        final int delaytime = 10000;
        handler3.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sharedPreferences.getBoolean("finishedrace", false)) {
                    sendRaceCompleteToServer();
                }
                handler3.postDelayed(this, delaytime);
            }
        }, delaytime);

        return START_STICKY;
    }

    private void sendRaceCompleteToServer() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, updateURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Expecting a success message, no data is technically received.
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.has("success")) {
                                // Server error
                                Log.e("PedometerService.java", "Update failed.");
                                Log.e("PedometerService.java", jsonObject.getString("Error"));
                                Log.e("PedometerService.java", jsonObject.getString("groupcode"));
                            } else {
                                // YAY!! Everything worked!
                                createNotificationForRaceWin();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        //Log.e();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> prams = new HashMap<>();
                prams.put("groupcode", Integer.toString(sharedPreferences.getInt("groupcode", 0)));
                return prams;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createNotificationForRaceWin() {
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, WinScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.trac_logo)
                .setContentTitle("You finished the race!")
                .setContentText("You placed 1st in your TRAC challenge!  Click here to see your victory!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(createID(), mBuilder.build());
    }

    private int createID(){
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.FRANCE).format(now));
        return id;
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
        if (sharedPreferences.getInt("score", -1) < 10000) {
            if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                int countSteps = (int) event.values[0];
                if (stepCounter == 0) {
                    stepCounter = (int) event.values[0];
                }
                // NewStepCounter is the steps total for the session, constantly reset from all over the place
                newStepCounter = countSteps - stepCounter;
            }


            if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                int detectSteps = (int) event.values[0];
                currentStepsDetected += detectSteps; //steps = steps + detectSteps; // This variable will be initialised with the STEP_DETECTOR event value (1), and will be incremented by itself (+1) for as long as steps are detected.

            }

            Log.v("Service Counter", String.valueOf(newStepCounter));
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("finishedrace", true);
            editor.commit();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void changeScore(final int scoreChange) {
        // This thing is run every minute at updateServerData()
        StringRequest stringRequest = new StringRequest(Request.Method.POST, updateURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // getString("newScore");
                        int newScore = 0;
                        try {
                            JSONObject jObject = new JSONObject(response);
                            newScore = Integer.parseInt(jObject.getString("newScore").trim());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("score", newScore);
                        editor.commit();
                        newStepCounter = 0;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        //Log.e();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> prams = new HashMap<>();
                prams.put("id", Integer.toString(id));
                prams.put("score", Integer.toString(scoreChange));

                return prams;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private Runnable updateServerData = new Runnable() {
        public void run() {
        if (!serviceStopped) { // Only allow the repeating timer while service is running (once service is stopped the flag state will change and the code inside the conditional statement here will not execute).
            // Call the method that updates score on the server..
            changeScore(newStepCounter);
            // Call "handler.postDelayed" again, after a specified delay (of a minute).
            Log.d(TAG, Integer.toString(newStepCounter));
            handler2.postDelayed(this, 60000);
        }
        }
    };

    private Runnable updateBroadcastData = new Runnable() {
        public void run() {
        if (!serviceStopped) { // Only allow the repeating timer while service is running (once service is stopped the flag state will change and the code inside the conditional statement here will not execute).
            // Call the method that broadcasts the data to the Activity..
            broadcastSensorValue();
            // Call "handler.postDelayed" again, after a specified delay.
            handler.postDelayed(this, 5000);
        }
        }
    };

    private void broadcastSensorValue() {

        //Log.d(TAG, "Data to Activity");
        // add step counter to intent.
        intent.putExtra("Counted_Step_Int", newStepCounter);
        intent.putExtra("Counted_Step", String.valueOf(newStepCounter));
        // add step detector to intent.
        intent.putExtra("Detected_Step_Int", currentStepsDetected);
        intent.putExtra("Detected_Step", String.valueOf(currentStepsDetected));
        // add new score to intent.
        intent.putExtra("New_score", scoreText);
        // call sendBroadcast with that intent  - which sends a message to whoever is registered to receive it.
        sendBroadcast(intent);
    }
    // ___________________________________________________________________________ \\

    public static void setNewStepCounter(int value) {
        newStepCounter = value;
    }
}
