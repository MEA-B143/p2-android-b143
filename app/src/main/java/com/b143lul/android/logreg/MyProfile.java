package com.b143lul.android.logreg;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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

public class MyProfile extends AppCompatActivity {
    private final String receiveURL = "http://b143servertesting.gearhostpreview.com/GetVals/GetField.php";
    String username_Name;
    TextView usernameName;
    TextView stepAmount;
    TextView gamesAmount;
    private JSONObject yourScore;
    private int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        usernameName = (TextView) findViewById(R.id.usernameName);
        stepAmount = (TextView) findViewById(R.id.amountOfSteps);
        gamesAmount = (TextView) findViewById(R.id.amountOfGames);
        SharedPreferences sharedPreferences = MyProfile.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        username_Name = sharedPreferences.getString("username", "username");
        usernameName.setText(username_Name);
        gamesAmount.setText("  " + 0);

        if(sharedPreferences.getBoolean(LOGGEDIN_SHARED_PREF, false)) {
            id = sharedPreferences.getInt(ID_SHARED_PREF, -1);
        } else {
            AlertDialog.Builder alertbox=new AlertDialog.Builder(MyProfile.this);
            alertbox.setTitle("How tf did u get here???");
            alertbox.setCancelable(false);
            finish();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, receiveURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        onGetResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> prams = new HashMap<>();
                prams.put("id", Integer.toString(id));
                prams.put("field", "score");

                return prams;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }
    private void onGetResponse(String Response){
        String scoreResponse = Response;
        String score = (scoreResponse.split(",")[0]);
        stepAmount.setText("  " + score);
    }
}
