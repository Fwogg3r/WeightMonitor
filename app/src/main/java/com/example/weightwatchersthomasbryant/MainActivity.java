package com.example.weightwatchersthomasbryant;

import android.content.Intent;
import android.os.Bundle;
import android.util.JsonWriter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper database = new DatabaseHelper(MainActivity.this);
    User user;
    EditText et_emailLogin, et_passwordLogin;
    Button btn_login, btn_signUpTransfer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        et_emailLogin = findViewById(R.id.et_emailLogin);
        et_passwordLogin = findViewById(R.id.et_passwordLogin);
        btn_login = findViewById(R.id.btn_login);
        btn_signUpTransfer = findViewById(R.id.btn_signUpTransfer);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!et_emailLogin.getText().toString().isEmpty() && !et_passwordLogin.getText().toString().isEmpty())
                {
                    user = database.userLogin(et_emailLogin.getText().toString(), et_passwordLogin.getText().toString());
                    if(user != null)
                    {
                        Intent intent;
                        //int userID, String email, String password, double currGoalWeight, double currWeight
                        if(database.weightLoggedToday(user))
                        {
                            intent = new Intent(MainActivity.this, HomeScreen.class);
                        }
                        else
                        {
                            intent = new Intent(MainActivity.this, CurrentWeightSetter.class);
                        }
                        intent.putExtra("userID", user.getUserID());
                        intent.putExtra("userEmail", user.getEmail());
                        intent.putExtra("userPassword", user.getPassword());
                        intent.putExtra("userCurrGoalWeight", user.getCurrGoalWeight());
                        intent.putExtra("userCurrWeight", user.getCurrWeight());
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Error: Incorrect email or password! Please try again or sign up.", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Error: One or more fields is empty!", Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_signUpTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, signup.class);
                startActivity(intent); //Switch login page to signup.
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}