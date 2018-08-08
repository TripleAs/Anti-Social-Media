package com.example.arafatm.anti_socialmedia.Util;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.arafatm.anti_socialmedia.Fragments.UploadedImages;
import com.example.arafatm.anti_socialmedia.Models.Group;

import java.util.ArrayList;

public class PictureAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> pictureLIst;
    private FragmentManager fragmentManager;
    private Group currentGroup;
    UploadedImages uploadedImages;
    private  int position;


    public PictureAdapter(Context context, ArrayList<String> List, Group currentGroup, FragmentManager fm, UploadedImages uploadedImages) {
        this.pictureLIst = List;
        this.context = context;
        this.currentGroup = currentGroup;
        this.fragmentManager = fm;
        this.uploadedImages = uploadedImages;
    }

    public int getCount() {
        return pictureLIst.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        position = position;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(200, 200));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        String iMageURL = pictureLIst.get(position);
        Glide.with(context)
                .load(iMageURL)
                .into(imageView);

        return imageView;
    }
}
