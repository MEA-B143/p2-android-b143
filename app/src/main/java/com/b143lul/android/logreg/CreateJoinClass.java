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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_join_class);
        createButton = (Button)findViewById(R.id.create);
        joinButton = (Button) findViewById(R.id.joinbutton);
        btn_menu= (ImageButton)findViewById(R.id.btn_menu);

        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent IntentMenu = new Intent(CreateJoinClass.this, Menu.class);
                startActivity(IntentMenu);

            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goMapSelection = new Intent(CreateJoinClass.this, MapSelection.class);
                startActivity(goMapSelection);
            }
        });
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent joinGroup = new Intent(CreateJoinClass.this, JoinGroup.class);
                startActivity(joinGroup);
            }
        });
    }
}
