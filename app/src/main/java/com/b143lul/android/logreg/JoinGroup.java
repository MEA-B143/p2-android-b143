package com.b143lul.android.logreg;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import java.util.TooManyListenersException;

import static com.b143lul.android.logreg.Login.ID_SHARED_PREF;
import static com.b143lul.android.logreg.Login.LOGGEDIN_SHARED_PREF;
import static com.b143lul.android.logreg.Login.SHARED_PREF_NAME;

public class JoinGroup extends AppCompatActivity {

    private final String JoinGroupURL = "http://b143servertesting.gearhostpreview.com/GroupCodes/JoinGroup.php";
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        SharedPreferences sharedPreferences = JoinGroup.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            if(sharedPreferences.getBoolean(LOGGEDIN_SHARED_PREF, false)) {
                id = sharedPreferences.getInt(ID_SHARED_PREF, -1);
            } else {
                AlertDialog.Builder alertbox=new AlertDialog.Builder(JoinGroup.this);
                alertbox.setTitle("Ania is stupid, sry its true");
                alertbox.setCancelable(false);
                finish();
        }
        //localGroupCode = sharedPreferences.getInt("groupcode", 00000);
        getGroupInfo();
    }

    void getGroupInfo() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, JoinGroupURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("NoGroupCode")){ //If the entered group code doesn't exist.
                                String ErrorMessage = "Group code doesn't exist.";
                                //TODO - Display this message in the activity later.
                            }
                            else if(jsonObject.has("TooManyPlayers")){ //If the playerlimit is reached and there is no space for this player.
                                Toast.makeText(JoinGroup.this, "Too many players.", Toast.LENGTH_SHORT);
                                //TODO - Display this message in the activity later.
                            }
                            else if (jsonObject.has("groupcode")){ //If everything is successful and the group information is retrieved.
                                String groupDetailsString = jsonObject.getString("groupcode");
                                String[] groupDetails = groupDetailsString.split(",");
                            }
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
                }){
        protected Map<String, String> getParams () throws AuthFailureError {
            Map<String, String> prams = new HashMap<>();
            prams.put("groupCode", "12345");
            prams.put("id", Integer.toString(id));
            return prams;
        }
    };
    RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}

