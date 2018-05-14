package com.b143lul.android.logreg;

import android.content.Intent;
import android.media.Image;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MapSelection extends AppCompatActivity {
    ImageButton BtnTrack1;
    ImageButton BtnBack;
    ImageButton BtnMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_selection);
        BtnTrack1 = (ImageButton) findViewById(R.id.btn_track1);
        BtnBack = (ImageButton) findViewById(R.id.btn_back);
        BtnMenu = (ImageButton) findViewById(R.id.btn_menu);

        BtnTrack1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goChallengeDetails = new Intent(MapSelection.this, ChallengeDetails.class);
                startActivity(goChallengeDetails);
            }
        });
        BtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        BtnMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String className = "MapSelection";
                Intent IntentMenu = new Intent(MapSelection.this, Menu.class);
                IntentMenu.putExtra("className", className);
                startActivity(IntentMenu);

            }

        });
    }

}
