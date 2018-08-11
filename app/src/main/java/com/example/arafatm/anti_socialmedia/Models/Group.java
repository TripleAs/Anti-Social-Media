package com.example.arafatm.anti_socialmedia.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

@ParseClassName("Group")
public class Group extends ParseObject {
    private static final String KEY_USERS = "users"; //Name of user column in parse
    private static final String KEY_POSTS = "post"; //Name of post column in parse
    private static final String KEY_NAME = "groupName"; //Name of group name column in parse
    private static final String KEY_IMAGE = "groupImage"; //Name of group image column in parse
    private static final String KEY_PENDING = "pending"; //Name of request state column in parse
    private static final String KEY_THEME = "theme"; //Name of theme column in parse
    private static final String KEY_NICKNAMES = "nicknames"; //Name of nickname column in parse

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
    public List<String> getUsers() {
        return getList(KEY_USERS);
    }

    public List<ParseUser> getParseUser() { return getList(KEY_USERS); }

    /*Gets the Array of users from Parse, updates it, and save it back to parse*/
    public void setUsers(List<String> users) {
        put(KEY_USERS, users);
    }

    public List<String> getPending() {
        return getList(KEY_PENDING);
    }

    public void setPending(List<String> requests) {
        put(KEY_PENDING, requests);
    }

    public List<Post> getPosts() {
        return getList(KEY_POSTS);
    }

    public String getTheme() {
        return getString(KEY_THEME);
    }

    public void setTheme(String theme) {
        put(KEY_THEME, theme);
    }

    public String getNicknames() {
        return getString(KEY_NICKNAMES);
    }

    /*Gets the Array of posts from Parse, updates it, and save it back to parse*/
    public void addPost(Post post) {
        List<Post> posts = getPosts();
        if (posts == null) {
            posts = new ArrayList<>();
        }
        posts.add(post);
        put(KEY_POSTS, posts);
    }

    public HashMap<String, String> getNicknamesDict() {
        String nicknamesString = getNicknames();
        if (nicknamesString != null) {
            return convertToStringToHashMap(nicknamesString);
        } else {
            return new HashMap<>();
        }
    }

    public void setNicknamesDict(HashMap<String, String> updatedDict) {
        put(KEY_NICKNAMES, updatedDict.toString());
    }

    public void initGroup(String name, List<String> requests, ParseFile image, String theme) {
        setGroupName(name);
        setPending(requests);
        setGroupImage(image);
        setTheme(theme);
        ArrayList<String> approved = new ArrayList<String>();
        approved.add(ParseUser.getCurrentUser().getObjectId());
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
    }

    private HashMap<String, String> convertToStringToHashMap(String text) {
        HashMap<String, String> data = new HashMap<String, String>();
        Pattern p = Pattern.compile("[\\{\\}\\=\\, ]++");
        String[] split = p.split(text);
        for (int i = 1; i + 2 <= split.length; i += 2) {
            data.put(split[i], split[i + 1]);
        }
        return data;
    }
}
