package com.b143lul.android.logreg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class CreateJoinClass extends AppCompatActivity {
    private Button createButton;
    private Button joinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_join_class);
        createButton = (Button)findViewById(R.id.create);
        joinButton = (Button) findViewById(R.id.joinbutton);

    }
}
