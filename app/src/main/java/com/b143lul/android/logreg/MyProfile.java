package com.b143lul.android.logreg;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import static com.b143lul.android.logreg.Login.SHARED_PREF_NAME;

public class MyProfile extends AppCompatActivity {
    private final String getGroupParticipantsURL = "http://b143servertesting.gearhostpreview.com/GroupCodes/getGroupParticipants.php";
    String username_Name;
    TextView usernameName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        usernameName = (TextView) findViewById(R.id.usernameName);
        SharedPreferences sharedPreferences = MyProfile.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        username_Name = sharedPreferences.getString("username", "username");
        usernameName.setText(username_Name);
    }
}
