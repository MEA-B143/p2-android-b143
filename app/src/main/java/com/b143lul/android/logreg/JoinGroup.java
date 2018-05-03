package com.b143lul.android.logreg;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.TooManyListenersException;

import static com.b143lul.android.logreg.Login.SHARED_PREF_NAME;

public class JoinGroup extends AppCompatActivity {

    private final String JoinGroupURL = "http://b143servertesting.gearhostpreview.com/GroupCodes/JoinGroup.php";
    private int localGroupCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        SharedPreferences sharedPreferences = JoinGroup.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        localGroupCode = sharedPreferences.getInt("groupcode", 00000);
        getGroupInfo();
    }

    void getGroupInfo() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, JoinGroupURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String groupResponse = response;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }){
        protected Map<String, String> getParams () throws AuthFailureError {
            Map<String, String> prams = new HashMap<>();
            prams.put("groupCode", Integer.toString(localGroupCode));
            return prams;
        }
    };
    RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}

