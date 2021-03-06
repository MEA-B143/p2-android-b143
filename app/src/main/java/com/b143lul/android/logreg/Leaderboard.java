package com.b143lul.android.logreg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
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

public class Leaderboard extends AppCompatActivity {
    private final String getGroupParticipantsURL = "http://b143servertesting.gearhostpreview.com/GroupCodes/getGroupParticipants.php";
    private int localGroupCode;
    private String username;
    private JSONObject jsonOutput;
    private int id;
    String[] nameArr;
    int[] scoreArr;
    TextView tvName;
    TextView tvSteps;
    TextView tvPlacement;
    ListView listView;
    ImageButton X;
    ImageButton btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // Activity was brought to front and not created,
            // Thus finishing this will get us to the last viewed activity
            finish();
            return;
        }
        setContentView(R.layout.activity_leaderboard);
        SharedPreferences sharedPreferences = Leaderboard.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        X = (ImageButton) findViewById(R.id.x);
        Intent intent = getIntent();
        final String nameOfClass = intent.getExtras().getString("nameOfClass");
        username = sharedPreferences.getString("username", "username");
        btn_back = (ImageButton) findViewById(R.id.btn_back);

        if(sharedPreferences.getBoolean(LOGGEDIN_SHARED_PREF, false)) {
            id = sharedPreferences.getInt(ID_SHARED_PREF, -1);
        } else {
            AlertDialog.Builder alertbox=new AlertDialog.Builder(Leaderboard.this);
            alertbox.setTitle("How tf did u get here???");
            alertbox.setCancelable(false);
            finish();
        }
        X.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profIntent;

                if (nameOfClass.equals("CreateJoinClass")) {
                    profIntent = new Intent(Leaderboard.this, CreateJoinClass.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    profIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(profIntent);
                } else if (nameOfClass.equals("MapSelection")) {
                    profIntent = new Intent(Leaderboard.this, MapSelection.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    profIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(profIntent);
                } else if(nameOfClass.equals("ChallengeDetails")){
                    profIntent = new Intent(Leaderboard.this, ChallengeDetails.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    profIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(profIntent);
                } else if(nameOfClass.equals("JoinGroup")){
                    profIntent = new Intent(Leaderboard.this, JoinGroup.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    profIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(profIntent);
                } else if(nameOfClass.equals("TrackMap")){
                    profIntent = new Intent(Leaderboard.this, TrackMap.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    profIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(profIntent);
                } else if(nameOfClass.equals("createdGroupCode")){
                    profIntent = new Intent(Leaderboard.this, createdGroupCode.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    profIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(profIntent);
                }

            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        localGroupCode = sharedPreferences.getInt("groupcode", 00000);

        listView = (ListView) findViewById(R.id.listView);

        getGroupParticipants();
    }

    private void getGroupParticipants(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getGroupParticipantsURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // This will return all the names and scores from the current challenge.
                        getScoresAndNames(response);
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

    private void getScoresAndNames(String response) {
        String responseCheck = response;
        try {
            jsonOutput = new JSONObject(responseCheck);

            nameArr = new String[jsonOutput.names().length()];
            scoreArr = new int[jsonOutput.names().length()];

            for (int i = 0; i < jsonOutput.names().length(); i++) {
                nameArr[i] = jsonOutput.names().getString(i);
                scoreArr[i] = Integer.parseInt(jsonOutput.getString(nameArr[i]));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        CustomAdapter customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);
    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return nameArr.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.customlayout, null);

            TextView tvName = (TextView)view.findViewById(R.id.tv_name);
            TextView tvSteps = (TextView)view.findViewById(R.id.tv_steps);
            TextView tvPlacement = (TextView)view.findViewById(R.id.tv_placement);

            tvName.setText(nameArr[i].toUpperCase());
            tvSteps.setText(scoreArr[i] + " steps");
            tvPlacement.setText(1+i +".");
            return view;
        }
    }
}


