package com.b143lul.android.logreg;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.b143lul.android.logreg.Login.LOGGEDIN_SHARED_PREF;
import static com.b143lul.android.logreg.Login.SHARED_PREF_NAME;

public class Signup extends AppCompatActivity {
    EditText edit_username;
    EditText edit_email;
    EditText edit_pass;
    Button btn_sign;
    Button btn_login;
    public static final String LOGIN_URL="http://b143servertesting.gearhostpreview.com/LogReg2/login.php";
    private static final String REGISTER_URL="http://b143servertesting.gearhostpreview.com/LogReg2/register.php";
    ProgressDialog progressDialog;
    public static final String ID_SHARED_PREF = "userid";
    public static final String KEY_PASSWORD="password";
    public static final String LOGIN_SUCCESS="success";
    public static final String USERNAME_SHARED_PREF = "username";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        edit_username = (EditText) findViewById(R.id.id_username);
        edit_email = (EditText) findViewById(R.id.id_email);
        edit_pass = (EditText) findViewById(R.id.id_pass);
        btn_sign = (Button) findViewById(R.id.btn_signup);
        btn_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        btn_login=(Button)findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLogin=new Intent(Signup.this,Login.class);
                startActivity(intentLogin);
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        boolean loggedIn = sharedPreferences.getBoolean(LOGGEDIN_SHARED_PREF, false);
        if (loggedIn) {
            Intent intentLogin=new Intent(Signup.this,Login.class);
            startActivity(intentLogin);
        }
        progressDialog = new ProgressDialog(Signup.this,R.style.Theme_AppCompat_Light);

    }

    private void registerUser() {
        String username = edit_username.getText().toString().trim().toLowerCase();
        String email = edit_email.getText().toString().trim().toLowerCase();
        String password = edit_pass.getText().toString().trim().toLowerCase();
        register(username, password, email);
        login(username,password);
    }

    private void register(String username, String password, String email){
        String urlSuffix = "?username=" + username + "&password=" + password + "&email=" + email;
        class RegisterUser extends AsyncTask<String, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Signup.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Intent groupJoinScreen = new Intent(Signup.this, CreateJoinClass.class);
                startActivity(groupJoinScreen);

            }

            @Override
            protected String doInBackground(String... params) {
                String s = params[0];
                BufferedReader bufferReader=null;
                try {
                    URL url=new URL(REGISTER_URL+s);
                    HttpURLConnection con=(HttpURLConnection)url.openConnection();
                    bufferReader=new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String result;
                    result=bufferReader.readLine();
                    return  result;

                }catch (Exception e){
                    return null;
                }
            }

        }
        RegisterUser ur=new RegisterUser();
        ur.execute(urlSuffix);
    }

    private void login(final String username, final String password) {
        if (username.isEmpty()) {

        }
        if (!username.contains("@")) {

        }
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.trim().startsWith(LOGIN_SUCCESS)){

                            SharedPreferences sharedPreferences = Signup.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            editor.putBoolean(LOGGEDIN_SHARED_PREF, true);
                            //editor.putString(EMAIL_SHARED_PREF, email);
                            editor.putString(USERNAME_SHARED_PREF, username);
                            editor.putString(KEY_PASSWORD, password);
                            String[] retvals = response.trim().split(",");
                            int id = Integer.parseInt(retvals[1].trim());
                            editor.putInt(ID_SHARED_PREF, id);
                            editor.commit();
                            Intent groupJoinScreen = new Intent(Signup.this, CreateJoinClass.class);
                            startActivity(groupJoinScreen);

                        } else{
                            Toast.makeText(Signup.this, response, Toast.LENGTH_LONG).show();
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
}
