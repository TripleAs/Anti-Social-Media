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
import com.example.arafatm.anti_socialmedia.Models.Group;
import com.example.arafatm.anti_socialmedia.Models.GroupRequestNotif;
import com.example.arafatm.anti_socialmedia.Models.Post;
import com.example.arafatm.anti_socialmedia.Models.Story;
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
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private TextView title;
    private EditText usernameInput;
    private EditText passwordInput;
    private Button parseLoginButton;
    private LoginButton loginButton;
    private Button signupButton;
  
    CallbackManager callbackManager;
    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
      
        title = findViewById(R.id.tvTitle);
        usernameInput = findViewById(R.id.etUsername);
        passwordInput = findViewById(R.id.etPassword);
        parseLoginButton = findViewById(R.id.btLogin);
        loginButton =  findViewById(R.id.login_button);
        signupButton = findViewById(R.id.btSwitchToSignup);
        callbackManager = CallbackManager.Factory.create();
        context = getApplicationContext();

        persistLogin();

        //title
        title.setText("anti-social media");

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
                    setUpLocalDatastore1();
                    startActivity(intent);
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

    private void setUpLocalDatastore1() {
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                ParseObject.pinAllInBackground("friends", objects, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.d("ParseLocalDataStore", "friends");
                    }
                });
            }
        });

        Group.Query groupQuery = new Group.Query();
        groupQuery.findInBackground(new FindCallback<Group>() {
            @Override
            public void done(List<Group> objects, ParseException e) {
                ParseObject.pinAllInBackground("groups", objects, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.d("ParseLocalDataStore", "groups");
                    }
                });
            }
        });

        GroupRequestNotif.Query notifQuery = new GroupRequestNotif.Query();
        notifQuery.findInBackground(new FindCallback<GroupRequestNotif>() {
            @Override
            public void done(List<GroupRequestNotif> objects, ParseException e) {
                ParseObject.pinAllInBackground("notifs", objects, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.d("ParseLocalDataStore", "notifs");
                    }
                });
            }
        });

        Post.Query postQuery = new Post.Query();
        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                ParseObject.pinAllInBackground("posts", objects, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.d("ParseLocalDataStore", "posts");
                    }
                });
            }
        });

        Story.Query storyQuery = new Story.Query();
        storyQuery.findInBackground(new FindCallback<Story>() {
            @Override
            public void done(List<Story> objects, ParseException e) {
                ParseObject.pinAllInBackground("stories", objects, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.d("ParseLocalDataStore", "stories");
                    }
                });
            }
        });
    }

    private void setUpLocalDatastore() {
        ParseUser user = ParseUser.getCurrentUser();
//        ParseQuery<ParseUser> friendsQuery = ParseUser.getQuery();
        ParseUser currentUser = ParseUser.getCurrentUser();
        //get the list of friends(Ids)
        List<String> friendListIds = currentUser.getList("friendList");

        // use usernames/FB Ids to find users
        ParseQuery<ParseUser> friendsQuery = ParseUser.getQuery();
        friendsQuery.whereContainedIn("username", friendListIds);
//        friendsQuery.whereContainedIn("username", user.getList("friendsList"));
        friendsQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                ParseObject.pinAllInBackground("friends", objects);
                Log.d("ParseLocalDataStore", "friends");
            }
        });

        saveGroupsToDatastore(user);

        GroupRequestNotif.Query notifQuery = new GroupRequestNotif.Query();
        notifQuery.whereEqualTo("receiver", user);
        notifQuery.findInBackground(new FindCallback<GroupRequestNotif>() {
            @Override
            public void done(List<GroupRequestNotif> objects, ParseException e) {
                ParseObject.pinAllInBackground("notifs", objects);
                Log.d("ParseLocalDataStore", "notifs");
            }
        });

        Story.Query storyQuery = new Story.Query();
        storyQuery.findInBackground(new FindCallback<Story>() {
            @Override
            public void done(List<Story> objects, ParseException e) {
                ParseObject.pinAllInBackground("stories", objects);
                Log.d("ParseLocalDataStore", "stories");
            }
        });
    }

    private void saveGroupsToDatastore(ParseUser user) {
        final List<Group> groups = user.getList("groups");
        if (groups != null) {
            ParseObject.pinAllInBackground("groups", groups, new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Log.d("ParseLocalDataStore", "groups");
                    savePostsToDatastore(groups);
                }
            });
        }
    }

    private void savePostsToDatastore(List<Group> groups) {
        for (int i = 0; i < groups.size(); i++) {
            Group group = groups.get(i);
            group.fetchFromLocalDatastoreInBackground(new GetCallback<Group>() {
                @Override
                public void done(Group object, ParseException e) {
                    final List<Post> posts = object.getPosts();
                    if (posts != null) {
                        ParseObject.pinAllInBackground("posts", posts, new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Log.d("ParseLocalDataStore", "posts");
                                saveCommentsToDatastore(posts);
                            }
                        });
                    }
                }
            });
        }
    }

    private void saveCommentsToDatastore(List<Post> posts) {
        for (int j = 0; j < posts.size(); j++) {
            Post post = posts.get(j);
            post.fetchFromLocalDatastoreInBackground(new GetCallback<Post>() {
                @Override
                public void done(Post object, ParseException e) {
                    if (object != null) {
                        List<Post> comments = object.getComments();
                        if (comments != null) {
                            ParseObject.pinAllInBackground("comments", comments);
                            Log.d("ParseLocalDataStore", "comments");
                        }
                    }
                }
            });
        }
    }
}
