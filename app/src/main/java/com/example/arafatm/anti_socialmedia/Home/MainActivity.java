package com.example.arafatm.anti_socialmedia.Home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.account.user.PushNotificationTask;
import com.applozic.mobicomkit.api.account.user.User;
import com.applozic.mobicomkit.api.account.user.UserLoginTask;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.example.arafatm.anti_socialmedia.Fragments.ChatFragment;
import com.example.arafatm.anti_socialmedia.Fragments.GameFragment;
import com.example.arafatm.anti_socialmedia.Fragments.GroupCreationFragment;
import com.example.arafatm.anti_socialmedia.Fragments.GroupFeedFragment;
import com.example.arafatm.anti_socialmedia.Fragments.GroupManagerFragment;
import com.example.arafatm.anti_socialmedia.Fragments.SettingsFragment;
import com.example.arafatm.anti_socialmedia.R;
import com.parse.ParseUser;


public class MainActivity extends AppCompatActivity implements ChatFragment.OnFragmentInteractionListener,
        GroupManagerFragment.OnFragmentInteractionListener,
        GameFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener, GroupCreationFragment.OnFragmentInteractionListener,
        GroupFeedFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);//Initiates BottomNavigationView

        final FragmentManager fragmentManager = getSupportFragmentManager(); //Initiates FragmentManager

        /*gets instance of all fragments here*/

        final Fragment chatFragment = new ChatFragment();
        final Fragment groupFragment = new GroupManagerFragment();
        final Fragment gameFragment = new GameFragment();
        final Fragment settingsFragment = new SettingsFragment();

        // handle navigation selection to various fragments
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.ic_chat_empty:
                                FragmentTransaction fragmentTransactionOne = fragmentManager.beginTransaction(); //creates new transaction
                                fragmentTransactionOne.replace(R.id.fragment_container, chatFragment).commit(); //replace the current page with the specified fragment
                                return true;
                            case R.id.ic_group_empty:
                                FragmentTransaction fragmentTransactionTwo = fragmentManager.beginTransaction();
                                fragmentTransactionTwo.replace(R.id.fragment_container, groupFragment).commit();
                                return true;
                            case R.id.ic_game_empty:
                                FragmentTransaction fragmentTransactionThree = fragmentManager.beginTransaction();
                                fragmentTransactionThree.replace(R.id.fragment_container, gameFragment).commit();
                                return true;
                            case R.id.ic_menu_thin:
                                FragmentTransaction fragmentTransactionFour = fragmentManager.beginTransaction();
                                fragmentTransactionFour.replace(R.id.fragment_container, settingsFragment).commit();
                                return true;

                            default:
                                return false;
                        }
                    }
                });


        UserLoginTask.TaskListener listener = new UserLoginTask.TaskListener() {

            @Override
            public void onSuccess(RegistrationResponse registrationResponse, Context context) {
                Intent intent = new Intent(MainActivity.this, ConversationActivity.class);
                intent.putExtra(ConversationUIService.USER_ID, "receiveruserid123");
                intent.putExtra(ConversationUIService.DISPLAY_NAME, "Friend McFrienderson"); //put it for displaying the title.
                intent.putExtra(ConversationUIService.TAKE_ORDER,false); //Skip chat list for showing on back press
                startActivity(intent);
            }

            @Override
            public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                // If any failure in registration the callback  will come here
            }};

        ParseUser parseUser = ParseUser.getCurrentUser();
        String userId = parseUser.getObjectId();
        String displayName = parseUser.getString("fullName");
        String email = parseUser.getEmail();

        User user = new User();
        user.setUserId(userId); //userId it can be any unique user identifier
        user.setDisplayName(displayName); //displayName is the name of the user which will be shown in chat messages
        user.setEmail(email); //optional
        user.setAuthenticationTypeId(User.AuthenticationType.APPLOZIC.getValue());
        //User.AuthenticationType.APPLOZIC.getValue() for password verification from Applozic server
        // and User.AuthenticationType.CLIENT.getValue() for access Token verification from your server
        // set access token as password
        user.setPassword(""); //optional, leave it blank for testing purpose,
        // read this if you want to add additional security by verifying password from your server
        // https://www.applozic.com/docs/configuration.html#access-token-url
//        user.setImageLink("");//optional,pass your image link
        new UserLoginTask(user, listener, this).execute((Void) null);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    /*Navigates to the groupManagerFragment*/
    public void navigate_to_fragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
