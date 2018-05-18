package com.b143lul.android.logreg;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
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
    private final String forfeitURL = "http://b143servertesting.gearhostpreview.com/GroupCodes/forfeit.php";
    private final String completedURL = "http://b143servertesting.gearhostpreview.com/Update/EndRace.php";
    private final String updateURL = "http://b143servertesting.gearhostpreview.com/Update/UpdateStudent.php";
    private final String checkGroupCompletion = "http://b143servertesting.gearhostpreview.com/GetVals/CheckGroupCompletion.php";
    private final String JoinGroupURL = "http://b143servertesting.gearhostpreview.com/GroupCodes/JoinGroup.php";
    private final String receiveURL = "http://b143servertesting.gearhostpreview.com/GetVals/GetField.php";
    private final boolean shouldAllowBack = false;
    private int localGroupCode;
    private JSONObject groupScores;
    CircleView circleView;
    private final int REFRESH_TIME = 5000;
    private String username;
    private SharedPreferences sharedPreferences;
    private String name;
    Button BtnForfeit;
    ImageButton BtnMenu;
    TextView currentScore;
    TextView challengeName;
    TextView groupcode;
    TextView placementText;

    private String checkGroupname;
    private String GroupName;


    // From Pedometer class:
    static final String State_Count = "Counter";
    static final String State_Detect = "Detector";
    boolean isServiceStopped;
    private Intent intent;
    private static final String TAG = "SensorEvent";
    private TextView steps;
    /////////////////////////////


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // Activity was brought to front and not created,
            // Thus finishing this will get us to the last viewed activity
            finish();
            return;
        }
        setContentView(R.layout.activity_track_map);

        BtnForfeit = (Button)findViewById(R.id.btn_forfeit);
        BtnForfeit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptForfeit();
            }
        });
        sharedPreferences = TrackMap.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        checkGroupname = sharedPreferences.getString("groupname","Name");
        GroupName = checkGroupname.substring(0, checkGroupname.length()-2);
        circleView = (CircleView)findViewById(R.id.CircleView);
        steps = (TextView)findViewById(R.id.textView3);
        username = sharedPreferences.getString("username", "username");
        int stepScore = sharedPreferences.getInt("score", -1);
        currentScore = (TextView)findViewById(R.id.currentScore);
        currentScore.setText(String.valueOf(stepScore));
        challengeName = (TextView) findViewById(R.id.challengeName);
        challengeName.setText(GroupName);
        placementText = (TextView)findViewById(R.id.placement);


        BtnMenu = (ImageButton) findViewById(R.id.btn_menu1);
        BtnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String className = "TrackMap";
                Intent IntentMenu = new Intent(TrackMap.this, Menu.class);
                IntentMenu.putExtra("className", className);
                IntentMenu.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(IntentMenu);
            }
        });


        if(sharedPreferences.getBoolean(LOGGEDIN_SHARED_PREF, false)) {
            id = sharedPreferences.getInt(ID_SHARED_PREF, -1);
        } else {
            AlertDialog.Builder alertbox=new AlertDialog.Builder(TrackMap.this);
            alertbox.setTitle("How tf did u get here???");
            alertbox.setCancelable(false);
            finish();
        }

        getServerGroupCode();
        groupcode = (TextView) findViewById(R.id.groupcode);
        localGroupCode = sharedPreferences.getInt("groupcode", 00000);
        groupcode.setText("Group Code: " + String.valueOf(localGroupCode));


        // For pedometer, from Pedometer.class
        intent = new Intent(this, PedometerService.class);
        checkGroupCompleted();

        checkEndReachedLoop();

        getGroupParticipants();
        startGetScores();

    }

    private void getServerGroupCode() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, receiveURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        onGroupCodeResponse(response);
                        String txtGroupCode = String.valueOf(localGroupCode);
                        getGroupInfo(txtGroupCode);
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
                prams.put("field", "groupcode");

                return prams;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void onGroupCodeResponse(String response) {
        String code = response.split(",")[0];
        if (code.equals("none")) {
            // Not in a group.
            Intent groupJoinScreen = new Intent(getApplicationContext(), CreateJoinClass.class);
            groupJoinScreen.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(groupJoinScreen);
            finish();
        } else {
            Log.e(TAG, code);
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            int debug = Integer.parseInt(code.trim());
            editor.putInt("groupcode", Integer.parseInt(code.trim()));
            editor.commit();
        }
    }

    private void promptForfeit() {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        alertbox.setTitle("Are you sure you want to forfeit?");
        alertbox.setCancelable(true);
        alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                forfeit();
            }
        });
        alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertbox.show();
    }

    private void checkGroupCompleted() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, checkGroupCompletion,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("completed")) {
                                // Success
                                int getval = Integer.parseInt(jsonObject.getString("completed"));
                                if (getval == 0) {
                                    startDaServiceCUH();
                                } else if (getval == 1) {
                                    stopService(new Intent(TrackMap.this, PedometerService.class));
                                    handler.removeCallbacksAndMessages(null);
                                    Intent launchEnd = new Intent(getApplicationContext(), WinScreen.class);
                                    launchEnd.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    launchEnd.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                    startActivity(launchEnd);
                                    finish();
                                }
                            } else {
                                Log.e(TAG, "Error: " + jsonObject.getString("Error"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            startDaServiceCUH();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        //Log.e();
                        startDaServiceCUH();
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

    private void checkEndReachedLoop() {
        final Handler handler3 = new Handler();
        final int delaytime = 1000;
        handler3.postDelayed(new Runnable() {
            @Override
            public void run() {
                int localScore = sharedPreferences.getInt("score", -1);
                if (localScore >= 40000) {
                    launchEndScreen();
                    Log.d(TAG, "Launched from checkEndReachedLoop");
                    handler3.removeCallbacks(this);
                } else {
                    handler3.postDelayed(this, delaytime);
                }
            }
        }, delaytime);
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
        int countedSteps = intent.getIntExtra("Counted_Step_Int", -1);
        if (countedSteps > 0) {
            // We got sum shit!!!
            changeScore(countedSteps);
            int localScore = sharedPreferences.getInt("score", -1);
            sharedPreferences.edit().putInt("score", localScore+countedSteps).apply();

            //steps.setText(String.valueOf(localScore+countedSteps));
        } else if (countedSteps == 0) {
            // We got somethin but it aint somethin

        } else {
            // It's minus 1 and we don't want no shit!!! :rage:
        }
        //Log.d(TAG, String.valueOf(countedStep));

        if (intent.getBooleanExtra("Launch_End", false)) {
            launchEndScreen();
            Log.d(TAG, "from updateViews");
        }
    }

    private void launchEndScreen() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, completedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        onUpdateCompleted(response);
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

    private void onUpdateCompleted(String response) {
        String responseCheck = response;
        // Expecting a success message, no data is technically received.
        try {
            JSONObject jsonObject = new JSONObject(responseCheck);
            if (!jsonObject.has("success")) {
                // Server error
                Log.e("TrackMap.java", "Update failed.");
                Log.e("TrackMap.java", jsonObject.getString("Error"));
                Log.e("TrackMap.java", jsonObject.getString("groupcode"));
            } else {
                // YAY!! Everything worked!
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("finishedrace", false);
                editor.putBoolean("showendscreen", true);
                editor.commit();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Stop the service in background on win
        PedometerService.setLaunchEnd(false);
        stopService(new Intent(getBaseContext(), PedometerService.class));
        isServiceStopped = true;
        Intent launchWinScreen = new Intent(TrackMap.this, WinScreen.class);
        launchWinScreen.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        launchWinScreen.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(launchWinScreen);
        finish();
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

    private void forfeit() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, forfeitURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // If successful don't expect response
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("groupcode");
                        editor.remove("groupname");
                        editor.commit();
                        ////////////////////////////////////////////////////////////////////////////////////////
                        handler.removeCallbacksAndMessages(null);
                        Intent IntentForfeit = new Intent(TrackMap.this, CreateJoinClass.class);
                        IntentForfeit.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        IntentForfeit.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                        IntentForfeit.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(IntentForfeit);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Stable connection could not be made", Toast.LENGTH_LONG);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> prams = new HashMap<>();
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
            circleView.update(groupScores, username);
            int placement = getPlacing(groupScores, username);
            String placeDaText = "# " + String.valueOf(placement);
            placementText.setText(placeDaText);
            sharedPreferences.edit().putInt("placement", placement).commit();
            int score = extractScoreWithEveryoneElse(groupScores, username);
            sharedPreferences.edit().putInt("score", score).commit();
            int localScore = sharedPreferences.getInt("score", -1);
            currentScore.setText(String.valueOf(localScore));
        } catch (JSONException e) {
            Toast.makeText(TrackMap.this, "An error occurred.  Probably don't have the group code stored in the SharedPrefs.", Toast.LENGTH_SHORT).show();
        }
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

    private int extractScoreWithEveryoneElse(JSONObject groupScores, String username) {
        int score = 0;
        for (int i = 0; i < groupScores.names().length(); i++) {
            try {
                if (!groupScores.names().getString(i).isEmpty()) {
                    String name = groupScores.names().getString(i);
                    if (name.equals(username)) {
                        score = groupScores.getInt(name);
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return score;
    }

    Handler handler = new Handler(Looper.getMainLooper());
    private void startGetScores() {
        handler.postDelayed(new Runnable() {
            public void run() {
                getGroupParticipants();
                handler.postDelayed(this, REFRESH_TIME);
            }
        }, REFRESH_TIME);
    }

    private void changeScore(final int scoreChange) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, updateURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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
                        PedometerService.setNewStepCounter(0);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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

    private void getGroupInfo(final String txtGroupCode) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, JoinGroupURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("NoGroupCode")) { //If the entered group code doesn't exist.
                                String ErrorMessage = "Group code doesn't exist.";
                                Toast.makeText(TrackMap.this, ErrorMessage, Toast.LENGTH_SHORT).show();
                            } else if (jsonObject.has("groupcode")) { //If everything is successful and the group information is retrieved.
                                String groupDetailsString = jsonObject.getString("groupcode");
                                String[] groupDetails = groupDetailsString.split(",");
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt("groupcode", Integer.parseInt(groupDetails[0].trim()));
                                editor.putString("groupname", (response.split(",")[4]));
                                editor.commit();
                            }
                            checkGroupname = sharedPreferences.getString("groupname","Name");
                            GroupName = checkGroupname.substring(0, checkGroupname.length()-2);
                            challengeName.setText(GroupName); //If the user logged out on TrackMap, this is where they get the GroupName

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> prams = new HashMap<>();
                prams.put("groupCode", txtGroupCode);
                prams.put("id", Integer.toString(id));
                return prams;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void onResume() {
        super.onResume();
        checkGroupCompleted();

        checkEndReachedLoop();

        getGroupParticipants();
        startGetScores();
        getServerGroupCode();
        registerReceiver(broadcastReceiver, new IntentFilter(PedometerService.BROADCAST_ACTION));
    }

    @Override
    public void onBackPressed() {
        if (!shouldAllowBack) {
            // Yikes...
        } else {
            super.onBackPressed();
        }
    }
}