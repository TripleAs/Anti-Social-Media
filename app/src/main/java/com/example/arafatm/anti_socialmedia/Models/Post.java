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
    private static final String KEY_COMMENT = "comment";
    private static final String KEY_LIKES = "likes";


    public String getMessage() {
        return getString(KEY_MESSAGE);
    }

    public String getComment() {
        return getString(KEY_COMMENT);
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
        if (getParseUser(KEY_SENDER) == null) return null;
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

    public static class Query extends ParseQuery<Post> {
        //Query of a post class
        public Query(){
            super(Post.class);
        }

        public Query getTop(){
            whereNotEqualTo("message", null);
            orderByDescending("createdAt");
            setLimit(20);
            return this;
        }

        public Query withUser(){
            include("User");
            return this;
        }

        public Query forGroup(Group group) {
            whereEqualTo("recipient", group);
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

    public List<String> getLikes() { return getList(KEY_LIKES); }
    public void setLikes(List<String> likes) { put(KEY_LIKES, likes); }

    public void toggleLikes(ParseUser user) {
        List<String> likes = getLikes();
        String objectId = user.getObjectId();
        if (likes == null) {
            likes = new ArrayList<>();
        }
        if (likes.contains(objectId)) {
            likes.remove(objectId);
            setLikes(likes);
        } else {
            likes.add(objectId);
            setLikes(likes);
        }
    }
}
