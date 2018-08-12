package com.example.arafatm.anti_socialmedia.Util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.arafatm.anti_socialmedia.Fragments.GroupFeedFragment;
import com.example.arafatm.anti_socialmedia.Models.Group;
import com.example.arafatm.anti_socialmedia.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class GroupManagerAdapter extends RecyclerView.Adapter<GroupManagerAdapter.ViewHolder> {
    private Context context;
    public ArrayList<Group> groups;
    private ArrayList<ParseUser> memberList;
    private FragmentManager fragmentManager;
    public int currentGroupPosition;

    public GroupManagerAdapter(ArrayList<Group> List, FragmentManager fm) {
        groups = List;
        fragmentManager = fm;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View groupView = inflater.inflate(R.layout.item_group_manager, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(groupView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Group group = groups.get(position);
        String groupName = group.getGroupName();

        if (groupName != null) {
            viewHolder.tvGroupName.setText(groupName);
        } else {
            viewHolder.tvGroupName.setText("");
        }

        List<String> groupMembers = group.getUsers();
        if(groupMembers != null){
           getMemberNames(groupMembers, viewHolder.tvGroupMembers);
        } else {
            viewHolder.tvGroupMembers.setText("");
        }

        ParseFile groupPic = group.getParseFile("groupImage");
        if (groupPic != null) {
            Glide.with(context)
                    .load(groupPic.getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(viewHolder.ivCoverPhoto);
        } else {
            viewHolder.ivCoverPhoto.setImageResource(R.drawable.ic_group_default);
        }
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvGroupName;
        public TextView tvGroupMembers;
        public ImageView ivCoverPhoto;

        public ViewHolder(View view) {
            super(view);
            tvGroupName = view.findViewById(R.id.tvGroupName);
            tvGroupMembers = view.findViewById(R.id.tvGroupMembers);
            ivCoverPhoto = view.findViewById(R.id.ivCoverPhoto);
            view.setOnClickListener(this);
        }

        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                currentGroupPosition = position;
                try {
                    Group currentGroup = groups.get(position).fetchIfNeeded();
                    Fragment fragment = GroupFeedFragment.newInstance(currentGroup.getObjectId(), currentGroup.getTheme());
                    fragmentManager.beginTransaction().replace(R.id.layout_child_activity, fragment).addToBackStack(null).commit();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void getMemberNames(final List<String> groupMembers, final TextView groupMemberNames){
        final int size = groupMembers.size();
        final List<String> names = new ArrayList<>();

        ParseQuery<ParseUser> membersQuery = ParseUser.getQuery();
        membersQuery.whereContainedIn("objectId", groupMembers);
//        membersQuery.fromLocalDatastore();

        membersQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < size; i++){
                        String index = objects.get(i).getString("fullName");
                        names.add(i, index);
                        groupMemberNames.setText(TextUtils.join(", ", names));
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private String getInitials(String name) {
        String initials = "";
        for (String s : name.split(" ")) {
            initials += Character.toUpperCase(s.charAt(0));
        }
        return initials;
    }
}
