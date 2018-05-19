package com.b143lul.android.logreg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class MapSelection extends AppCompatActivity {
    ImageButton BtnTrack1;
    ImageButton BtnBack;
    ImageButton BtnMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // Activity was brought to front and not created,
            // Thus finishing this will get us to the last viewed activity
            finish();
            return;
        }
        setContentView(R.layout.activity_map_selection);
        BtnTrack1 = (ImageButton) findViewById(R.id.btn_track1);
        BtnBack = (ImageButton) findViewById(R.id.btn_back);
        BtnMenu = (ImageButton) findViewById(R.id.btn_menu);

        BtnTrack1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goChallengeDetails = new Intent(MapSelection.this, ChallengeDetails.class);
                goChallengeDetails.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
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
                IntentMenu.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(IntentMenu);

            }

        });
    }

}
