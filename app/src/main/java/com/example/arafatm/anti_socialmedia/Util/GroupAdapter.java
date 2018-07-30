package com.example.arafatm.anti_socialmedia.Util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.arafatm.anti_socialmedia.Models.Group;
import com.example.arafatm.anti_socialmedia.R;
import com.parse.ParseFile;

import java.util.ArrayList;
import java.util.List;

public class GroupAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Group> groupList;

    public GroupAdapter(Context context, ArrayList<Group> List) {
        mContext = context;
        this.groupList = List;
    }

    @Override
    public int getCount() {
        return groupList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ImageView imageView;
        if (view == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(250, 250);
            imageView.setLayoutParams(params);
        } else {
            imageView = (ImageView) view;
        }


        ParseFile groupImage = groupList.get(position).getParseFile("groupImage");

        if (groupImage != null) {
            /*shows group image on gridView*/
            Glide.with(viewGroup.getContext())
                    .load(groupImage.getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.ic_group_default);
        }
        return imageView;
    }

    // Add a list of items -- change to type used
    public void addAll(List<Group> list) {
        groupList.addAll(list);
        notifyDataSetChanged();
    }
}
