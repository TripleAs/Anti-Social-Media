package com.example.arafatm.anti_socialmedia.Util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.arafatm.anti_socialmedia.Fragments.EditNicknameFragment;
import com.example.arafatm.anti_socialmedia.Fragments.ProfileFragment;
import com.example.arafatm.anti_socialmedia.R;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ParseUser> members;
    private HashMap<String, String> nicknamesDict;
    private FragmentManager fragmentManager;
    private Fragment fragment;

    public MemberAdapter(ArrayList<ParseUser> List, HashMap<String, String> dict, Fragment frag, FragmentManager fm) {
        this.members = List;
        this.nicknamesDict = dict;
        this.fragment = frag;
        this.fragmentManager = fm;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View groupView = inflater.inflate(R.layout.item_member, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(groupView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        ParseUser member = members.get(position);
        String name = member.getString("fullName");
        viewHolder.tvFullName.setText(name);

        String objectId = member.getObjectId();
        if (nicknamesDict != null) {
            String nickname = nicknamesDict.get(objectId);
            if (nickname != null) {
                viewHolder.tvNickname.setText(nickname);
                viewHolder.workingName = nickname;
            } else {
                viewHolder.tvNickname.setText(name);
                viewHolder.workingName = name;
            }
        } else {
            viewHolder.tvNickname.setText(name);
            viewHolder.workingName = name;
        }

        PhotoHelper.displayPropic(member, viewHolder.ivPropic, context);
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvNickname;
        public TextView tvFullName;
        public ImageView ivPropic;
        public ImageButton ibEdit;
        public String workingName;

        public ViewHolder(View view) {
            super(view);
            tvNickname = view.findViewById(R.id.tvNickname);
            tvFullName = view.findViewById(R.id.tvFullName);
            ivPropic = view.findViewById(R.id.ivPropic);
            ibEdit = view.findViewById(R.id.ibEdit);
            view.setOnClickListener(this);
            ibEdit.setOnClickListener(this);
            tvFullName.setOnClickListener(this);
            ivPropic.setOnClickListener(this);
        }

        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                ParseUser member = members.get(position);
                if (view.getId() == tvFullName.getId() || view.getId() == ivPropic.getId()) {
                    openProfile(member);
                } else if (view.getId() == ibEdit.getId()) {
                    openEditDialog(workingName, member, position);
                }
            }
        }
    }

    private void openProfile(ParseUser friend) {
        ProfileFragment profileFragment = ProfileFragment.newInstance(friend);
        fragmentManager.beginTransaction()
                .replace(R.id.layout_child_activity, profileFragment).addToBackStack(null)
                .commit();
    }

    private void openEditDialog(String name, ParseUser user, int position) {
        EditNicknameFragment editNicknameFragment = EditNicknameFragment.newInstance(name, user, position);
        editNicknameFragment.setTargetFragment(fragment, 2);
        editNicknameFragment.show(fragmentManager, "fragment_edit_nickname");
    }
}
