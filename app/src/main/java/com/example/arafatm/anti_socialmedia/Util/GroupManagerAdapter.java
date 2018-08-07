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
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.arafatm.anti_socialmedia.Fragments.GroupFeedFragment;
import com.example.arafatm.anti_socialmedia.Models.Group;
import com.example.arafatm.anti_socialmedia.R;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.ArrayList;

public class GroupManagerAdapter extends RecyclerView.Adapter<GroupManagerAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Group> groups;
    private FragmentManager fragmentManager;

    public GroupManagerAdapter(ArrayList<Group> List, FragmentManager fm) {
        this.groups = List;
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
            String initials = getInitials(groupName);
            viewHolder.tvGroupName.setText(initials);
        } else {
            viewHolder.tvGroupName.setText("");
        }

        ParseFile groupPic = group.getParseFile("groupImage");
        if (groupPic != null) {
            Glide.with(context)
                    .load(groupPic.getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(viewHolder.ivCoverPhoto);
//            Glide.with(context)
//                    .load(groupPic.getUrl())
//                    .apply(new RequestOptions().transforms(new CircleCrop(), new Blur))
//                    .into(viewHolder.ivCoverPhoto);
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
        public ImageView ivCoverPhoto;

        public ViewHolder(View view) {
            super(view);
            tvGroupName = view.findViewById(R.id.tvGroupName);
            ivCoverPhoto = view.findViewById(R.id.ivCoverPhoto);

            view.setOnClickListener(this);
        }

        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
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
