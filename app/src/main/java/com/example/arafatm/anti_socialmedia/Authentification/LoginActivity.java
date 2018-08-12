package com.example.arafatm.anti_socialmedia.Authentification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.example.arafatm.anti_socialmedia.Home.MainActivity;
import com.example.arafatm.anti_socialmedia.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    private LoginButton loginButton;

    CallbackManager callbackManager;
    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        loginButton = findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();
        context = getApplicationContext();

        persistLogin();

        loginButton.setReadPermissions(Arrays.asList(
                "user_friends", "public_profile", "email"));

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
                intent.putExtra("accessToken", Parcels.wrap(loginResult.getAccessToken()));
                startActivity(intent);
                finish();
//                requestFBInfo(loginResult);
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
}