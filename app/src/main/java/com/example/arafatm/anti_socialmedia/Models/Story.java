package com.example.arafatm.anti_socialmedia.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("Story")
public class Story extends ParseObject {
    private static final String KEY_SENDER = "sender";
    private static final String KEY_RECIPIENT = "recipient";
    private static final String KEY_STORY = "story";


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
    allRecipient.add(recipientObjectId);
    put(KEY_RECIPIENT, allRecipient);
    }

    /*Gets the Array of stories from Parse, updates it, and save it back to parse*/
    public void setStory(ParseFile file) {
        put(KEY_STORY, file);
    }
}
