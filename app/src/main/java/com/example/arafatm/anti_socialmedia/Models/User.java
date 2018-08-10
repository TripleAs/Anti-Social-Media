package com.example.arafatm.anti_socialmedia.Models;

import com.parse.ParseUser;

public class User extends ParseUser {
    private static final String KEY_NAME = "fullName";

    public String getUserFullName() {
        return getString(KEY_NAME);
    }

    //Looks like we need to query in order to get the full names. Do we really want this?

}
