package com.b143lul.android.logreg;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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

public class CreateGroup extends AppCompatActivity {
    final String createGroupURL = "http://b143servertesting.gearhostpreview.com/GroupCodes/CreateGroup.php";
    private int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_selection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        SharedPreferences sharedPreferences = CreateGroup.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean(LOGGEDIN_SHARED_PREF, false)) {
            id = sharedPreferences.getInt(ID_SHARED_PREF, -1);
        } else {
            AlertDialog.Builder alertbox=new AlertDialog.Builder(CreateGroup.this);
            alertbox.setTitle("How tf did u get here???");
            alertbox.setCancelable(false);
            finish();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, createGroupURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(CreateGroup.this, response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.e("CreateGroup.java", error.toString());
                        error.printStackTrace();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> prams = new HashMap<>();
                prams.put("id", Integer.toString(id));
                prams.put("name", "Test group");
                prams.put("daylimit", "1");
                prams.put("hourlimit", "3");
                prams.put("playerlimit", "10");

                return prams;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}