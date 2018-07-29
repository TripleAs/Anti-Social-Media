package com.example.arafatm.anti_socialmedia.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Group")
public class Group extends ParseObject {
    private static final String KEY_USERS = "users"; //Name of user column in parse
    private static final String KEY_POSTS = "post"; //Name of post column in parse
    private static final String KEY_NAME = "groupName"; //Name of group name column in parse
    private static final String KEY_IMAGE = "groupImage"; //Name of group image column in parse
    private static final String KEY_STORIES = "groupStory"; //Name of group story column in parse
    private static final String KEY_PENDING = "pending";
    private static final String KEY_THEME = "theme";

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
    public List<ParseUser> getUsers() { return getList(KEY_USERS); }

    /*Gets the Array of users from Parse, updates it, and save it back to parse*/
    public void setUsers(List<ParseUser> users) {
        put(KEY_USERS, users);
    }

    public List<ParseUser> getPending() {
        return getList(KEY_PENDING);
    }

    public void setPending(List<ParseUser> requests) {
        put(KEY_PENDING, requests);
    }

    public List<Post> getPosts() {
        return getList(KEY_POSTS);
    }

    public String getTheme() { return getString(KEY_THEME); }

    /*Gets the Array of posts from Parse, updates it, and save it back to parse*/
    public void addPost(Post post) {
        List<Post> posts = getPosts();
        if (posts == null) {
            posts = new ArrayList<Post>();
        }
        posts.add(post);
        put(KEY_POSTS, posts);
    }

    public void approveUser(ParseUser user) {
        List<ParseUser> approved = getUsers();
        List<ParseUser> pending = getPending();
        approved.add(user);
        pending.remove(user);
    }

    public void initGroup(String name, List<ParseUser> requests, ParseFile image) {
        setGroupName(name);
        setPending(requests);
        setGroupImage(image);
        ArrayList<ParseUser> approved = new ArrayList<ParseUser>();
        approved.add(ParseUser.getCurrentUser());
        setUsers(approved);
    }

    public static class Query extends ParseQuery {
        public Query() {
            super(Group.class);
        }

        public Query getTop() {
            setLimit(20);
            return this;
        }

        public Query withUser() {
            include("user");
            return this;
        }

        public Query getGroupForUser(ParseUser user) {
            whereEqualTo("user", user);
            return this;
        }
    }
}
