package com.b143lul.android.logreg;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import static com.b143lul.android.logreg.Login.SHARED_PREF_NAME;


public class SettingsScreen extends AppCompatActivity {

    Switch switch1;
    Switch switch2;
    TextView settings_title;
    TextView notifications;
    TextView sounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference_screen);

        SharedPreferences sharedPreferences = SettingsScreen.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.commit();

        switch1 = (Switch) findViewById(R.id.switch1);
        switch2 = (Switch) findViewById(R.id.switch2);
        settings_title = (TextView) findViewById(R.id.textView3);
        notifications = (TextView) findViewById(R.id.textView4);
        sounds = (TextView) findViewById(R.id.textView5);

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences sharedPreferences = SettingsScreen.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (switch1.isChecked()) {
                    editor.putBoolean("notifications", true);
                    notifications.setText("Switch notifications on");
                    editor.commit();
                } else {
                    editor.putBoolean("notification", false);
                    notifications.setText("Switch notifcations off");
                    editor.commit();
                }
            }
        });

        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences sharedPreferences = SettingsScreen.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (switch2.isChecked()) {
                    editor.putBoolean("sounds", true);
                    sounds.setText("Switch sounds on");
                    editor.commit();
                } else {
                    editor.putBoolean("sounds", false);
                    sounds.setText("Switch sounds off");
                    editor.commit();
                }
            }
        });
    }
}