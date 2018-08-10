package com.example.arafatm.anti_socialmedia.Util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
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
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.ArrayList;
import java.util.List;

public class GroupManagerAdapter extends RecyclerView.Adapter<GroupManagerAdapter.ViewHolder> {
    private Context context;
    public ArrayList<Group> groups;
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
            String listOfMembers = "";
            String numberOfMembers = "";
            int size = groupMembers.size();

            /** for (int i = 0; i < size; i++){  //For whatever reason, I can't get fullname bc it's a string, not a parseUser
                String something = groupMembers.get(i);
                String memberName = something;              //.getString("fullName");
                listOfMembers +=  memberName;
                if (i != groupMembers.size()-1){
                    listOfMembers += ", ";
                }
            } TODO: note- I tried to do fullnames of members, but it would only do objectid. We'd need to query if we'd want this function. Unnecessary?
             viewHolder.tvGroupMembers.setText(listOfMembers); **/

            if (size > 0){
                numberOfMembers = size + " other member";
                if (size > 1){
                    numberOfMembers += ("s");
                }
            }
            viewHolder.tvGroupMembers.setText(numberOfMembers);

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

    private String getInitials(String name) {
        String initials = "";
        for (String s : name.split(" ")) {
            initials += Character.toUpperCase(s.charAt(0));
        }
        return initials;
    }
}
