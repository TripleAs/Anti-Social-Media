package com.example.arafatm.anti_socialmedia.Util;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.arafatm.anti_socialmedia.Models.Post;
import com.example.arafatm.anti_socialmedia.R;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{
    private List<Post> mComments;
    Context context;

    public CommentAdapter(List<Post> comments){
        mComments = comments;
    }

    //create ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvUserComment;
        public TextView tvBodyComment;

        public ViewHolder(View itemView) {
            super(itemView);

            //perform findViewBtId lookups
            tvUserComment = itemView.findViewById(R.id.tvCommentUser);
            tvBodyComment = itemView.findViewById(R.id.tvCommentBody);

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View commentView = inflater.inflate(R.layout.item_comment, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(commentView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Post comment_Post = null;
        ParseUser parseUser = null;

        try {
            comment_Post = mComments.get(i).fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            parseUser = comment_Post.getSender().fetchIfNeeded();

        }
        catch (ParseException e){
            e.printStackTrace();
        }

        String user = parseUser.getString("fullName");
        String body = comment_Post.getComment();

        // need to add "message" instead of "comment" for first post
        if (i==0){
            body = comment_Post.getString("message");

            Typeface tf = Typeface.defaultFromStyle(Typeface.BOLD);
            viewHolder.tvBodyComment.setTypeface(tf);
            viewHolder.tvUserComment.setTypeface(tf);
        }

        viewHolder.tvBodyComment.setText(body);
        viewHolder.tvUserComment.setText(user);
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public void clear() {
        mComments.clear();
        notifyDataSetChanged();
    }
}
