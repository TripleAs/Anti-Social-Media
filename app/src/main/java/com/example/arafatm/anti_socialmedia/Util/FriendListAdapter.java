package com.example.arafatm.anti_socialmedia.Util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.arafatm.anti_socialmedia.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {
    public List<ParseUser> allFriends;
    private Context context;

    // Pass in the contact array into the constructor
    public FriendListAdapter(List<ParseUser> allFriends) {
        this.allFriends = allFriends;
    }

    @Override
    public FriendListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
         context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_friend, viewGroup, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        // Get the data model based on position
        final ParseUser friend = allFriends.get(position);

        // Set item views based on your views and data model
       viewHolder.friendName.setText(friend.getUsername());

        ParseFile file = friend.getParseFile("image"); //verify this
        if (file != null) {
            Glide.with(context)
                    .load(file.getUrl())
                    .into(viewHolder.friendPic);
        }


        final ImageView addFriendButton =  viewHolder.addButton;
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,friend.getUsername()+" added", Toast.LENGTH_SHORT).show();
                addFriendButton.setImageResource(R.drawable.ic_check_mark);

                //update the database
            }
        });
    }

    @Override
    public int getItemCount() {
        return allFriends.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView friendName;
        public ImageView addButton;
        public ImageView friendPic;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            friendName = (TextView) itemView.findViewById(R.id.tvFullName);
            addButton = (ImageView) itemView.findViewById(R.id.ivAddButton);
            friendPic = (ImageView) itemView.findViewById(R.id.ivPropic);
        }
    }
}
