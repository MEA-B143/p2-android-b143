package com.b143lul.android.logreg;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class Login extends AppCompatActivity {
    //public static String className;
    public static final String LOGIN_URL="http://b143servertesting.gearhostpreview.com/LogReg2/login.php";
    public static final String KEY_EMAIL="email";
    public static final String KEY_PASSWORD="password";
    public static final String LOGIN_SUCCESS="success";
    public static final String SHARED_PREF_NAME="tech";
    public static final String EMAIL_SHARED_PREF="email";
    public static final String LOGGEDIN_SHARED_PREF="loggedin";
    public static final String ID_SHARED_PREF = "userid";
    public static final String USERNAME_SHARED_PREF = "username";
    private final String receiveURL = "http://b143servertesting.gearhostpreview.com/GetVals/GetField.php";
    private static final String TAG = "Login.class";
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextUsername;
    private Button BtnLogin;
    private Button BtnNewUser;
    private boolean loggedIn=false;

    private final boolean shouldAllowBack = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //editTextEmail=(EditText)findViewById(R.id.editText_email);
        editTextUsername = (EditText) findViewById(R.id.editText_username);
        editTextPassword=(EditText)findViewById(R.id.editText_password);
        BtnLogin=(Button)findViewById(R.id.btn_login);
        BtnNewUser = (Button)findViewById(R.id.btn_new_user);

        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = editTextUsername.getText().toString().trim();
                final String password = editTextPassword.getText().toString().trim();
                login(username, password);
            }
        });

        BtnNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentSignUp=new Intent(Login.this,Signup.class);
                startActivity(intentSignUp);
            }
        });
    }

    private void login(final String username, final String password) {
        if (username.isEmpty()) {

        }
        final ProgressDialog progressDialog = new ProgressDialog(Login.this,R.style.Theme_AppCompat_Light);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.trim().startsWith(LOGIN_SUCCESS)){

                            SharedPreferences sharedPreferences = Login.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            editor.putBoolean(LOGGEDIN_SHARED_PREF, true);
                            //editor.putString(EMAIL_SHARED_PREF, email);
                            editor.putString(USERNAME_SHARED_PREF, username);
                            editor.putString(KEY_PASSWORD, password);
                            String[] retvals = response.trim().split(",");
                            int id = Integer.parseInt(retvals[1].trim());
                            int score = Integer.parseInt(retvals[2].trim());
                            editor.putInt(ID_SHARED_PREF, id);
                            int localScore = sharedPreferences.getInt("score", 0);
                            if (localScore > score) {
                                editor.putInt("score", localScore);
                            } else {
                                editor.putInt("score", score);
                            }
                            editor.commit();
                            progressDialog.dismiss();
                            checkGroup("groupcode", id);
                        } else{
                            Toast.makeText(Login.this, "Invalid Login Information", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> prams = new HashMap<>();
                prams.put("username", username);
                prams.put(KEY_PASSWORD, password);

                return prams;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        loggedIn = sharedPreferences.getBoolean(LOGGEDIN_SHARED_PREF, false);
        String username = sharedPreferences.getString(USERNAME_SHARED_PREF, null);
        String password = sharedPreferences.getString(KEY_PASSWORD, null);
        //String username = sharedPreferences.getString(USERNAME_SHARED_PREF,null);
        if(loggedIn){
            login(username, password);
        }
    }

    private void checkGroup(final String column, final int id) {
        // We need to check if the person is in a group before determining which activity to start next.
        //runOnUiThread(changeMessage);
        final ProgressDialog progressDialog = new ProgressDialog(Login.this,R.style.Theme_AppCompat_Light);
        progressDialog.setMessage("Checking for any group information...");
        progressDialog.show();

        // Booting up the progress dialog.

        // Building the requesting of group code from the server.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, receiveURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        onGroupCodeResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG,Log.getStackTraceString(error));
                        // Print any errors to the console.
                        progressDialog.dismiss();
                        // We also need to stop the progress dialog from staying up because then the app can continue.
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> prams = new HashMap<>();
                prams.put("id", Integer.toString(id));
                prams.put("field", column);

                return prams;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void onGroupCodeResponse(String response) {
        String code = response.split(",")[0];
        if (code.equals("none")) {
            // Not in a group.
            Intent groupJoinScreen = new Intent(Login.this, CreateJoinClass.class);
            startActivity(groupJoinScreen);
            finish();
        } else {
            Log.e(TAG, code);
            SharedPreferences sharedPreferences = Login.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            int debug = Integer.parseInt(code.trim());
            editor.putInt("groupcode", Integer.parseInt(code.trim()));
            editor.commit();
            Intent launchMap = new Intent(Login.this, TrackMap.class);
            startActivity(launchMap);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (!shouldAllowBack) {
            endAppPopup();
        } else {
            super.onBackPressed();
        }
    }

    private void endAppPopup() {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        alertbox.setTitle("Would you like to exit the app?");
        alertbox.setCancelable(true);
        alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                closeApp();
            }
        });
        alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertbox.show();
    }

    private void closeApp() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}
