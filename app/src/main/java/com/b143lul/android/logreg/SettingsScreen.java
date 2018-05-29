package com.b143lul.android.logreg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import static com.b143lul.android.logreg.Login.SHARED_PREF_NAME;

public class SettingsScreen extends AppCompatActivity {

    Switch switch1;
    Switch switch2;
    TextView settings_title;
    TextView notifications;
    TextView sounds;
    ImageButton btn_back;
    ImageButton X;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // Activity was brought to front and not created,
            // Thus finishing this will get us to the last viewed activity
            finish();
            return;
        }
        setContentView(R.layout.activity_preference_screen);
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        X = (ImageButton) findViewById(R.id.x);
        Intent intent = getIntent();
        final String nameOfClass = intent.getExtras().getString("nameOfClass");
        SharedPreferences sharedPreferences = SettingsScreen.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.commit();

        switch1 = (Switch) findViewById(R.id.switch1);
        switch2 = (Switch) findViewById(R.id.switch2);
        settings_title = (TextView) findViewById(R.id.textView3);
        notifications = (TextView) findViewById(R.id.textView4);
        sounds = (TextView) findViewById(R.id.textView5);

        X.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profIntent;

                if (nameOfClass.equals("CreateJoinClass")) {
                    profIntent = new Intent(SettingsScreen.this, CreateJoinClass.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    profIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(profIntent);
                } else if (nameOfClass.equals("MapSelection")) {
                    profIntent = new Intent(SettingsScreen.this, MapSelection.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    profIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(profIntent);
                } else if(nameOfClass.equals("ChallengeDetails")){
                    profIntent = new Intent(SettingsScreen.this, ChallengeDetails.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    profIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(profIntent);
                } else if(nameOfClass.equals("JoinGroup")){
                    profIntent = new Intent(SettingsScreen.this, JoinGroup.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    profIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(profIntent);
                } else if(nameOfClass.equals("TrackMap")){
                    profIntent = new Intent(SettingsScreen.this, TrackMap.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    profIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(profIntent);
                } else if(nameOfClass.equals("createdGroupCode")){
                    profIntent = new Intent(SettingsScreen.this, createdGroupCode.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    profIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(profIntent);
                }

            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        switch1.setChecked(sharedPreferences.getBoolean("notifications", true));
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
                    editor.putBoolean("notifications", false);
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