package com.b143lul.android.logreg;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class Menu extends AppCompatActivity {

    ImageButton x;
    Button profile;
    Button leaderboard;
    Button settings;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        x = (ImageButton) findViewById(R.id.x);
        profile= (Button) findViewById(R.id.profile);
        leaderboard = (Button) findViewById(R.id.leaderboard);
        settings = (Button) findViewById(R.id.settings);
        logout = (Button) findViewById(R.id.logout);

        x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

       /* profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent IntentProfile = new Intent(Menu.this, profile.class);
                startActivity(IntentProfile);

            }
        });

        leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent IntentLeaderboard = new Intent(Menu.this, leaderboard.class);
                startActivity(IntentLeaderboard);

            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent IntentSettings = new Intent(Menu.this, settings.class);
                startActivity(IntentSettings);

            }
        });*/

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent IntentLogout = new Intent(Menu.this, Login.class);
                startActivity(IntentLogout);

            }
        });


    }
}
