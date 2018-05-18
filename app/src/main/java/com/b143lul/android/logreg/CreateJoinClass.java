package com.b143lul.android.logreg;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class CreateJoinClass extends AppCompatActivity {
    private Button createButton;
    private Button joinButton;
    ImageButton btn_menu;
    private final boolean shouldAllowBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // Activity was brought to front and not created,
            // Thus finishing this will get us to the last viewed activity
            finish();
            return;
        }
        setContentView(R.layout.activity_create_join_class);
        createButton = (Button)findViewById(R.id.create);
        joinButton = (Button) findViewById(R.id.joinbutton);
        btn_menu = (ImageButton) findViewById(R.id.btn_menu);

        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String className = "CreateJoinClass";
                Intent IntentMenu = new Intent(CreateJoinClass.this, Menu.class);
                IntentMenu.putExtra("className", className);
                IntentMenu.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(IntentMenu);
                finish();
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goMapSelection = new Intent(CreateJoinClass.this, MapSelection.class);
                goMapSelection.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(goMapSelection);
            }
        });
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent joinGroup = new Intent(CreateJoinClass.this, JoinGroup.class);
                joinGroup.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(joinGroup);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!shouldAllowBack) {
            // Yikes...
        } else {
            super.onBackPressed();
        }
    }
}
