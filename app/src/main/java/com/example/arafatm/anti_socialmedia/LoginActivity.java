package com.example.arafatm.anti_socialmedia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.arafatm.anti_socialmedia.Authentification.SignupActivity;
import com.example.arafatm.anti_socialmedia.Home.MainActivity;
import com.example.arafatm.anti_socialmedia.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    LoginButton loginButton;
    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        persistLogin();

        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(
                "user_friends", "public_profile", "email"));
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

    private void persistLogin() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if (isLoggedIn && ParseUser.getCurrentUser() != null) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("user_friends", "public_profile", "email"));
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    private void requestFBInfo(final LoginResult loginResult) {
        // define request for Facebook user's information
        GraphRequest meRequest = GraphRequest.newMeRequest(
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
                            final String propicUrl = object.getJSONObject("picture")
                                    .getJSONObject("data").getString("url");
                            loginOrSignup(userId, fullname, email, propicUrl, loginResult);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        // execute request asynchronously
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture.type(large)");
        meRequest.setParameters(parameters);
        meRequest.executeAsync();

        Toast.makeText(LoginActivity.this, "Logging you in...", Toast.LENGTH_LONG).show();
    }

    private void loginOrSignup(final String userId, final String fullname, final String email,
                               final String propicUrl, final LoginResult loginResult) {
        ParseUser.logInInBackground(userId, userId, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Log.d("LoginActivity", "Login successful");
                    final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
//                    getFriends(loginResult);
                    finish();
                } else {
                    ParseUser parseUser = new ParseUser();
                    // Set core properties
                    parseUser.setUsername(userId);
                    parseUser.setPassword(userId);
                    parseUser.put("fullName", fullname);
                    parseUser.setEmail(email);
                    parseUser.put("propicUrl", propicUrl);
                    // Invoke signUpInBackground
                    parseUser.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(i);
                                getFriends(loginResult);
                                finish();
                            } else {
                                Log.e("LoginActivity","Login failure");
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    private void getFriends(LoginResult loginResult) {
        GraphRequest friendsRequest = GraphRequest.newMyFriendsRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(
                            JSONArray jsonArray,
                            GraphResponse response) {
                        Log.d("FriendList", jsonArray.toString());
                        addFriends(jsonArray);
                    }
                });
        friendsRequest.executeAsync();
    }

    private void addFriends(JSONArray jsonArray) {
        ParseUser user = ParseUser.getCurrentUser();
        ArrayList<String> friends = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject object = jsonArray.getJSONObject(i);
                friends.add(object.getString("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        user.put("friendList", friends);
        user.saveInBackground();
    }
}
