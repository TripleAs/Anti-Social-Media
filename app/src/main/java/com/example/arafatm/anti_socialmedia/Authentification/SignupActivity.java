package com.example.arafatm.anti_socialmedia.Authentification;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.arafatm.anti_socialmedia.Home.MainActivity;
import com.example.arafatm.anti_socialmedia.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity {

    @BindView(R.id.etFullName) EditText fullnameInput;
    @BindView(R.id.etUsername) EditText usernameInput;
    @BindView(R.id.etPassword) EditText passwordInput;
    @BindView(R.id.etEmail) EditText emailInput;
    @BindView(R.id.btSignup) Button signUpBtn;
    @BindView(R.id.btSwitchToLogin) Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        signUpBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                final String fullname = fullnameInput.getText().toString();
                final String username = usernameInput.getText().toString();
                final String password = passwordInput.getText().toString();
                final String email = emailInput.getText().toString();

                signUp(fullname, username, password, email);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void signUp(String fullname, String username, String password, String email){
        ParseUser user = new ParseUser();

        // not a default function for "fullName"
        user.put("fullName", fullname);

        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);

        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("LoginActivity", "Sign up successful!");


                    final Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    startActivity(intent);

                    Toast.makeText(getApplicationContext(), "Done!", Toast.LENGTH_LONG).show();

                    finish();

                } else {
                    Toast.makeText(getApplicationContext(), "Unsuccessful", Toast.LENGTH_LONG).show();
                    Log.e("LoginActivity", "Sign up failure.");
                    e.printStackTrace();
                }
            }
        });
    }
}
