package com.example.arafatm.anti_socialmedia.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("Group")
public class Group extends ParseObject {
    private static final String KEY_USERS = "users"; //Name of user column in parse
    private static final String KEY_POSTS = "post"; //Name of post column in parse
    private static final String KEY_NAME = "groupName"; //Name of group name column in parse
    private static final String KEY_IMAGE = "groupImage"; //Name of group image column in parse

    public String getGroupName() {
        return getString(KEY_NAME);
    }

    public void setGroupName(String name) {
        put(KEY_NAME, name);
    }

    public ParseFile getGroupImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setGroupImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    //gets list all users
    public List<ParseUser> getUsers() {
        return getList(KEY_USERS);
    }

    /*Gets the Array of users from Parse, updates it, and save it back to parse*/
    public void addUsers(ParseUser user) {
        List<ParseUser> users = getList(KEY_USERS);
        users.add(user);
        put(KEY_USERS, users);
    }

    //gets list all posts
    public List<ParseObject> getPosts() {
        return getList(KEY_POSTS);
    }

    /*Gets the Array of posts from Parse, updates it, and save it back to parse*/
    public void addPosts(Post post) {
        List<ParseObject> users = getList(KEY_POSTS);
        users.add(post);
        put(KEY_POSTS, users);
    }

    public static class Query extends ParseQuery {
        public Query() {
            super(Group.class);
        }

//        public Query getTop() {
//            setLimit(20);
//            return this;
//        }

//        public Query withUser() {
//            include("user");
//            return this;
//        }
//
//        public Query getPostsForUser(ParseUser user) {
//            whereEqualTo("user", user);
//            return this;
//        }
    }
}
