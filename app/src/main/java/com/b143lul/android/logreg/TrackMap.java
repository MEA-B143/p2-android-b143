package com.b143lul.android.logreg;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
    Paint paint;
    CircleView circleView;
    private final int REFRESH_TIME = 5000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        circleView = new CircleView(this);
        setContentView(circleView);
        //final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
        SharedPreferences sharedPreferences = TrackMap.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean(LOGGEDIN_SHARED_PREF, false)) {
            id = sharedPreferences.getInt(ID_SHARED_PREF, -1);
        } else {
            AlertDialog.Builder alertbox=new AlertDialog.Builder(TrackMap.this);
            alertbox.setTitle("How tf did u get here???");
            alertbox.setCancelable(false);
            finish();
        }
        localGroupCode = sharedPreferences.getInt("groupcode", 00000);
        getGroupParticipants();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scheduleGetScores();
    }
    /*
    Handler handler = new Handler(Looper.getMainLooper());
    Runnable PaintScoresEvery15Seconds = new Runnable() {
        @Override
        public void run() {
            getGroupParticipants();
            handler.postDelayed(this, 5000);
        }
    };
    */

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
                return prams;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void drawGroupMembers(String response) {
        String responseCheck = response;
        try {
            groupScores = new JSONObject(response);
            circleView.update(groupScores);
        } catch (JSONException e) {
            Toast.makeText(TrackMap.this, "An error occurred.  No users probably have the group code stored in the SharedPrefs.", Toast.LENGTH_SHORT).show();
        }
    }
    Handler handler = new Handler(Looper.getMainLooper());
    public void scheduleGetScores() {
        handler.postDelayed(new Runnable() {
            public void run() {
                getGroupParticipants();          // this method will contain your almost-finished HTTP calls
                handler.postDelayed(this, REFRESH_TIME);
            }
        }, REFRESH_TIME);
    }
}