package com.example.arafatm.anti_socialmedia.Util;

import android.content.Context;
import android.net.Uri;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.arafatm.anti_socialmedia.Fragments.CommentFragment;
import com.example.arafatm.anti_socialmedia.Models.Post;
import com.example.arafatm.anti_socialmedia.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{
    private List<Post> mPosts;
    Context context;
    FragmentManager manager;
    HashMap<String, String> nicknames;

    public PostAdapter(FragmentManager m, Context c, List<Post> posts, HashMap<String, String> hashMap){
        manager = m;
        context = c;
        mPosts = posts;
        nicknames = hashMap;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void navigate_to_fragment(Fragment fragment);
    }

//    public void onViewProfileSelected() {
//        ProfileFragment profileFragment = ProfileFragment.newInstance(ParseUser.getCurrentUser());
//        navigate_to_fragment(profileFragment);
//    }
//    public static ProfileFragment newInstance(ParseUser user) {
//        ProfileFragment profileFragment = new ProfileFragment();
//        Bundle args = new Bundle();
//        args.putParcelable(ParseUser.class.getSimpleName(), Parcels.wrap(user));
//        profileFragment.setArguments(args);
//        return profileFragment;
//    }

    //create ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvFullNameFeed) TextView tvFullName;
        @BindView(R.id.tvPostBody) TextView tvPostText;
        @BindView(R.id.ivProPicPost) ImageView ivPostPic;
        @BindView(R.id.tvNumberOfComments) TextView tvNumberComments;           //comment
        @BindView(R.id.btCommentIcon) ImageButton btCommentExpand;              //comment
        @BindView(R.id.ivImagePost) ImageView imagePost;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View postView = inflater.inflate(R.layout.item_feed, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder viewHolder, int position) {
        // get the data according to this position
        final Post post = mPosts.get(position);
        ParseUser sender = null;

        //added this because debugger asked us to
        try {
            sender = post.getSender().fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String message = post.getMessage();
        String senderName = sender.getString("fullName");
        String number = Integer.toString(post.getCommentsCount());

        String objectId = sender.getObjectId();
        if (nicknames != null) {
            String nickname = nicknames.get(objectId);
            if (nickname != null) {
                viewHolder.tvFullName.setText(nickname);
            } else {
                viewHolder.tvFullName.setText(senderName);
            }
        } else {
            viewHolder.tvFullName.setText(senderName);
        }

        viewHolder.tvPostText.setText(message);
        viewHolder.tvNumberComments.setText(number);

        //picture with post
        if (post.getImage() != null) {
            Glide.with(context)
                    .load(post.getImage().getUrl())
                    .into(viewHolder.imagePost);
        }

        PhotoHelper.displayPropic(sender, viewHolder.ivPostPic, context);

        //goes to the comment fragment
        final ImageButton commentExpandButton = viewHolder.btCommentExpand;
        commentExpandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommentFragment commentFragment = CommentFragment.newInstance(post);     //need a method to remember the post from previous screen
                manager.beginTransaction()
                        .replace(R.id.layout_child_activity, commentFragment)
                        .commit();
            }
        });

    }


    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        mPosts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        mPosts.addAll(list);
        notifyDataSetChanged();
    }
}
