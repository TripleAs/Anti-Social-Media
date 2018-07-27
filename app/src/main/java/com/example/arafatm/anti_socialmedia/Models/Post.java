package com.example.arafatm.anti_socialmedia.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ParseClassName("Post")
public class Post extends ParseObject {
    private static final String KEY_SENDER = "sender";
    private static final String KEY_PROPIC = "profileImage";
    private static final String KEY_RECIPIENT = "recipient";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_CREATEDAT = "createdAt";
    private static final String KEY_MEDIA = "media";
    private static final String KEY_COMMENTS = "comments";

    public String getMessage() {
        return getString(KEY_MESSAGE);
    }

    public void setMessage(String message) {
        put(KEY_MESSAGE, message);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_MEDIA);
    }

    public void setImage(ParseFile image) {
        put(KEY_MEDIA, image);
    }


    public ParseUser getSender() {
        return getParseUser(KEY_SENDER);
    }

    public void setUser(ParseUser sender) {
        put(KEY_SENDER, sender);
    }

    public ParseObject getRecipient() {
        return getParseObject(KEY_RECIPIENT);
    }

    public void setRecipient(Group recipient) {
        put(KEY_RECIPIENT, recipient);
    }

    public Date getCreatedAt() {
        return getDate(KEY_CREATEDAT);
    }

    public void initPost(String message, Group group) {
        setMessage(message);
        setUser(ParseUser.getCurrentUser());
        setRecipient(group);
        group.addPost(this);
    }

    /*Gets the Array of comments from Parse, updates it, and save it back to parse*/
    public void addComments(Post comment) {
        List<ParseObject> comments = getList(KEY_COMMENTS);
        comments.add(comment);
        put(KEY_COMMENTS, comments);
    }

    public static class Query extends ParseQuery<Post> {
        //Query of a post class

        public Query(){
            super(Post.class);
        }

        public Query getTop(){
            orderByDescending("createdAt");
            setLimit(20);
            return this;
        }

        public Query withUser(){
            include("User");
            return this;
        }

    }

    // Functions for commenting
    public ArrayList<Post> getComments(){
        List<Post> commentsList = getList("comments");
        ArrayList<Post> comments = new ArrayList<Post>();
        if(commentsList != null){
            comments.addAll(commentsList);
        }
        return comments;
    }
    public void setComments(List<Post> comments){
        put("comments", comments);
        saveInBackground();
    }

    public void setCommentString(String parseFile){
        put("comment", parseFile);
    }
    public int getCommentsCount(){
        return getComments().size();
    }
}
