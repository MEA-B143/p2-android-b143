package com.b143lul.android.logreg;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class WinScreen extends AppCompatActivity {

    ImageButton btnOkay;
    TextView tvPlacement;
    TextView tvTotalSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win_screen);

        Intent intent = getIntent();
        final String nameOfClass = intent.getExtras().getString("className");

        btnOkay= (ImageButton) findViewById(R.id.btnOkay);
        btnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent IntentCreateJoin = new Intent(WinScreen.this, CreateJoinClass.class);
                IntentCreateJoin.putExtra("nameOfClass", nameOfClass);
                startActivity(IntentCreateJoin);
            }
        });
    }
}
