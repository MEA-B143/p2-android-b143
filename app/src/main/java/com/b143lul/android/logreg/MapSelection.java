package com.b143lul.android.logreg;

import android.media.Image;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MapSelection extends AppCompatActivity {
    ImageButton BtnTrack1;
    ImageButton BtnBack;
    ImageButton Menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_selection);
        BtnTrack1 = (ImageButton)findViewById(R.id.btnCreate);

        BtnTrack1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
