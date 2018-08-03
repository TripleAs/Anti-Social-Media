package com.example.arafatm.anti_socialmedia.Models;

import org.json.JSONException;
import org.json.JSONObject;

public class InstaPost {

    public String url;        //database ID for the ig post
    public String caption;
    public String userHandle;

    public String createdAt;    //this is really weird on JSON, so not going to include
    public int numberOfLikes;   //nested, so this might be different

    public InstaPost(){
        //constructor
    }

    public static InstaPost fromJSON(JSONObject jsonObject) throws JSONException{
        InstaPost instaPost = new InstaPost();

        instaPost.url = jsonObject.getString("url");
        instaPost.caption = jsonObject.getString("text");
        instaPost.userHandle = "@"+jsonObject.getString("username");
//        instaPost.numberOfLikes = jsonObject.getInt("count");

        return instaPost;
    }

}
