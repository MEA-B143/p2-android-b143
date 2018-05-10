package com.b143lul.android.logreg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import static com.b143lul.android.logreg.Login.ID_SHARED_PREF;
import static com.b143lul.android.logreg.Login.LOGGEDIN_SHARED_PREF;
import static com.b143lul.android.logreg.Login.SHARED_PREF_NAME;

public class ChallengeDetails extends AppCompatActivity {
    private final String createGroupURL = "http://b143servertesting.gearhostpreview.com/GroupCodes/CreateGroup.php";
    private int id;
    SharedPreferences sharedPreferences;

    EditText et_Name;
    EditText et_PlayerLimit;
    EditText et_DayLimit;
    ImageButton BtnBack;
    ImageButton btn_menu;
    FloatingActionButton btn_create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        et_Name = (EditText) findViewById(R.id.etName);
        et_PlayerLimit = (EditText) findViewById(R.id.etPlayerLimit);
        et_DayLimit = (EditText) findViewById(R.id.etDayLimit);
        btn_create = (FloatingActionButton) findViewById(R.id.btnCreate);
        BtnBack = (ImageButton)findViewById(R.id.btn_back);

        BtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String challengeName = et_Name.getText().toString().trim();
                String playerLimit = et_PlayerLimit.getText().toString().trim();
                String dayLimit = et_DayLimit.getText().toString().trim();

                if (challengeName.equals("") || playerLimit.equals("") || dayLimit.equals("")) {
                    Toast.makeText(ChallengeDetails.this, "You have to put something in every field.", Toast.LENGTH_SHORT).show();
                } else if (playerLimit.equals(0) || dayLimit.equals(0)) {
                    Toast.makeText(ChallengeDetails.this, "Player limit and day limit can't be 0.", Toast.LENGTH_SHORT).show();
                } else if (challengeName.length() > 15) {
                    Toast.makeText(ChallengeDetails.this, "Maximum name length is 15.", Toast.LENGTH_SHORT).show();
                } else if (playerLimit.length() > 1) {
                    Toast.makeText(ChallengeDetails.this, "Maximum player limit is 9.", Toast.LENGTH_SHORT).show();
                } else if (et_DayLimit.length() > 1) {
                    Toast.makeText(ChallengeDetails.this, "Maximum day limit is 9.", Toast.LENGTH_SHORT).show();
                } else {
                    sendGroupInfo(challengeName, playerLimit, dayLimit);
                    Toast.makeText(ChallengeDetails.this, "Success", Toast.LENGTH_SHORT).show();
                }

            }
        });

        sharedPreferences = ChallengeDetails.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean(LOGGEDIN_SHARED_PREF, false)) {
            id = sharedPreferences.getInt(ID_SHARED_PREF, -1);
        } else {
            AlertDialog.Builder alertbox = new AlertDialog.Builder(ChallengeDetails.this);
            alertbox.setTitle("How tf did u get here???");
            alertbox.setCancelable(false);
            finish();
        }
    }
    void sendGroupInfo(final String challengeGroup, final String playerLimit, final String dayLimit) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, createGroupURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.startsWith("success")) {
                                Toast.makeText(ChallengeDetails.this, response, Toast.LENGTH_SHORT).show();
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                // The response will be something like "success,12345"
                                editor.putInt("groupcode", Integer.parseInt(response.split(",")[1]));
                                editor.commit();
                                Intent IntentGroupCode = new Intent(ChallengeDetails.this, createdGroupCode.class);
                                startActivity(IntentGroupCode);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Log.e("ChallengeDetails.java", error.toString());
                            error.printStackTrace();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> prams = new HashMap<>();
                    prams.put("id", Integer.toString(id));
                    prams.put("name", challengeGroup);
                    prams.put("daylimit", dayLimit);
                    prams.put("playerlimit", playerLimit);
                    return prams;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
}