package com.example.arafatm.anti_socialmedia.Authentification;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
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
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.parse.FindCallback;
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
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    Boolean friendsDone = false;
    Boolean groupsDone = false;
    Boolean postsDone = false;
    Boolean storiesDone = false;
    Boolean notifsDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        AccessToken accessToken = Parcels.unwrap(intent.getExtras().getParcelable("accessToken"));
        requestFBInfo(accessToken);
    }

    private void requestFBInfo(final AccessToken accessToken) {
        // define request for Facebook user's information
        GraphRequest meRequest = GraphRequest.newMeRequest(
                accessToken,
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
                            loginOrSignup(userId, fullname, email, propicUrl, accessToken);
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
    }

    private void loginOrSignup(final String userId, final String fullname, final String email,
                               final String propicUrl, final AccessToken accessToken) {
        ParseUser.logInInBackground(userId, userId, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Log.d("LoginActivity", "Login successful");
                    setUpLocalDatastore();
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
                                getFriends(accessToken);
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

    private void getFriends(AccessToken accessToken) {
        GraphRequest friendsRequest = GraphRequest.newMyFriendsRequest(
                accessToken,
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
        setUpLocalDatastore();
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

    private void setUpLocalDatastore() {
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                ParseObject.pinAllInBackground("friends", objects, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.d("ParseLocalDataStore", "friends");
                        trackingSetupProgress("friends");
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
                        trackingSetupProgress("groups");
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
                        trackingSetupProgress("notifs");
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
                        trackingSetupProgress("posts");
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
                        trackingSetupProgress("stories");
                        Log.d("ParseLocalDataStore", "stories");
                    }
                });
            }
        });
    }

    private void trackingSetupProgress(String model) {
        switch (model) {
            case "friends":
                friendsDone = true;
                break;
            case "groups":
                groupsDone = true;
                break;
            case "notifs":
                notifsDone = true;
                break;
            case "posts":
                postsDone = true;
                break;
            case "stories":
                storiesDone = true;
                break;
        }

        if (friendsDone && groupsDone && notifsDone && postsDone && storiesDone) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
