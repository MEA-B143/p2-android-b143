package com.b143lul.android.logreg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.b143lul.android.logreg.Login.ID_SHARED_PREF;
import static com.b143lul.android.logreg.Login.LOGGEDIN_SHARED_PREF;
import static com.b143lul.android.logreg.Login.SHARED_PREF_NAME;

public class JoinGroup extends AppCompatActivity {

    private final String JoinGroupURL = "http://b143servertesting.gearhostpreview.com/GroupCodes/JoinGroup.php";
    private int id;
    EditText edit_groupcode;
    FloatingActionButton btn_join;
    SharedPreferences sharedPreferences;
    ImageButton btn_menu;
    ImageButton btn_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        edit_groupcode = (EditText) findViewById(R.id.etEnterGC);
        btn_join = (FloatingActionButton) findViewById(R.id .btnLetsGo);
        btn_menu=(ImageButton) findViewById(R.id.btn_menu);
        btn_back = (ImageButton) findViewById(R.id.btn_back);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String className = "JoinGroup";
                Intent IntentMenu = new Intent(JoinGroup.this, Menu.class);
                IntentMenu.putExtra("className", className);
                startActivity(IntentMenu);

            }
        });


        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtGroupCode = edit_groupcode.getText().toString().trim();

                if(txtGroupCode.equals("")){
                    Toast.makeText(JoinGroup.this, "You have to input a group code.", Toast.LENGTH_SHORT).show();
                }
                else if(txtGroupCode.length() < 5 ){
                    Toast.makeText(JoinGroup.this, "A group code is 5 digits.", Toast.LENGTH_SHORT).show();
                }
                else {
                    getGroupInfo(txtGroupCode);
                    //Searching for the group code in the online database and if successful gather the information about the challenge.
                }
            }
        });

        sharedPreferences = JoinGroup.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            if(sharedPreferences.getBoolean(LOGGEDIN_SHARED_PREF, false)) {
                id = sharedPreferences.getInt(ID_SHARED_PREF, -1);
            } else {
                AlertDialog.Builder alertbox=new AlertDialog.Builder(JoinGroup.this);
                alertbox.setTitle("Error");
                alertbox.setCancelable(false);
                finish();
        }
    }

    void getGroupInfo(final String txtGroupCode) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, JoinGroupURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("NoGroupCode")){ //If the entered group code doesn't exist.
                                String ErrorMessage = "Group code doesn't exist.";
                                Toast.makeText(JoinGroup.this, ErrorMessage, Toast.LENGTH_SHORT).show();
                            }
                            else if(jsonObject.has("TooManyPlayers")){ //If the playerlimit is reached and there is no space for this player.
                                Toast.makeText(JoinGroup.this, "Too many players.", Toast.LENGTH_SHORT).show();
                            }
                            else if (jsonObject.has("groupcode")){ //If everything is successful and the group information is retrieved.
                                String groupDetailsString = jsonObject.getString("groupcode");
                                String[] groupDetails = groupDetailsString.split(",");
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt("groupcode", Integer.parseInt(groupDetails[0].trim()));
                                editor.commit();
                                Intent goMap = new Intent(JoinGroup.this, TrackMap.class);
                                startActivity(goMap);
                                //TODO - Enter TrackMap
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
            prams.put("groupCode", txtGroupCode);
            prams.put("id", Integer.toString(id));
            return prams;
        }
    };
    RequestQueue requestQueue = Volley.newRequestQueue(this);
    requestQueue.add(stringRequest);
    }
}

