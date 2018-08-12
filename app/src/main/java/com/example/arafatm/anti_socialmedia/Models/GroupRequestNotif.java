package com.example.arafatm.anti_socialmedia.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("GroupRequestNotif")
public class GroupRequestNotif extends ParseObject {
    private String KEY_SENDER = "sender";
    private String KEY_RECEIVER = "receiver";
    private String KEY_REQUESTED_GROUP = "requestedGroup";
    private String KEY_SEEN = "isSeen";

    public ParseUser getSender() {
        return getParseUser(KEY_SENDER);
    }

    public void setSender(ParseUser sender) {
        put(KEY_SENDER, sender);
    }

    ;

    public ParseUser getReceiver() {
        return getParseUser(KEY_RECEIVER);
    }

    public void setReceiver(ParseUser receiver) {
        put(KEY_RECEIVER, receiver);
    }

    public ParseObject getRequestedGroup() {
        return getParseObject(KEY_REQUESTED_GROUP);
    }

    public void setRequestedGroup(Group group) {
        put(KEY_REQUESTED_GROUP, group);
    }

    public void initRequest(ParseUser receiver, Group group) {
        setReceiver(receiver);
        setSender(ParseUser.getCurrentUser());
        setRequestedGroup(group);
        put(KEY_SEEN, false);
    }

    public void acceptRequest() {
        put(KEY_SEEN, true);
        saveInBackground();
        ParseObject group = getRequestedGroup();
        String userId = getReceiver().getObjectId();
        List<String> approved = group.getList("users");
        List<String> pending = group.getList("pending");
        approved.add(userId);
        pending.remove(userId);
        group.put("users", approved);
        group.put("pending", pending);
        group.saveInBackground();
    }

    public void rejectRequest() {
        put(KEY_SEEN, true);
        saveInBackground();
        ParseObject group = getRequestedGroup();
        List<String> pending = group.getList("pending");
        pending.remove(getReceiver().getObjectId());
        group.put("pending", pending);
        group.saveInBackground();
    }

    public static class Query extends ParseQuery {
        public Query() {
            super(GroupRequestNotif.class);
        }

        public GroupRequestNotif.Query withAll() {
            include("requestedGroup").include("receiver").include("sender").include("isSeen");
            return this;
        }

        public GroupRequestNotif.Query getInvitesReceived(ParseUser user) {
            whereEqualTo("receiver", user).whereEqualTo("isSeen", false);
            return this;
        }
    }
}