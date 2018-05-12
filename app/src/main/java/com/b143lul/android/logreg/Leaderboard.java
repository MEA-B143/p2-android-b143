package com.b143lul.android.logreg;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import java.util.Iterator;
import java.util.Map;

import static com.b143lul.android.logreg.Login.ID_SHARED_PREF;
import static com.b143lul.android.logreg.Login.LOGGEDIN_SHARED_PREF;
import static com.b143lul.android.logreg.Login.SHARED_PREF_NAME;

public class Leaderboard extends AppCompatActivity {
    private final String getGroupParticipantsURL = "http://b143servertesting.gearhostpreview.com/GroupCodes/getGroupParticipants.php";
    private int localGroupCode;
    private String username;
    private JSONObject groupScores;
    private int id;
    String[] nameArr;
    int[] scoreArr;
    TextView tvName;
    TextView tvSteps;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        SharedPreferences sharedPreferences = Leaderboard.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "username");
        if(sharedPreferences.getBoolean(LOGGEDIN_SHARED_PREF, false)) {
            id = sharedPreferences.getInt(ID_SHARED_PREF, -1);
        } else {
            AlertDialog.Builder alertbox=new AlertDialog.Builder(Leaderboard.this);
            alertbox.setTitle("How tf did u get here???");
            alertbox.setCancelable(false);
            finish();
        }
        localGroupCode = sharedPreferences.getInt("groupcode", 00000);

        listView = (ListView) findViewById(R.id.listView);

        getGroupParticipants();
    }

    private void getGroupParticipants(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getGroupParticipantsURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // This will return all the scores.
                            String responseCheck = response;
                            try {
                            groupScores = new JSONObject(responseCheck);

                            String[] nameArr = new String[groupScores.names().length()];
                            int[] scoreArr = new int[groupScores.names().length()];

                            for (int i = 0; i < groupScores.names().length(); i++) {
                                nameArr[i] = groupScores.names().getString(i);
                                scoreArr[i] = Integer.parseInt(groupScores.getString(nameArr[i]));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        CustomAdapter customAdapter = new CustomAdapter();
                        listView.setAdapter(customAdapter);
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

            tvName.setText(nameArr[i]);
            tvSteps.setText(scoreArr[i] + "steps");
            return view;
        }
    }
}


