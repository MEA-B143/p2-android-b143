package com.b143lul.android.logreg;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import static com.b143lul.android.logreg.Login.SHARED_PREF_NAME;

//import static com.b143lul.android.logreg.Login.className;

public class Menu extends AppCompatActivity {
    ImageButton x;
    Button profile;
    Button leaderboard;
    Button settings;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        x = (ImageButton) findViewById(R.id.x);
        profile= (Button) findViewById(R.id.profile);
        leaderboard = (Button) findViewById(R.id.leaderboard);
        settings = (Button) findViewById(R.id.settings);
        logout = (Button) findViewById(R.id.logout);

        Intent intent = getIntent();
        final String nameOfClass = intent.getExtras().getString("className");

        x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

       profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent IntentProfile = new Intent(Menu.this, MyProfile.class);
                    IntentProfile.putExtra("nameOfClass", nameOfClass);
                    startActivity(IntentProfile);

            }
        });

        leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameOfClass.equals("TrackMap")) {
                    Intent IntentLeaderboard = new Intent(Menu.this, Leaderboard.class);
                    IntentLeaderboard.putExtra("nameOfClass", nameOfClass);
                    startActivity(IntentLeaderboard);
                } else {
                    Toast.makeText(Menu.this, "Leaderboard is only accessible in a challenge.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent IntentSettings = new Intent(Menu.this, SettingsScreen.class);
                IntentSettings.putExtra("nameOfClass", nameOfClass);
                startActivity(IntentSettings);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutPrompt();
            }
        });

    }

    private void logoutPrompt() {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        alertbox.setTitle("Are you sure?");
        alertbox.setCancelable(true);
        alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
            }
        });
        alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertbox.show();
    }

    private void logout() {
        SharedPreferences.Editor sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE).edit();
        sharedPreferences.clear();
        sharedPreferences.commit();
        Intent IntentLogout = new Intent(Menu.this, Login.class);
        IntentLogout.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(IntentLogout);
        finish();
    }
}
