package com.b143lul.android.logreg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import static com.b143lul.android.logreg.Login.ID_SHARED_PREF;
import static com.b143lul.android.logreg.Login.LOGGEDIN_SHARED_PREF;
import static com.b143lul.android.logreg.Login.SHARED_PREF_NAME;

public class createdGroupCode extends AppCompatActivity {
    private final String getGroupParticipantsURL = "http://b143servertesting.gearhostpreview.com/GroupCodes/getGroupParticipants.php";
    //private final String createGroupURL = "http://b143servertesting.gearhostpreview.com/GroupCodes/CreateGroup.php";
    private int id;
    private int localGroupCode;
    FloatingActionButton btn_LetsGo;
    TextView crGroupCode;
    ImageButton btn_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createdgroupcode);
        crGroupCode = (TextView) findViewById(R.id.groupcode);
        btn_LetsGo = (FloatingActionButton) findViewById(R.id.btnLetsGo);
        SharedPreferences sharedPreferences = createdGroupCode.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        localGroupCode = sharedPreferences.getInt("groupcode", 00000);
        showGroupCode(localGroupCode);

        btn_menu = (ImageButton) findViewById(R.id.btn_menu);

        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String className = "createdGroupCode";
                Intent IntentMenu = new Intent(createdGroupCode.this, Menu.class);
                IntentMenu.putExtra("className", className);
                startActivity(IntentMenu);

            }
        });


        btn_LetsGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent IntentTrackMap = new Intent(createdGroupCode.this, TrackMap.class);
                startActivity(IntentTrackMap);
            }
        });
        {

            if (sharedPreferences.getBoolean(LOGGEDIN_SHARED_PREF, false)) {
                id = sharedPreferences.getInt(ID_SHARED_PREF, -1);
            } else {
                AlertDialog.Builder alertbox = new AlertDialog.Builder(createdGroupCode.this);
                alertbox.setTitle("How tf did u get here???");
                alertbox.setCancelable(false);
                finish();
            }
        }
    }
        private void showGroupCode ( int localGroupCode){
            crGroupCode.setText(String.valueOf(localGroupCode));
        }

}