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

import java.util.HashMap;
import java.util.Map;

import static com.b143lul.android.logreg.Login.ID_SHARED_PREF;
import static com.b143lul.android.logreg.Login.LOGGEDIN_SHARED_PREF;
import static com.b143lul.android.logreg.Login.SHARED_PREF_NAME;

public class WinScreen extends AppCompatActivity {

    ImageButton btnOkay;
    TextView tvPlacement;
    TextView tvTotalSteps;
    private final String forfeitURL = "http://b143servertesting.gearhostpreview.com/GroupCodes/forfeit.php";
    private int id;
    private int localGroupCode;
    SharedPreferences sharedPreferences;
    private int placement;
    private final boolean shouldAllowBack = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // Activity was brought to front and not created,
            // Thus finishing this will get us to the last viewed activity
            finish();
            return;
        }
        setContentView(R.layout.activity_win_screen);

        Intent intent = getIntent();

        btnOkay = (ImageButton) findViewById(R.id.btn_okay);
        btnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forfeit();
                Intent IntentCreateJoin = new Intent(WinScreen.this, CreateJoinClass.class);
                startActivity(IntentCreateJoin);
            }
        });
        tvPlacement = (TextView)findViewById(R.id.tvPlacement);
        sharedPreferences = WinScreen.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        localGroupCode = sharedPreferences.getInt("groupcode", 0);
        if(sharedPreferences.getBoolean(LOGGEDIN_SHARED_PREF, false)) {
            id = sharedPreferences.getInt(ID_SHARED_PREF, -1);
        } else {
            AlertDialog.Builder alertbox=new AlertDialog.Builder(getApplicationContext());
            alertbox.setTitle("How tf did u get here???");
            alertbox.setCancelable(false);
            finish();
        }
        placement = sharedPreferences.getInt("placement", 0);
        if (placement == 0) {

        }
        tvPlacement.setText("No. " + String.valueOf(placement));
    }


    private void forfeit() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, forfeitURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // If successful don't expect response
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("groupcode");
                        editor.remove("groupname");
                        editor.commit();
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

    @Override
    public void onBackPressed() {
        if (!shouldAllowBack) {
            // Yikes...
        } else {
            super.onBackPressed();
        }
    }
}
