package com.b143lul.android.logreg;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
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
import java.util.Iterator;
import java.util.Map;

import static com.b143lul.android.logreg.Login.ID_SHARED_PREF;
import static com.b143lul.android.logreg.Login.LOGGEDIN_SHARED_PREF;
import static com.b143lul.android.logreg.Login.SHARED_PREF_NAME;

public class TrackMap extends AppCompatActivity {
    private int id;
    private final String getGroupParticipantsURL = "http://b143servertesting.gearhostpreview.com/GroupCodes/getGroupParticipants.php";
    private int localGroupCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_map);

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
                return prams;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void drawGroupMembers(String response) {
        String responseCheck = response;
        try {
            JSONObject jsonObject = new JSONObject(response);
            for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                String name = it.next();
                int userscore = Integer.parseInt(jsonObject.getString(name));

            }
        } catch (JSONException e) {
            Toast.makeText(TrackMap.this, "An error occurred.  Account probably already had a group code.", Toast.LENGTH_SHORT).show();
        }
    }
    private float posX(int points) {
        int maxPoints = 5000;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        if (points < maxPoints/5) {                                    //These If-statements divide the maxPoints into 5, as there are 5 sections of the track. This one is the first piece (horizontal).
            return (points/(maxPoints/5))*((width/5)*3);                  //Dividing the points with the amount of points needed for the piece an then multiplying it by where it needs to "fit", makes it fit there. Its multiplied by width/5*3 because there is width/5 on each side of the track.
        } else if (points >= maxPoints/5 && points < (maxPoints/5)*2) {   //This is the first vertical piece.
            return (width/5)*3;                                              //This ensures that the first vertical piece will be width/5 from the right side of the screen.
        } else if (points >= (maxPoints/5)*2 && points < (maxPoints/5)*3) {       //Second horizontal piece.
            return (width/5*3)-((points-(maxPoints/5)*2)/(maxPoints/5))*((width/5)*3);   //This is basically the same as the first piece, but it's modified to go backwards, so at points increase the value gets smaller and smaller.
        } else if (points >= (maxPoints/5)*3 && points < (maxPoints/5)*4) {    //Second vertical piece.
            return 0;                                                        //This is a 0, not really much to say; it's basically because the second vertical piece for some reason aligned itself with the rest. Magic..?
        } else {                                                         //And finally "the rest" which is the last piece (horizontal).
            return ((points-(maxPoints/5)*4)/(maxPoints/5))*((width/5)*3); //This is just the same as the first horizontal piece, except some of it is negated so it doesn't go off screen.
        }
    }

    private float posY(int points) {                                                    //The same applies to the if-statements here, as above.
        int maxPoints = 5000;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;

        if (points < maxPoints/5) {
            return height/5;                                    //This ensures that the first horizontal piece is height/5 from the top.
        } else if (points >= maxPoints/5 && points < (maxPoints/5)*2) {
            return ((points/(maxPoints/5))*((height/5)*3)/2)-height/10; //And this makes the first vertical piece go for height/5*3/2, because we have height/5 on the top and bottom, so theres height/5*3 left in the middle, but we have 2 vertical pieces, so its height/5*3/2.
        } else if (points >= (maxPoints/5)*2 && points < (maxPoints/5)*3) {
            return height/2;                                       //Again, this is to make sure the second horizontal piece is in the right plac, which is in the middle.
        } else if (points >= (maxPoints/5)*3 && points < (maxPoints/5)*4) {
            return ((points/(maxPoints/5))*((height/5)*3)/2)-height/5*2;         //This second vertical piece is basically the same as the first, except for the alignment, so it's further down.
        } else {
            return height/5*4;      //And finally this makes the last horizontal piece appear height/5 from the bottom.
        }
    }
}
