package com.example.arafatm.anti_socialmedia.Util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.arafatm.anti_socialmedia.R;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

public class GroupAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<ParseObject> groupList;

    public GroupAdapter(Context context, ArrayList<ParseObject> List) {
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
        } else {
            imageView = (ImageView) view;
        }

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(200, 200);
        imageView.setLayoutParams(params);

        imageView.setBackgroundResource(R.drawable.round_coner_of_each_image);

        // imageView.setImageResource(mThumbIds[position]);
        Glide.with(viewGroup.getContext())
                .load(groupList.get(position).getParseFile("groupImage").getUrl())
                .into(imageView);
        return imageView;
    }

    // Add a list of items -- change to type used
    public void addAll(List<ParseObject> list) {
        groupList.addAll(list);
        notifyDataSetChanged();
    }
}
