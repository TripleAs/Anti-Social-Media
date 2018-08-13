package com.example.arafatm.anti_socialmedia.Util;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.transition.AutoTransition;
import android.support.transition.Explode;
import android.support.transition.Fade;
import android.support.transition.Slide;
import android.support.transition.Transition;
import android.support.transition.TransitionInflater;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
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
import com.example.arafatm.anti_socialmedia.Fragments.GroupManagerFragment;
import com.example.arafatm.anti_socialmedia.Models.Group;
import com.example.arafatm.anti_socialmedia.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import static com.example.arafatm.anti_socialmedia.Fragments.GroupCustomizationFragment.KEY_BLUE;
import static com.example.arafatm.anti_socialmedia.Fragments.GroupCustomizationFragment.KEY_GREEN;
import static com.example.arafatm.anti_socialmedia.Fragments.GroupCustomizationFragment.KEY_RED;

public class GroupManagerAdapter extends RecyclerView.Adapter<GroupManagerAdapter.ViewHolder> {
    private Context context;
    public ArrayList<Group> groups;
    private FragmentManager fragmentManager;
    public int currentGroupPosition;
    private GroupManagerFragment managerFragment;

    public GroupManagerAdapter(ArrayList<Group> List, FragmentManager fm, GroupManagerFragment fragment) {
        groups = List;
        fragmentManager = fm;
        managerFragment = fragment;
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
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        final Group group = groups.get(position);
        String groupName = group.getGroupName();

        final String picTransitionName = "coverPhoto" + group.getObjectId();
        ViewCompat.setTransitionName(viewHolder.ivCoverPhoto, picTransitionName);
        final String textTransitionName = "groupName" + group.getObjectId();
        ViewCompat.setTransitionName(viewHolder.tvGroupName, textTransitionName);


        if (groupName != null) {
            viewHolder.tvGroupName.setText(groupName);
        } else {
            viewHolder.tvGroupName.setText("");
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

        String theme = group.getTheme();
        switch (theme) {
            case KEY_RED:
                viewHolder.ivBorder.setColorFilter(ContextCompat.getColor(context,
                        R.color.red_gradient_1));
                break;
            case KEY_GREEN:
                viewHolder.ivBorder.setColorFilter(ContextCompat.getColor(context,
                        R.color.green_gradient_2));
                break;
            case KEY_BLUE:
                viewHolder.ivBorder.setColorFilter(ContextCompat.getColor(context,
                        R.color.blue_gradient_2));
                break;
        }

        viewHolder.tvGroupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentGroupPosition = viewHolder.getAdapterPosition();
                Fragment feedFragment = GroupFeedFragment.newInstance(group.getObjectId(), group.getTheme());
                managerToFeedTransition(managerFragment, feedFragment, viewHolder.ivCoverPhoto,
                        picTransitionName, viewHolder.tvGroupName, textTransitionName);
            }
        });

        viewHolder.ivCoverPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentGroupPosition = viewHolder.getAdapterPosition();
                Fragment feedFragment = GroupFeedFragment.newInstance(group.getObjectId(), group.getTheme());
                managerToFeedTransition(managerFragment, feedFragment, viewHolder.ivCoverPhoto,
                        picTransitionName, viewHolder.tvGroupName, textTransitionName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvGroupName;
        public ImageView ivCoverPhoto;
        public ImageView ivBorder;
        public int position;

        public ViewHolder(View view) {
            super(view);
            tvGroupName = view.findViewById(R.id.tvGroupName);
            ivCoverPhoto = view.findViewById(R.id.ivCoverPhoto);
            ivBorder = view.findViewById(R.id.borderDrawable);
        }
    }

    private void managerToFeedTransition(Fragment managerFragment, Fragment feedFragment,
                                         ImageView ivCoverPhoto, String picTransitionName,
                                         TextView tvGroupName, String textTransitionName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Setup exit transition on first fragment
            managerFragment.setSharedElementReturnTransition(new AutoTransition());
            managerFragment.setExitTransition(new Fade());

            // Setup enter transition on second fragment
            feedFragment.setSharedElementEnterTransition(new AutoTransition());
            feedFragment.setEnterTransition(new Fade());

            // Find the shared element (in Fragment A)

            // Add second fragment by replacing first
            FragmentTransaction ft = fragmentManager.beginTransaction()
                    .replace(R.id.layout_child_activity, feedFragment)
                    .addToBackStack("transaction")
                    .addSharedElement(tvGroupName, textTransitionName)
                    .addSharedElement(ivCoverPhoto, picTransitionName);
            // Apply the transaction
            ft.commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.layout_child_activity, feedFragment)
                    .addToBackStack(null).commit();
        }
    }
}
