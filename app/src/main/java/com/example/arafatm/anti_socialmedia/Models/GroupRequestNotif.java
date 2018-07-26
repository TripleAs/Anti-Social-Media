package com.example.arafatm.anti_socialmedia.Models;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class GroupRequestNotif extends ParseObject {

    ParseUser sender;
    ParseUser receiver;
    Group requestedGroup;
    Boolean isAccepted;
    ParseUser currentUser = ParseUser.getCurrentUser();

//    private String KEY_SENDER = "sender";
//    private String KEY_RECEIVER = "receiver";
//    private String KEY_REQUESTED_GROUP = "requestedGroup";
//    private String KEY_IS_ACCEPTED = "isAccepted";
//
//    public ParseUser getSender() { return getParseUser(KEY_SENDER); }
//
//    public void setSender(ParseUser sender) { put(KEY_SENDER, sender); };
//
//    public ParseUser getReceiver() { return getParseUser(KEY_RECEIVER); }
//
//    public void setReceiver(ParseUser receiver) { put(KEY_RECEIVER, receiver); }
//
//    public ParseObject getRequestedGroup() { return getParseObject(KEY_REQUESTED_GROUP); }
//
//    public void setRequestedGroup(Group group) { put(KEY_REQUESTED_GROUP, group); }
//
//    public void acceptRequest() {
//        isAccepted = true;
//        ParseObject group = getRequestedGroup();
//        group.approveUser(getReceiver());
//    }
//
//    public void rejectRequest() {
//        isAccepted = false;
//        ParseObject group = getRequestedGroup();
//        List<String> pending = group.getList("pending");
//        pending.remove(getReceiver().getObjectId());
//        group.put("pending", pending);
//    }

    public ParseUser getSender() { return sender; }

    public void setSender(ParseUser senderUser) { sender = senderUser; };

    public ParseUser getReceiver() { return receiver; }

    public void setReceiver(ParseUser receiverUser) { receiver = receiverUser; }

    public Group getRequestedGroup() { return requestedGroup; }

    public void setRequestedGroup(Group group) { requestedGroup = group; }

    public void acceptRequest() {
        isAccepted = true;
        Group group = getRequestedGroup();
        group.approveUser(getReceiver());
    }

    public void rejectRequest() {
        isAccepted = false;
        Group group = getRequestedGroup();
        List<String> pending = group.getPending();
        pending.remove(getReceiver().getObjectId());
        group.setPending(pending);
    }
}