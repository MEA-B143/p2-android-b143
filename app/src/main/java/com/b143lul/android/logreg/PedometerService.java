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
import android.os.CountDownTimer;
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
import java.util.Random;

import static android.os.SystemClock.uptimeMillis;
import static com.b143lul.android.logreg.Login.ID_SHARED_PREF;
import static com.b143lul.android.logreg.Login.LOGGEDIN_SHARED_PREF;
import static com.b143lul.android.logreg.Login.SHARED_PREF_NAME;
import static com.b143lul.android.logreg.Login.USERNAME_SHARED_PREF;
import static java.lang.Math.round;


public class PedometerService extends Service implements SensorEventListener {

    SensorManager sensorManager;
    Sensor stepCounterSensor;
    Sensor stepDetectorSensor;

    private static int currentStepsDetected;

    private static int stepCounter;
    private static int newStepCounter;

    boolean serviceStopped;

    Intent intent;

    private static final String TAG = "StepService";
    public static final String BROADCAST_ACTION = "com.websmithing.b143lul.mybroadcast";

    private String updateURL = "http://b143servertesting.gearhostpreview.com/Update/UpdateStudent.php";
    private final String completedURL = "http://b143servertesting.gearhostpreview.com/Update/EndRace.php";
    private final String updateSecondsURL = "http://b143servertesting.gearhostpreview.com/Update/UpdateExerciseTime.php";
    private final String getGroupParticipantsURL = "http://b143servertesting.gearhostpreview.com/GroupCodes/getGroupParticipants.php";
    private final String receiveURL = "http://b143servertesting.gearhostpreview.com/GetVals/GetField.php";

    String scoreText;

    private final android.os.Handler handler = new android.os.Handler() {};
    private final android.os.Handler handler2 = new android.os.Handler() {};

    final Handler handler3 = new Handler();
    final int delaytime = 5000;

    int id = 0;

    SharedPreferences sharedPreferences;

    private boolean gate;

    private static boolean launchEnd;

    String CHANNEL_ID = "randchannellul";

    Handler timerHandler = new Handler();
    int tempTime = 0;
    boolean timerStarted = false;
    final String SECONDS_OF_EXERCISE_KEY_SHARED_PREF = "secondsofexercise";
    long startStepTime;
    CountDownTimer countDownTimer;

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


        // Updating server data at timed interval
        handler2.removeCallbacks(updateServerData);
        handler2.post(updateServerData);

        // This is the loop for sending the race completion to server
        handler3.removeCallbacks(sendRaceComplete);
        handler3.post(sendRaceComplete);

        gate = false;

        // Needs to be done for creating a notification channel
        createNotificationChannel();
        getUserSeconds();


