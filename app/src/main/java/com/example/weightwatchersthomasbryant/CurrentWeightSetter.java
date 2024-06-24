package com.example.weightwatchersthomasbryant;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import java.util.Random;

public class CurrentWeightSetter extends AppCompatActivity {

    TelephonyManager tMgr;
    SmsManager sms;
    public boolean sendSMS(String message)
    {
        String phoneNumber;
        try
        {
            phoneNumber = tMgr.getLine1Number();
            Intent intent = new Intent(CurrentWeightSetter.this, Settings.class);
            PendingIntent pi = PendingIntent.getActivity(CurrentWeightSetter.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            sms.sendTextMessage(phoneNumber, null, message, pi, null);
        }
        catch(SecurityException e)
        {
            return false;
        }

        return true;
    }

    User user;
    DatabaseHelper database = new DatabaseHelper(CurrentWeightSetter.this);
    EditText et_numberGoalWeight, et_numberCurrentWeight;
    TextView textView_inspirationalMessage;
    Button btn_continue;

    //https://www.usatoday.com/story/life/2023/11/30/positive-quotes-to-inspire/11359498002/
    private final String[] inspirationalMessages = {
            "\"It takes courage to grow up and become who you really are.\" — E.E. Cummings",
            "\"Your self-worth is determined by you. You don't have to depend on someone telling you who you are.\" — Beyoncé",
            "\"Nothing is impossible. The word itself says 'I'm possible!'\" — Audrey Hepburn",
            "\"Keep your face always toward the sunshine, and shadows will fall behind you.\" — Walt Whitman",
            "\"You have brains in your head. You have feet in your shoes. You can steer yourself any direction you choose. You're on your own. And you know what you know. And you are the guy who'll decide where to go.\" — Dr. Seuss",
            "\"Attitude is a little thing that makes a big difference.\" — Winston Churchill",
            "\"To bring about change, you must not be afraid to take the first step. We will fail when we fail to try.\" — Rosa Parks",
            "\"All our dreams can come true, if we have the courage to pursue them.\" — Walt Disney",
            "\"Don't sit down and wait for the opportunities to come. Get up and make them.\" — Madam C.J. Walker",
            "\"Champions keep playing until they get it right.\" — Billie Jean King",
            "\"I am lucky that whatever fear I have inside me, my desire to win is always stronger.\" — Serena Williams",
            "\"You are never too old to set another goal or to dream a new dream.\" — C.S. Lewis",
            "\"It is during our darkest moments that we must focus to see the light.\" — Aristotle",
            "\"Believe you can and you're halfway there.\" — Theodore Roosevelt",
            "\"Life shrinks or expands in proportion to one’s courage.\" — Anaïs Nin",
            "\"Just don't give up trying to do what you really want to do. Where there is love and inspiration, I don't think you can go wrong.\" — Ella Fitzgerald",
            "\"Try to be a rainbow in someone's cloud.\" — Maya Angelou",
            "\"If you don't like the road you're walking, start paving another one.\" — Dolly Parton",
            "\"Real change, enduring change, happens one step at a time.\" — Ruth Bader Ginsburg",
            "\"All dreams are within reach. All you have to do is keep moving towards them.\" — Viola Davis",
            "\"It is never too late to be what you might have been.\" — George Eliot",
            "\"When you put love out in the world it travels, and it can touch people and reach people in ways that we never even expected.\" — Laverne Cox",
            "\"Give light and people will find the way.\" — Ella Baker",
            "\"It always seems impossible until it's done.\" — Nelson Mandela",
            "\"Don’t count the days, make the days count.\" — Muhammad Ali",
            "\"If you risk nothing, then you risk everything.\" — Geena Davis",
            "\"Definitions belong to the definers, not the defined.\" — Toni Morrison",
            "\"When you have a dream, you've got to grab it and never let go.\" — Carol Burnett",
            "\"Never allow a person to tell you no who doesn’t have the power to say yes.\" — Eleanor Roosevelt",
            "\"When it comes to luck, you make your own.\" — Bruce Springsteen",
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_current_weight_setter);

        sms = SmsManager.getDefault();
        tMgr = (TelephonyManager)CurrentWeightSetter.this.getSystemService(Context.TELEPHONY_SERVICE);

        et_numberCurrentWeight = findViewById(R.id.et_numberCurrentWeight);
        et_numberGoalWeight = findViewById(R.id.et_numberGoalWeight);
        textView_inspirationalMessage = findViewById(R.id.textView_inspirationalMessage);
        btn_continue = findViewById(R.id.btn_continue);

        Random rand = new Random(); //pick a random quote from the list
        int randInt = rand.nextInt(inspirationalMessages.length);
        textView_inspirationalMessage.setText(inspirationalMessages[randInt]);

        Intent i = getIntent();
        //int userID, String email, String password, double currGoalWeight, double currWeight
        int userID = i.getIntExtra("userID", 0); // if there's a default value we have a problem.
        String email = i.getStringExtra("userEmail");
        String password = i.getStringExtra("userPassword");
        double currGoalWeight = i.getDoubleExtra("userCurrGoalWeight", 0.0);
        double currWeight = i.getDoubleExtra("userCurrWeight", 0.0);
        user = new User(userID, email, password, currGoalWeight, currWeight);


        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_numberCurrentWeight.getText() != null && et_numberGoalWeight.getText() != null)
                {
                    user = new User(userID, email, password, Double.parseDouble(et_numberGoalWeight.getText().toString()), Double.parseDouble(et_numberCurrentWeight.getText().toString())); //yikes...
                    user.setCurrGoalWeight(Double.parseDouble(et_numberGoalWeight.getText().toString()));
                    user.setCurrWeight(Double.parseDouble(et_numberCurrentWeight.getText().toString()));
                    boolean success = database.logWeight(user);
                    if(success)
                    {
                        Intent intent = new Intent(CurrentWeightSetter.this, HomeScreen.class);
                        intent.putExtra("userID", user.getUserID());
                        intent.putExtra("userEmail", user.getEmail());
                        intent.putExtra("userPassword", user.getPassword());
                        intent.putExtra("userCurrGoalWeight", user.getCurrGoalWeight());
                        intent.putExtra("userCurrWeight", user.getCurrWeight());
                        if(user.getCurrWeight() == user.getCurrGoalWeight())
                        {
                            sendSMS("Congratulations on reaching your weight goal!");
                        }
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(CurrentWeightSetter.this, "Error: Weight failed to log! Please try again.", Toast.LENGTH_LONG).show();
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