package com.example.arafatm.anti_socialmedia.Authentification;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.arafatm.anti_socialmedia.Home.MainActivity;
import com.example.arafatm.anti_socialmedia.R;
import com.example.arafatm.anti_socialmedia.SignupActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    LoginButton loginButton;
    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if (isLoggedIn && ParseUser.getCurrentUser() != null) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("user_friends", "public_profile", "email"));
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }

        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("user_friends", "public_profile", "email"));
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                requestFBInfo(loginResult);
            }

            @Override
            public void onCancel() {
                Log.d("LoginActivity", "Login cancelled");
            }

            @Override
            public void onError(FacebookException exception) {
                exception.printStackTrace();
            }
        });


        signupButton = findViewById(R.id.btSwitchToSignup);
        signupButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);

            }
        });


    }

    // Every activity/fragment with FacebookSDK Login should forward onActivityResult to the callbackManager.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void signup(final String userId, String fullname, String email) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            ParseUser.logOut();
        }
        ParseUser parseUser = new ParseUser();
        // Set core properties
        parseUser.setUsername(userId);
        parseUser.setPassword(userId);
        parseUser.put("fullName", fullname);
        parseUser.setEmail(email);
        // Invoke signUpInBackground
        parseUser.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {

                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    // try logging user in rather than signing up
                    ParseUser.logInInBackground(userId, userId, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (e == null) {
                                Log.d("LoginActivity", "Login successful");

                                final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Log.e("LoginActivity", "Login failure");
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    private void requestFBInfo(LoginResult loginResult) {
        // define request for Facebook user's information
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("GraphRequest", object.toString());
                        // Application code
                        try {
                            final String userId = object.getString("id");
                            final String email = object.getString("email");
                            final String fullname = object.getString("name");
                            signup(userId, fullname, email);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        // execute request asynchronously
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email");
        request.setParameters(parameters);
        request.executeAsync();
        Toast.makeText(LoginActivity.this, "Logging you in...", Toast.LENGTH_LONG).show();
    }
}