        return START_STICKY;
    }

    private void getUserSeconds() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, receiveURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        onGetUserSecondsResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG,Log.getStackTraceString(error));
                        // Print any errors to the console.
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> prams = new HashMap<>();
                prams.put("id", Integer.toString(id));
                prams.put("field", "secondsofexercise");

                return prams;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void onGetUserSecondsResponse(String response) {
        int seconds = Integer.parseInt(response.split(",")[0]);
        int currentTime = sharedPreferences.getInt(SECONDS_OF_EXERCISE_KEY_SHARED_PREF, 0);
        if (seconds > currentTime) {
            sharedPreferences.edit().putInt(SECONDS_OF_EXERCISE_KEY_SHARED_PREF, seconds).apply();
        }
    }

    private void sendRaceCompleteToServer() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, completedURL,
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
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("finishedrace", false);
                                editor.putBoolean("showendscreen", true);
                                editor.commit();
                                launchEnd = true;
                            }
                            if (launchEnd) {
                                handler3.removeCallbacks(sendRaceComplete);
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
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.trac_logo)
                .setContentTitle("You finished the race!")
                .setContentText("You placed 1st in your TRAC challenge!  Click here to claim your victory!")
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
        //handler.removeCallbacks(updateBroadcastData);
        //handler2.removeCallbacks(updateServerData);
        //handler3.removeCallbacks(sendRaceComplete);
        serviceStopped = true;

    }

    /** Called when the overall system is running low on memory, and actively running processes should trim their memory usage. */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (sharedPreferences.getInt("score", -1) < 40000) {
            if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                int countSteps = (int) event.values[0];
                if (stepCounter == 0) {
                    stepCounter = (int) event.values[0];
                }
                // NewStepCounter is the steps total for the session, constantly reset from all over the place
                newStepCounter = countSteps - stepCounter;
                Log.i(TAG, "Step counter called.");
            }


            if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                int detectSteps = (int) event.values[0];
                Log.i(TAG, "Step detector called: " + String.valueOf(detectSteps));
                sharedPreferences.edit().putInt("score", sharedPreferences.getInt("score", 0)+detectSteps).commit();
                currentStepsDetected += detectSteps; //steps = steps + detectSteps; // This variable will be initialised with the STEP_DETECTOR event value (1), and will be incremented by itself (+1) for as long as steps are detected.

                if (detectSteps == 1) {
                    if (!timerStarted) {
                        startStepTime = uptimeMillis();
                        timerStarted = true;
                        //stepTimer();
                    }
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }

                    // Countdown timer for the steptimer to count the amount of time used on exercise but only after 10 seconds of inactivity
                    countDownTimer = new CountDownTimer(10000, 1000) {

                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            // After finished
                            tempTime = (int) Math.floor((uptimeMillis() - startStepTime) / 1000);
                            sharedPreferences.edit().putInt(SECONDS_OF_EXERCISE_KEY_SHARED_PREF, tempTime - 5).commit();
                            updateUserSeconds();
                            Log.i(TAG, "tempTime = " + String.valueOf(tempTime));
                            timerStarted = false;
                            if (tempTime > 60) {
                                // If you walk for more than 5 minutes
                                makeTrackingStepsNotification();
                                //Random rand = new Random();
                                //int  n = rand.nextInt(4) + 1;

                                // This notification,
                                // if internet connection is there and you are either first or there is 500 steps to the next person,
                                // will currently overwite the basic one in the above function :)
                                checkDistanceToNextPlayer();
                            }
                            tempTime = 0;
                        }

                    }.start();

                    /*  Maybe could have a countdown timer that keeps being called and notification/time spent walking will be saved at finish
                    new CountDownTimer(30000, 1000) {

                        public void onTick(long millisUntilFinished) {

                        }

                        public void onFinish() {
                            // After finished
                        }

                    }.start();
                    */
                }
            }

            Log.v("Service Counter", String.valueOf(newStepCounter));
        } else {
            if (!sharedPreferences.getBoolean("finishedrace", false)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("finishedrace", true);
                editor.commit();
                this.stopSelf();
            }
        }
    }



    /*

    ALL Step Timer Stuff

     */
    ////////////////////////////////////////////////////////////////////////////
    private void updateUserSeconds() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, updateSecondsURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        int newSeconds = 0;
                        try {
                            JSONObject jObject = new JSONObject(response);
                            if (jObject.has(SECONDS_OF_EXERCISE_KEY_SHARED_PREF)) {
                                newSeconds = Integer.parseInt(jObject.getString(SECONDS_OF_EXERCISE_KEY_SHARED_PREF).trim());
                            } else {
                                String errormsg = jObject.getString("Error");
                                Log.e(TAG, errormsg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        /*
                        Maybe add a sharedprefs updater idk
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("score", newScore);
                        editor.commit();
                        */
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
                prams.put("seconds", Integer.toString(sharedPreferences.getInt(SECONDS_OF_EXERCISE_KEY_SHARED_PREF, 0)));

                return prams;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void makeTrackingStepsNotification() {
        Random rand = new Random();
        int  n = rand.nextInt(4) + 1;
        Log.i(TAG, "n is: " + String.valueOf(n));
        String title;
        switch (n) {
            case 1:
                title = "Good stuff!";
                launchTrackingStepsNotification(title);
                break;
            case 2:
                title = "Nice!";
                launchTrackingStepsNotification(title);
                break;
            case 3:
                title = "Wow!";
                launchTrackingStepsNotification(title);
                break;
            case 4:
                title = "You've been going ham!";
                launchTrackingStepsNotification(title);
                break;
            default:
                title = "Nice!";
                launchTrackingStepsNotification(title);
                break;
        }
    }

    private void launchTrackingStepsNotification(String title) {
        Intent intent = new Intent(this, Splash_Screen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.trac_logo)
                .setContentTitle(title)
                .setContentText("We recorded " + String.valueOf(round(tempTime/60)) + " minutes of exercise.  Keep going!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(createID(), mBuilder.build());
    }

    private void checkDistanceToNextPlayer() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getGroupParticipantsURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // This will return all the scores.
                        onGroupMembersResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> prams = new HashMap<>();
                prams.put("groupCode", Integer.toString(sharedPreferences.getInt("groupcode", 0)));
                prams.put("id", Integer.toString(id));
                return prams;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void onGroupMembersResponse(String response) {
        String responseCheck = response;
        try {
            JSONObject groupScores = new JSONObject(responseCheck);
            String username = sharedPreferences.getString(USERNAME_SHARED_PREF, "null");
            //int placement = getPlacing(groupScores, username);
            String nextPlayer = getDistanceToNextPlayer(groupScores, username);
            if (nextPlayer.equals("first")) {
                ohDamnNearbyNotification("You're in 1st place!", "You're winning the race!  Keep it up!");
            } else {
                String[] splitNextOpponentInfo = nextPlayer.trim().split(",");
                String theirName = splitNextOpponentInfo[0];
                int stepsDifference = Integer.parseInt(splitNextOpponentInfo[1].trim());
                if (stepsDifference < 1500) {
                    String title = "Overtake your friend!";
                    String message = "You're only " + String.valueOf(stepsDifference) + " steps away from reaching " + theirName.toUpperCase() + "!  Try overtake 'em!";
                    ohDamnNearbyNotification(title, message);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void ohDamnNearbyNotification(String title, String message) {
        // Might switch over to launchTrackingStepsNotification(String title) idk... I thought that this made more sense and the app is like 3MB so it's not like we're tryna save space
        Intent intent = new Intent(this, Splash_Screen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.trac_logo)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(createID(), mBuilder.build());
    }

    private String getDistanceToNextPlayer(JSONObject groupScores, String username) {
        String response = "";
        for (int i = 0; i < groupScores.names().length(); i++) {
            try {
                if (!groupScores.names().getString(i).isEmpty()) {
                    int userscore = 0;
                    String name = groupScores.names().getString(i);
                    userscore = Integer.parseInt(groupScores.getString(name));
                    if (name.equals(username)) {
                        if (Integer.parseInt(groupScores.getString(username)) < 40000) {
                            if (i == 0) {
                                response = "first";
                            } else {
                                String opponentAhead = groupScores.names().getString(i - 1);
                                int opponentScore = groupScores.getInt(opponentAhead);
                                int yourScore = groupScores.getInt(username);
                                response = opponentAhead + "," + String.valueOf(opponentScore - yourScore);
                            }
                        }
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    private int getPlacing(JSONObject groupScores, String username) {
        int placing = 0;
        for (int i = 0; i < groupScores.names().length(); i++) {
            try {
                if (!groupScores.names().getString(i).isEmpty()) {
                    String name = groupScores.names().getString(i);
                    if (name.equals(username)) {
                        placing = i + 1;
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return placing;
    }
    ////////////////////////////////////////////////////////////////////////////






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
                        currentStepsDetected = 0;
                        stepCounter = 0;
                        newStepCounter = 0;

                        if (launchEnd) {
                            handler2.removeCallbacks(updateServerData);
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
                updateUserSeconds();
                // Call "handler.postDelayed" again, after a specified delay (of a minute).
                Log.d(TAG, Integer.toString(newStepCounter));
                handler2.postDelayed(this, 10000);
            }
        }
    };

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

    private Runnable sendRaceComplete = new Runnable() {
        @Override
        public void run() {
            if (sharedPreferences.getBoolean("finishedrace", false)) {
                sendRaceCompleteToServer();
            }
            handler3.postDelayed(this, delaytime);
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

        //intent.putExtra("LaunchEnd", launchEnd);
        // call sendBroadcast with that intent  - which sends a message to whoever is registered to receive it.
        sendBroadcast(intent);
        if (launchEnd) {
            handler.removeCallbacks(updateBroadcastData);
        }
    }
    // ___________________________________________________________________________ \\

    public static void setNewStepCounter(int value) {
        newStepCounter = value;
        currentStepsDetected = 0;
        stepCounter = 0;
    }

    public static void setLaunchEnd(boolean value) {
        launchEnd = value;
    }
}