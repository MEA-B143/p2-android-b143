package com.b143lul.android.logreg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import java.util.HashMap;
import java.util.Map;

import static com.b143lul.android.logreg.Login.ID_SHARED_PREF;
import static com.b143lul.android.logreg.Login.LOGGEDIN_SHARED_PREF;
import static com.b143lul.android.logreg.Login.SHARED_PREF_NAME;

public class createdGroupCode extends AppCompatActivity {
    private final String getGroupParticipantsURL = "http://b143servertesting.gearhostpreview.com/GroupCodes/getGroupParticipants.php";
    private final String forfeitURL = "http://b143servertesting.gearhostpreview.com/GroupCodes/forfeit.php";
    //private final String createGroupURL = "http://b143servertesting.gearhostpreview.com/GroupCodes/CreateGroup.php";
    private int id;
    private int localGroupCode;
    FloatingActionButton btn_LetsGo;
    TextView crGroupCode;
    ImageButton btn_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // Activity was brought to front and not created,
            // Thus finishing this will get us to the last viewed activity
            finish();
            return;
        }
        setContentView(R.layout.activity_createdgroupcode);
        crGroupCode = (TextView) findViewById(R.id.groupcode);
        btn_LetsGo = (FloatingActionButton) findViewById(R.id.btnLetsGo);
        SharedPreferences sharedPreferences = createdGroupCode.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        localGroupCode = sharedPreferences.getInt("groupcode", 00000);
        showGroupCode(localGroupCode);

        btn_menu = (ImageButton) findViewById(R.id.btn_menu);

        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String className = "createdGroupCode";
                Intent IntentMenu = new Intent(createdGroupCode.this, Menu.class);
                IntentMenu.putExtra("className", className);
                IntentMenu.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(IntentMenu);
            }
        });


        btn_LetsGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent IntentTrackMap = new Intent(createdGroupCode.this, TrackMap.class);
                IntentTrackMap.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                IntentTrackMap.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(IntentTrackMap);
                finish();
            }
        });

        if (sharedPreferences.getBoolean(LOGGEDIN_SHARED_PREF, false)) {
            id = sharedPreferences.getInt(ID_SHARED_PREF, -1);
        } else {
            AlertDialog.Builder alertbox = new AlertDialog.Builder(createdGroupCode.this);
            alertbox.setTitle("How tf did u get here???");
            alertbox.setCancelable(false);
            finish();
        }
    }

    private void showGroupCode ( int localGroupCode){
        crGroupCode.setText(String.valueOf(localGroupCode));
    }

    @Override
    public void onBackPressed() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, forfeitURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // If successful don't expect response
                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("groupcode");
                        editor.remove("groupname");
                        editor.commit();
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
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
}