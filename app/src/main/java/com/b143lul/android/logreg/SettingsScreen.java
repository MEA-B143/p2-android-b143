package com.b143lul.android.logreg;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingsScreen extends AppCompatActivity {
    public static final String SETTINGS="tech";
    SharedPreferences sharedPreferences = SettingsScreen.this.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference_screen);
    }
}
