package com.example.weightwatchersthomasbryant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class signup extends AppCompatActivity {

    DatabaseHelper database = new DatabaseHelper(signup.this);
    User user;
    EditText et_emailLogin, et_passwordLogin, et_verifyPassword;
    Button btn_signUp, btn_loginTransfer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        et_emailLogin = findViewById(R.id.et_emailLogin);
        et_passwordLogin = findViewById(R.id.et_passwordLogin);
        btn_signUp = findViewById(R.id.btn_signUp);
        btn_loginTransfer = findViewById(R.id.btn_loginTransfer);
        et_verifyPassword = findViewById(R.id.et_verifyPassword);

        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_emailLogin.getText().toString().contains("@"))
                {
                    if(et_passwordLogin.getText().toString().equals(et_verifyPassword.getText().toString()))
                    {
                        //(int userID, String email, String password, double currGoalWeight, double currWeight)
                        String userEmail = et_emailLogin.getText().toString();
                        String userPassword = et_passwordLogin.getText().toString();

                        if(database.userExists(userEmail) != -1)
                        {
                            Toast.makeText(signup.this,"ERROR: User with that email already exists! Please sign in. (-1 returned.)", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            user = new User(0, userEmail, userPassword, 0.0,0.0);
                            boolean success = database.createUser(user); //we pass temporary values to the database - these will get overwritten very soon.
                            if(success)
                            {
                                user.setUserID(database.userExists(userEmail)); //if this ends up -1 we r SCREWED! hopefully it doesn't.
                                Toast.makeText(signup.this, "Successfully created account! Please sign in.", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Toast.makeText(signup.this, "ERROR: User failed to create! (-1 returned.)", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(signup.this, "Passwords do not match.", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(signup.this, "Invalid email entered: Does not contain symbol '@'", Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_loginTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signup.this, MainActivity.class)); //no need to pass anything here - just get 'em out.
            }
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}