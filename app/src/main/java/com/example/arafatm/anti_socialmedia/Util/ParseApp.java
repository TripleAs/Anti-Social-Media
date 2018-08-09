package com.example.arafatm.anti_socialmedia.Util;

import android.app.Application;

import com.example.arafatm.anti_socialmedia.Models.Group;
import com.example.arafatm.anti_socialmedia.Models.GroupRequestNotif;
import com.example.arafatm.anti_socialmedia.Models.Post;
import com.example.arafatm.anti_socialmedia.Models.Story;
import com.parse.Parse;
import com.parse.ParseObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Use for monitoring Parse OkHttp traffic
        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        // See http://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);

        /*register each of the model classes*/
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Group.class);
        ParseObject.registerSubclass(Story.class);
        ParseObject.registerSubclass(GroupRequestNotif.class);

        /*Setting up parse*/
        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("triple_as_lovely_app") // takes in App ID
                .clientKey("amy_alison_arafat_are_triple_as") //takes in App Master Key
                .server("http://anti-social-media.herokuapp.com/parse") // takes in Parse URL
                .enableLocalDataStore()
                .build();

        Parse.initialize(configuration); //initializing parse
    }
}
