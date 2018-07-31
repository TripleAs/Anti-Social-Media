package com.example.arafatm.anti_socialmedia.Authentification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.applozic.mobicomkit.contact.AppContactService;
import com.applozic.mobicommons.people.contact.Contact;
import com.example.arafatm.anti_socialmedia.Home.MainActivity;
import com.example.arafatm.anti_socialmedia.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.tvTitle) TextView title;
    @BindView(R.id.etUsername) EditText usernameInput;
    @BindView(R.id.etPassword) EditText passwordInput;
    @BindView(R.id.btLogin) Button parseLoginButton;
    @BindView(R.id.login_button) LoginButton loginButton;
    @BindView(R.id.btSwitchToSignup) Button signupButton;

    CallbackManager callbackManager;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        persistLogin();

        callbackManager = CallbackManager.Factory.create();
        context = getApplicationContext();

        //title
        title.setText("Anti-Social\nMedia");



        // Login via Parse
        parseLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = usernameInput.getText().toString();
                final String password = passwordInput.getText().toString();

                loginParse(username, password);
            }
        });

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

    private void loginParse(String username, String password){
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e == null ){          //if there's no errors
                    Log.d("LoginActivity", "Login successful!");

                    final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Unsuccessful", Toast.LENGTH_LONG).show();
                    Log.e("LoginActivity", "Login failure.");
                    e.printStackTrace();
                }
            }
        });
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
        addContacts(user, friends);
    }

    // adding local Applozic contacts so that contact tab can be prepopulated
    private void addContacts(ParseUser user, ArrayList<String> friendList) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereContainedIn("username", friendList);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        ParseObject currentFriend = objects.get(i);
                        String friendName = currentFriend.getString("fullName");
                        Log.d("weird", friendName);

                        Contact contact = new Contact();
                        contact.setUserId(friendName);
                        contact.setFullName(friendName);
                        contact.setEmailId(currentFriend.getString("email"));

                        String propicUrl = currentFriend.getString("propicUrl");
                        propicUrl = (propicUrl == null) ? currentFriend.getParseFile("profileImage").getUrl() : propicUrl;
                        contact.setImageURL(propicUrl);

                        Context context = getApplicationContext();
                        AppContactService appContactService = new AppContactService(context);
                        appContactService.add(contact);

                    }
                } else {
                    Log.e("weird", "Query error");
                }
            }
        });
    }
}
