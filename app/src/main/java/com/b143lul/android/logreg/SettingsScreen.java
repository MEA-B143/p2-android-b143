package com.b143lul.android.logreg;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsScreen extends AppCompatActivity {
    public static final String SETTINGS="tech";
    SharedPreferences sharedPreferences = SettingsScreen.this.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    private boolean switch_settings=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference_screen);
Switch switch1 = (Switch) findViewById(R.id.switch1);
Switch switch2 = (Switch) findViewById(R.id.switch2);
TextView title = (TextView) findViewById(R.id.textView3);
TextView notifications = (TextView) findViewById(R.id.textView4);
TextView sounds = (TextView) findViewById(R.id.textView5);
    }
}