package com.b143lul.android.logreg;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.b143lul.android.logreg.Login.ID_SHARED_PREF;
import static com.b143lul.android.logreg.Login.LOGGEDIN_SHARED_PREF;
import static com.b143lul.android.logreg.Login.SHARED_PREF_NAME;

public class TrackMap extends AppCompatActivity {
    private int id;
    private final String getGroupParticipantsURL = "http://b143servertesting.gearhostpreview.com/GroupCodes/getGroupParticipants.php";
    private int localGroupCode;
    private JSONObject groupScores;
    CircleView circleView;
    private final int REFRESH_TIME = 5000;
    private String username;

    // From Pedometer class:
    static final String State_Count = "Counter";
    static final String State_Detect = "Detector";
    boolean isServiceStopped;
    private Intent intent;
    private static final String TAG = "SensorEvent";
    /////////////////////////////


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_map);
        circleView = (CircleView)findViewById(R.id.CircleView);

        SharedPreferences sharedPreferences = TrackMap.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "username");
        if(sharedPreferences.getBoolean(LOGGEDIN_SHARED_PREF, false)) {
            id = sharedPreferences.getInt(ID_SHARED_PREF, -1);
        } else {
            AlertDialog.Builder alertbox=new AlertDialog.Builder(TrackMap.this);
            alertbox.setTitle("How tf did u get here???");
            alertbox.setCancelable(false);
            finish();
        }
        localGroupCode = sharedPreferences.getInt("groupcode", 00000);

        // For pedometer, from Pedometer.class
        intent = new Intent(this, PedometerService.class);
        startDaServiceCUH();

        getGroupParticipants();
        startGetScores();
    }

    private void startDaServiceCUH() {
        // start Service.
        startService(new Intent(getBaseContext(), PedometerService.class));
        // register our BroadcastReceiver by passing in an IntentFilter. * identifying the message that is broadcasted by using static string "BROADCAST_ACTION".
        registerReceiver(broadcastReceiver, new IntentFilter(PedometerService.BROADCAST_ACTION));
        isServiceStopped = false;
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
        String countedStep = intent.getStringExtra("Counted_Step");
        String DetectedStep = intent.getStringExtra("Detected_Step");
        Log.d(TAG, String.valueOf(countedStep));
        Log.d(TAG, String.valueOf(DetectedStep));

        //steps.setText('"' + String.valueOf(countedStep) + '"' + " Steps Counted");

    }

    private void getGroupParticipants(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getGroupParticipantsURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // This will return all the scores.
                        drawGroupMembers(response);
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
                prams.put("groupCode", Integer.toString(localGroupCode));
                prams.put("id", Integer.toString(id));
                return prams;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void drawGroupMembers(String response) {
        String responseCheck = response;
        try {
            groupScores = new JSONObject(responseCheck);
            SharedPreferences sharedPreferences = TrackMap.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            circleView.update(groupScores, username);
        } catch (JSONException e) {
            Toast.makeText(TrackMap.this, "An error occurred.  Probably don't have the group code stored in the SharedPrefs.", Toast.LENGTH_SHORT).show();
        }
    }
    Handler handler = new Handler(Looper.getMainLooper());
    public void startGetScores() {
        handler.postDelayed(new Runnable() {
            public void run() {
                getGroupParticipants();          // this method will contain your almost-finished HTTP calls
                handler.postDelayed(this, REFRESH_TIME);
            }
        }, REFRESH_TIME);
    }
}