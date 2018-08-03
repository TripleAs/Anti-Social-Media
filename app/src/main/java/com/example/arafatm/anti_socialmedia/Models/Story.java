package com.example.arafatm.anti_socialmedia.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Story")
public class Story extends ParseObject {
    private static final String KEY_SENDER = "sender";
    private static final String KEY_RECIPIENT = "recipient";
    private static final String KEY_STORY = "story";
    private static final String KEY_STORYTYPE = "storyType";
    private static final String KEY_STORYCAPTION = "caption";
    private static final String KEY_STORYTEXT = "text";


    public ParseUser getSender() {
        return getParseUser(KEY_SENDER);
    }

    public void setSender(ParseUser sender) {
        put(KEY_SENDER, sender);
    }

    public List<String> getAllRecipient() {
        return getList(KEY_RECIPIENT);
    }

    public void addRecipient(String recipientObjectId) {
        List<String> allRecipient = getList(KEY_RECIPIENT);
        if (allRecipient == null) {
            List<String> allRecipientNew = new ArrayList<>();
            allRecipientNew.add(recipientObjectId);
            put(KEY_RECIPIENT, allRecipientNew);
            return;
        }
        allRecipient.add(recipientObjectId);
        put(KEY_RECIPIENT, allRecipient);
    }

    /*Sets the story type to either a video or a picture*/
    public void setStoryType(String dataType) {
        put(KEY_STORYTYPE, dataType);
    }

    /*Gets the story type*/
    public String getStoryType() {
        return getString(KEY_STORYTYPE);
    }

    /*Gets the Array of stories from Parse, updates it, and save it back to parse*/
    public void setStory(ParseFile file) {
        put(KEY_STORY, file);
    }

    /*Gets the Array of stories from Parse, updates it, and save it back to parse*/
    public ParseFile getStory() {
        return getParseFile(KEY_STORY);
    }

    public void setStoryCaption(String storyCaption) {
        put(KEY_STORYCAPTION, storyCaption);
    }

    public String getStoryCaption() {
       return getString(KEY_STORYCAPTION);
    }

    public void setStoryText(String storyCaption) {
        put(KEY_STORYCAPTION, storyCaption);
    }

    public String getStoryText() {
        return  getString(KEY_STORYTEXT);
    }

    public static class Query extends ParseQuery<Story> {
        //Query of a post class

        public Query() {
            super(Story.class);
        }

        public Story.Query getTop() {
            setLimit(20);
            return this;
        }

        public Story.Query withUser() {
            include("User");
            return this;
        }
    }

}
