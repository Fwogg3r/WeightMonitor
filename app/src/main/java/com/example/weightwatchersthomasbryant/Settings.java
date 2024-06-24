package com.example.weightwatchersthomasbryant;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

public class Settings extends AppCompatActivity {

    private static final int SEND_SMS_PERMISSION_CODE = 1;
    private static final int READ_PHONE_STATE_PERMISSION_CODE = 1;

    Button btn_ModifyWeight;
    ImageButton imgBtn_Return;
    Switch sw_smsNotifs;



    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        imgBtn_Return = findViewById(R.id.btn_Return);
        btn_ModifyWeight = findViewById(R.id.btn_ModifyWeight);
        sw_smsNotifs = findViewById(R.id.sw_smsNotifs);

        Intent i = getIntent();
        //int userID, String email, String password, double currGoalWeight, double currWeight
        int userID = i.getIntExtra("userID", 0); // if there's a default value we have a problem.
        String email = i.getStringExtra("userEmail");
        String password = i.getStringExtra("userPassword");
        double currGoalWeight = i.getDoubleExtra("userCurrGoalWeight", 0.0);
        double currWeight = i.getDoubleExtra("userCurrWeight", 0.0);
        user = new User(userID, email, password, currGoalWeight, currWeight);

        imgBtn_Return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.this, HomeScreen.class);
                //unfortunately the way HomeScreen is built we have to pass this information again. Could I have made it a global variable? probably. did I know how to do that? nope.
                intent.putExtra("userID", user.getUserID());
                intent.putExtra("userEmail", user.getEmail());
                intent.putExtra("userPassword", user.getPassword());
                intent.putExtra("userCurrGoalWeight", user.getCurrGoalWeight());
                intent.putExtra("userCurrWeight", user.getCurrWeight());
                startActivity(intent);
            }
        });

        btn_ModifyWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.this, CurrentWeightSetter.class);
                //unfortunately the way CurrentWeightSetter is built we have to pass this information again. Could I have made it a global variable? probably. did I know how to do that? nope.
                intent.putExtra("userID", user.getUserID());
                intent.putExtra("userEmail", user.getEmail());
                intent.putExtra("userPassword", user.getPassword());
                intent.putExtra("userCurrGoalWeight", user.getCurrGoalWeight());
                intent.putExtra("userCurrWeight", user.getCurrWeight());
                startActivity(intent);
            }
        });


        sw_smsNotifs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    if(ContextCompat.checkSelfPermission(Settings.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(Settings.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(Settings.this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_CODE);
                        ActivityCompat.requestPermissions(Settings.this, new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE_PERMISSION_CODE);
                    }
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}