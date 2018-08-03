package com.example.arafatm.anti_socialmedia.Util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.arafatm.anti_socialmedia.R;

import java.util.ArrayList;
public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder> {
    private Context context;
    private ArrayList<String> pictureLIst;

    public PictureAdapter(Context context, ArrayList<String> List) {
        this.pictureLIst = List;
        this.context = context;
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
                .apply(RequestOptions.circleCropTransform())
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

                //TODO: GETS PITCURE AND USE IT TO CREATE POST

            }
        }
    }
}
