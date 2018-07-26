package com.example.arafatm.anti_socialmedia.Util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.arafatm.anti_socialmedia.R;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.ArrayList;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> implements View.OnClickListener {
    private Context mContext;
    private ArrayList<ParseObject> groupList;

    public GroupListAdapter(Context context, ArrayList<ParseObject> List) {
        mContext = context;
        this.groupList = List;
    }


    @Override
    public GroupListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        // Inflate the custom layout
        View friendListView = inflater.inflate(R.layout.grouplist_item, viewGroup, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(friendListView);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        final ParseObject group = groupList.get(position);

        // Set item views based on your views and data model
        viewHolder.groupName.setText(group.getString("groupName"));

        ParseFile file = group.getParseFile("groupImage"); //verify this
        if (file != null) {
            Glide.with(mContext)
                    .load(file.getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(viewHolder.groupPic);
        }

        final RadioButton addFriendButton = viewHolder.addButton;
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Toast.makeText(mContext, "Story added to group", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    @Override
    public void onClick(View view) {

    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView groupName;
        public RadioButton addButton;
        public ImageView groupPic;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            groupName = (TextView) itemView.findViewById(R.id.tvGroupName);
            addButton = (RadioButton) itemView.findViewById(R.id.rd_selected);
            groupPic = (ImageView) itemView.findViewById(R.id.ivGroupPic);
        }
    }
}
