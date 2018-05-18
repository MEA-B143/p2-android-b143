package com.b143lul.android.logreg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
    ImageButton btn_back;
    ImageButton X;
    int FLAG_ACTIVITY_CLEAR_TOP;
    private JSONObject yourScore;
    private int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // Activity was brought to front and not created,
            // Thus finishing this will get us to the last viewed activity
            finish();
            return;
        }
        setContentView(R.layout.activity_my_profile);
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        usernameName = (TextView) findViewById(R.id.usernameName);
        stepAmount = (TextView) findViewById(R.id.amountOfSteps);
        gamesAmount = (TextView) findViewById(R.id.amountOfGames);
        X = (ImageButton) findViewById(R.id.x);
        final SharedPreferences sharedPreferences = MyProfile.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        username_Name = sharedPreferences.getString("username", "username");
        usernameName.setText(username_Name);
        gamesAmount.setText("  " + 0);
        //Bundle bundle=getIntent().getExtras();
        Intent intent = getIntent();
        final String nameOfClass = intent.getExtras().getString("nameOfClass");

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        if(sharedPreferences.getBoolean(LOGGEDIN_SHARED_PREF, false)) {
            id = sharedPreferences.getInt(ID_SHARED_PREF, -1);
        } else {
            AlertDialog.Builder alertbox=new AlertDialog.Builder(MyProfile.this);
            alertbox.setTitle("How tf did u get here???");
            alertbox.setCancelable(false);
            finish();
        }
        X.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profIntent;

                if (nameOfClass.equals("CreateJoinClass")) {
                    profIntent = new Intent(MyProfile.this, CreateJoinClass.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    profIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(profIntent);
                } else if (nameOfClass.equals("MapSelection")) {
                    profIntent = new Intent(MyProfile.this, MapSelection.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    profIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(profIntent);
                } else if(nameOfClass.equals("ChallengeDetails")){
                    profIntent = new Intent(MyProfile.this, ChallengeDetails.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    profIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(profIntent);
                } else if(nameOfClass.equals("JoinGroup")){
                    profIntent = new Intent(MyProfile.this, JoinGroup.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    profIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(profIntent);
                } else if(nameOfClass.equals("TrackMap")){
                    profIntent = new Intent(MyProfile.this, TrackMap.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    profIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(profIntent);
                } else if(nameOfClass.equals("createdGroupCode")){
                    profIntent = new Intent(MyProfile.this, createdGroupCode.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    profIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(profIntent);
                }

            }
        });

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
