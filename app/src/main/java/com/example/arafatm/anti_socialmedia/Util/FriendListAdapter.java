package com.example.arafatm.anti_socialmedia.Util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.arafatm.anti_socialmedia.Fragments.ProfileFragment;
import com.example.arafatm.anti_socialmedia.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class  FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {
    public List<ParseUser> allFriends;
    private Context context;
    private ArrayList<String> newGroupMembers;

    // Pass in the contact array into the constructor
    public FriendListAdapter(List<ParseUser> allFriends) {
        this.allFriends = allFriends;
        newGroupMembers = new ArrayList<>();
    }

    @Override
    public FriendListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
         context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View friendListView = inflater.inflate(R.layout.item_friend, viewGroup, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(friendListView);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        final ParseUser friend = allFriends.get(position);

        // Set item views based on your views and data model
       viewHolder.friendName.setText(friend.getString("fullName"));

        ParseFile file = friend.getParseFile("profileImage"); //verify this
        if (file != null) {
            Glide.with(context)
                    .load(file.getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(viewHolder.friendPic);
        }
    }

    @Override
    public int getItemCount() {
        return allFriends.size();
    }

    public ArrayList<String> getNewGroupMembers() {
        return newGroupMembers;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView friendName;
        public ImageView addButton;
        public ImageView friendPic;
        private boolean Added = false;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            friendName = (TextView) itemView.findViewById(R.id.tvGroupName);
            addButton = (ImageView) itemView.findViewById(R.id.ivAddButton);
            friendPic = (ImageView) itemView.findViewById(R.id.ivGroupPic);

            itemView.setOnClickListener(this);
            friendName.setOnClickListener(this);
            addButton.setOnClickListener(this);
            friendPic.setOnClickListener(this);
        }

        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                ParseUser friend = allFriends.get(position);
                if (view.getId() == friendName.getId() || view.getId() == friendPic.getId()) {
                    openProfile(friend);
                } else if (view.getId() == addButton.getId()) {
                    if (Added == true) { //when the user unselects a friend
                        addButton.setImageResource(R.drawable.ic_add_icon);
                        //remove that user's ObjectId from the newGroupMembers list
                        newGroupMembers.remove(friend.getObjectId());
                        Added = false;
                        Toast.makeText(context,friend.getUsername()+" removed", Toast.LENGTH_SHORT).show();
                    } else {
                        addButton.setImageResource(R.drawable.ic_check_mark);
                        //add that user's ObjectId to the newGroupMembers list to be accessed later
                        newGroupMembers.add(friend.getObjectId());
                        Added = true;
                        Toast.makeText(context,friend.getUsername()+" added", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void openProfile(ParseUser friend) {
        ProfileFragment profileFragment = ProfileFragment.newInstance(friend);
        ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                .replace(R.id.layout_child_activity, profileFragment)
                .commit();
    }
}
