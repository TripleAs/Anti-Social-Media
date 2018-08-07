package com.example.arafatm.anti_socialmedia.Util;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.arafatm.anti_socialmedia.Fragments.GroupFeedFragment;
import com.example.arafatm.anti_socialmedia.Fragments.UploadedImages;
import com.example.arafatm.anti_socialmedia.Models.Group;
import com.example.arafatm.anti_socialmedia.R;

import java.util.ArrayList;
public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder> {
    private Context context;
    private ArrayList<String> pictureLIst;
    private FragmentManager fragmentManager;
    private Group currentGroup;
    UploadedImages uploadedImages;

    public PictureAdapter(Context context, ArrayList<String> List, Group currentGroup, FragmentManager fm, UploadedImages uploadedImages) {
        this.pictureLIst = List;
        this.context = context;
        this.currentGroup = currentGroup;
        this.fragmentManager = fm;
        this.uploadedImages = uploadedImages;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View pictureView = inflater.inflate(R.layout.picture_item, viewGroup, false);
        // Return a new holder instance
        PictureAdapter.ViewHolder viewHolder = new PictureAdapter.ViewHolder(pictureView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String iMageURL = pictureLIst.get(i);
        Glide.with(context)
                .load(iMageURL)
                .into(viewHolder.instgramPhoto);
    }

    @Override
    public int getItemCount() {
        return pictureLIst.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView instgramPhoto;

        public ViewHolder(View view) {
            super(view);
            instgramPhoto = view.findViewById(R.id.iv_image);
            view.setOnClickListener(this);
        }

        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Toast.makeText(context, Integer.toString(position), Toast.LENGTH_SHORT).show();
                //create a bundle to store the url
                Bundle bundle = new Bundle();
                bundle.putString("imageURL", pictureLIst.get(position));

                GroupFeedFragment.goToPost = true;
                // come back after lunch!
                Fragment groupFeedFragment = new GroupFeedFragment();
                groupFeedFragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentManager.beginTransaction().replace(R.id.preview_frame, groupFeedFragment).addToBackStack(null).commit();
                uploadedImages.dismiss();
            }
        }
    }
}